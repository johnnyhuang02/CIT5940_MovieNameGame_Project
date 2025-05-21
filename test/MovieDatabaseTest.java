import static org.junit.Assert.*;

import com.opencsv.exceptions.CsvValidationException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

public class MovieDatabaseTest {

    @Test
    public void testLoadFromCSV() throws CsvValidationException, IOException, InterruptedException {
        String moviesPath = "tmdb_5000_movies.csv";
        String creditsPath = "tmdb_5000_credits.csv";

        MovieDatabase db = new MovieDatabase();

        db.loadFromCSV(moviesPath, creditsPath);

        // try getSuggestions with prefix a, get at most size 10
        List<String> suggestions = db.getSuggestions("a");
        assertTrue(suggestions.size() <= 10);


        List<Movie> avatars = db.getMovieByTitle("Avatar");
        assertNotNull("getMovieByTitle should not return null", avatars);
        assertFalse("There should be at least one movie called Avatar", avatars.isEmpty());
        // check we store one movie object
        assertEquals(1, avatars.size());

        // get avatar movie object
        Movie avatar = avatars.get(0);

        // Check the year
        // need to be 2009
        assertEquals(2009, avatar.getYear());

        List<String> genres = avatar.getGenres();
        assertEquals(4, genres.size());
        assertTrue(genres.contains("action"));
        assertTrue(genres.contains("adventure"));
        assertTrue(genres.contains("fantasy"));
        assertTrue(genres.contains("science fiction"));

        // check crew, contain 153
        List<Person> crew = avatar.getCrew();
        assertEquals(153, crew.size());
        Person crew1 = crew.get(0);
        Person crew2 = crew.get(1);
        assertEquals("Stephen E. Rivkin", crew1.getName());
        assertEquals("Editor", crew1.getRole());
        assertEquals("Rick Carter", crew2.getName());
        assertEquals("Production Design", crew2.getRole());

    }


    @Test
    public void testLoadFromAPI_Avatar() throws IOException, InterruptedException {
        MovieDatabase db = new MovieDatabase();

        List<Movie> avatars = db.getMovieByTitle("Avatar");
        assertNotNull("getMovieByTitle should not return null", avatars);
        assertFalse("There should be at least one movie", avatars.isEmpty());
        List<String> suggest = db.getSuggestions("a");
        assertTrue(suggest.size() <= 10);
        assertEquals("avatar", db.getSuggestions("a").get(0));

        Movie avatar = avatars.get(0);
//        for (String genre : avatar.getGenres()) {
//            System.out.println(genre);
//        }
        // Check the year
        assertEquals(2009, avatar.getYear());
//
//        // Check that Sam Worthington appears in the cast
        boolean hasSam = avatar.getCast().stream()
                .anyMatch(p -> "Sam Worthington".equals(p.getName()));
        assertTrue("Cast should contain Sam Worthington", hasSam);

        // testing use
        List<Person> crew = avatar.getCrew();
//        System.out.println(crew.size());
        List<Person> cast = avatar.getCast();
        System.out.println(cast.size());
        assertNotNull(cast);
        assertNotNull(crew);

        List<Movie> movie2 = db.getMovieByTitle("What Lies Beneath");
        List<String> suggest2 = db.getSuggestions("a");
        System.out.println(suggest.size());
        System.out.println(suggest.get(0));


    }

    @Test
    public void testGetRandomMovie() throws CsvValidationException, IOException, InterruptedException {
        String moviesPath = "tmdb_5000_movies.csv";
        String creditsPath = "tmdb_5000_credits.csv";

        MovieDatabase db = new MovieDatabase();

        // generate a random movie
        db.loadFromCSV(moviesPath, creditsPath);
        Movie randomMovie = db.selectRandomMovie();
        assertNotNull(randomMovie);
        System.out.println(randomMovie.getTitle());

        // generate another random movie
        Movie anotherRandomMovie = db.selectRandomMovie();
        assertNotNull(anotherRandomMovie);

        // two random movies are likely not the same
        assertNotEquals(randomMovie.getTitle(), anotherRandomMovie.getTitle());
    }


}
