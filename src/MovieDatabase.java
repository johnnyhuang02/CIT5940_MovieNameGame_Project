import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.util.stream.Collectors;

public class MovieDatabase {

    // primary index: MovieID -> Movie object
    private Map<Integer, Movie> moviesById = new HashMap<>();
    // secondary index: title -> list of MovieIDs with this title
    private final Map<String,List<Integer>> idsByTitle = new HashMap<>();

    // TMDB API constants
    private static final String API_KEY  = "d75c504554a673411620508c4dca1583";
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private final HttpClient http = HttpClient.newHttpClient();

    private Set<String> genreSet = new HashSet<>();
    // private autocomplete stored used for Autocomplete suggestions
    private AutoComplete auto = new AutoComplete();

    // debug usage
//    private int errorCounts = 0;
//    public int getCount() {
//        return errorCounts;
//    }

    // --------------- CSV Loader ---------------

    /**
     * Load from the two TMDB CSVs:
     *  - moviesCsvPath:   tmdb_5000_movies.csv
     *  - creditsCsvPath:  tmdb_5000_credits.csv
     */
    public void loadFromCSV(String moviesCsvPath,
                            String creditsCsvPath)
            throws IOException, CsvValidationException {

        // create Movie object
        // load the "tmdb_5000_credits.csv" for cast, crew
        loadCredits(creditsCsvPath);

        // load the "tmdb_5000_movies.csv" for genre, year
        loadMetadata(moviesCsvPath);

        for (Map.Entry<String, List<Integer>> entry : idsByTitle.entrySet()) {
            String titleKey = entry.getKey();
            long weight = entry.getValue().size();
            auto.addWord(titleKey, weight);
        }
    }

    // private helper function 1:
    // load the "tmdb_5000_credits.csv" for cast, crew
    private void loadCredits(String path)
            throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] header = reader.readNext();
            Map<String,Integer> idx = buildIndex(header);

            int rowNum = 0;
            while (true) {
                String[] nextRecord;
                try {
                    nextRecord = reader.readNext();
                    if (nextRecord == null) {
                        break;
                    }

                    int movieId = Integer.parseInt(nextRecord[idx.get("movie_id")].trim());
                    String title = nextRecord[idx.get("title")].trim();

                    // get the cast and crew from record
                    List<Person> cast;
                    List<Person> crew;
                    try {
                        cast = parseCast(nextRecord[idx.get("cast")], movieId);
                        crew = parseCrew(nextRecord[idx.get("crew")], movieId);
                    } catch (Exception e) {
                        // skip storing this Movie object if error in loading cast of crew
                        continue;
                    }

                    // create new Movie with empty genre/year
                    Movie movie = new Movie(title, 0, new ArrayList<>(), cast, crew);
                    moviesById.put(movieId, movie);
                    indexTitle(title, movieId);
//                    errorCounts += 1;

                } catch (CsvValidationException e) {
//                    System.err.println(rowNum + " skipping malformed CSV line due to unterminated quoted field");
//                    e.printStackTrace();
                    continue;
                    // continue with next line
                } catch (Exception e) {
//                    System.err.println("skipping malformed record: possible parsing issue.");
//                    e.printStackTrace();
                    continue;
                }
                rowNum += 1;
            }
        }
    }

    // private helper function 2:
    // load from "tmdb_5000_movies.csv" for genre, year
    private void loadMetadata(String path)
            throws IOException, CsvValidationException {
        // build a reader around it
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] header = reader.readNext();
            Map<String,Integer> idx = buildIndex(header);

            // read each row by name
            String[] row;
            while ((row = reader.readNext()) != null) {
                try {
                    int id = Integer.parseInt(row[idx.get("id")].trim());
                    Movie m = moviesById.get(id);
                    if (m == null) {
                        continue;
                    }

                    // get the title
                    String title = row[idx.get("title")].trim();

                    // cast first four index as year
                    String date = row[idx.get("release_date")].trim();
                    int year = 0;
                    if (date.length() >= 4) {
                        try {
                            year = Integer.parseInt(date.substring(0, 4));
                        } catch (NumberFormatException ignored) {
//                            System.err.println("error in loading year for MovieId: " + id);
                            continue;
                        }
                    }

                    // get list of genres
                    List<String> genres = parseGenreNames(row[idx.get("genres")]);
                    genreSet.addAll(genres);
                    // set year and genres
                    m.setYear(year);
                    m.setGenres(genres);

                } catch (Exception e) {
//                    System.err.println("Skipping malformed row: " + Arrays.toString(row));
//                    e.printStackTrace();
                }
            }
        }
    }

    // --------------- API Loader ---------------
    private List<Movie> loadFromAPI(String title) throws IOException, InterruptedException {
        List<Movie> out = new ArrayList<>();

        // grab every matching ID
        List<Integer> ids = searchMovieIds(title);
        for (int id : ids) {
            // fetch details + credits
            Movie m = fetchFullMovie(id);
            // cache
            moviesById.put(id, m);
            genreSet.addAll(m.getGenres());
            indexTitle(m.getTitle(), id);
            auto.addWord(m.getTitle().toLowerCase(), 1);
            out.add(m);
        }

        return out;

    }

    // search TMDB for movies matching a title, return their numeric IDs
    // falls back to an empty list on any non-200 HTTP response
    private List<Integer> searchMovieIds(String title)
            throws IOException, InterruptedException {
        String url = String.format(
                "%s/search/movie?api_key=%s&query=%s",
                BASE_URL,
                API_KEY,
                URLEncoder.encode(title, StandardCharsets.UTF_8)
        );
        // http GET request
        HttpResponse<String> resp = http.send(
                HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        if (resp.statusCode() != 200) {
            return Collections.emptyList();
        }

        // Parse the JSON array of results, extracting the "id" field from each object
        JsonArray results = JsonParser
                .parseString(resp.body())
                .getAsJsonObject()
                .getAsJsonArray("results");

        List<Integer> ids = new ArrayList<>();
        for (var el : results) {
            ids.add(el.getAsJsonObject().get("id").getAsInt());
        }
        return ids;
    }

    // get the full movie record for a given ID
    // build and return a Movie instance that includes title, year, genres, cast, and crew
    private Movie fetchFullMovie(int id) throws IOException, InterruptedException {
        String url = String.format(
                "%s/movie/%d?api_key=%s&append_to_response=credits",
                BASE_URL, id, API_KEY
        );
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        JsonObject root       = JsonParser.parseString(resp.body()).getAsJsonObject();

        String title          = root.get("title").getAsString();
        String releaseDate    = root.get("release_date").getAsString();
        int year              = releaseDate.length() >= 4
                ? Integer.parseInt(releaseDate.substring(0,4))
                : 0;

        // genres
        List<String> genres = new ArrayList<>();
        for (var g : root.getAsJsonArray("genres")) {
            genres.add(g.getAsJsonObject().get("name").getAsString().toLowerCase());
        }

        // cast
        List<Person> cast = new ArrayList<>();
        for (var c : root.getAsJsonObject("credits").getAsJsonArray("cast")) {
            String name = c.getAsJsonObject().get("name").getAsString();
            cast.add(new Person(name, "cast"));
        }

        // crew
        List<Person> crew = new ArrayList<>();
        for (var c : root.getAsJsonObject("credits").getAsJsonArray("crew")) {
            var obj = c.getAsJsonObject();
            String name = obj.get("name").getAsString();
            String job  = obj.get("job").getAsString();
            crew.add(new Person(name, job));
        }

        return new Movie(title, year, genres, cast, crew);
    }

    // --------------- public function calls -------------------------

    // return a List<Movie> with this title
    public List<Movie> getMovieByTitle(String title)
            throws IOException, InterruptedException {
        List<Movie> local = localLookup(title);
        if (!local.isEmpty()) {
            return local;
        }
        return loadFromAPI(title);
    }

    // exact match lookup by title from local map/cache
    private List<Movie> localLookup(String title) {
        List<Integer> ids = idsByTitle.getOrDefault(
                title.toLowerCase(), Collections.emptyList());
        List<Movie> result = new ArrayList<>(ids.size());
        for (int id : ids) {
            Movie m = moviesById.get(id);
            if (m != null) result.add(m);
        }
        return result;
    }

    // getSuggestions(String prefix)
    public List<String> getSuggestions(String prefix) {
        List<ITerm> suggestions = auto.getSuggestions(prefix);
        List<String> titleSuggestion = new ArrayList<>();
        for (int i = 0; i < suggestions.size(); i++) {
            titleSuggestion.add(suggestions.get(i).getTerm());
        }
        return titleSuggestion;
    }

    // get the genreSet()
    public Set<String> getGenreSet() {
        return genreSet;
    }

    // pick a random
    public Movie selectRandomMovie() {
        List<Integer> allMovieIds = new ArrayList<>(moviesById.keySet());
        // if allMovieId is empty, return default Inception
        if (allMovieIds.isEmpty()) {
            return new Movie(
                    "Inception",
                    2010,
                    Arrays.asList("sci-fi", "thriller"),
                    new ArrayList<>(),
                    new ArrayList<>());
        }

        Random random = new Random();
        int randomIndex = random.nextInt(allMovieIds.size());
        Integer chosenId = allMovieIds.get(randomIndex);
        return moviesById.get(chosenId);
    }

    // --------------- helper functions -------------------------

    private void indexTitle(String title, int id) {
        String key = title.toLowerCase();
        idsByTitle.computeIfAbsent(key, k -> new ArrayList<>()).add(id);
    }

    private Map<String,Integer> buildIndex(String[] header) {
        Map<String,Integer> idx = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            idx.put(header[i], i);
        }
        return idx;
    }

    private List<String> parseGenreNames(String json) {
        List<String> names = new ArrayList<>();

        String fixed = json.trim().replace("'", "\"");

        try {
            JsonArray arr = JsonParser.parseString(fixed).getAsJsonArray();
            for (var el : arr) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("name")) {
                    names.add(obj.get("name").getAsString().toLowerCase());
                }
            }
        } catch (Exception e) {
            // malformed JSON -> just return empty genres list
        }
//        genreSet.addAll(names);
        return names;
    }

    // parse the cast json string into List<Person>
    private List<Person> parseCast(String json, int movieId) {
        List<Person> cast = new ArrayList<>();
        String fixed = json.replaceAll("\"\"", "");

        JsonArray arr = JsonParser.parseString(fixed).getAsJsonArray();
        for (var el : arr) {
            JsonObject obj = el.getAsJsonObject();
            if (obj.has("name")) {
                cast.add(new Person(obj.get("name").getAsString(), "cast"));
            }
        }
        return cast;
    }

    // parse the crew json string into List<Person>
    private List<Person> parseCrew(String json, int movieId) {
        String fixed = json.replace("\"\"", "");
        List<Person> crew = new ArrayList<>();

        JsonArray arr = JsonParser.parseString(fixed).getAsJsonArray();
        for (var el : arr) {
            JsonObject obj = el.getAsJsonObject();
            if (obj.has("name") && obj.has("job")) {
                crew.add(new Person(obj.get("name").getAsString(), obj.get("job").getAsString()));
            }
        }
        return crew;
    }


}