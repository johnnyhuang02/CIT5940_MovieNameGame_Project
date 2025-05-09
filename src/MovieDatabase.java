import java.util.ArrayList;
import java.util.List;

public class MovieDatabase {
    private List<Movie> movieList;
    private List<String> movieNames;

    public void loadFromCSV(String path) {
        // load all name into movieName
    }

    public Movie getMovieByTitle(String title) {
    	for (Movie m : movieList) {
            if (m.getTitle().equalsIgnoreCase(title)) {
                return m;
            }
        }
        return null;
    }

    // using autocomplete to generate suggestions,
    // using movieNames
    public List<String> getSuggestions(String prefix) {

    	List<String> result = new ArrayList<>();
        for (String name : movieNames) {
            if (name.toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(name);
            }
        }
        return result;
    }


    public List<Movie> getAllMovies() {
        return null;
    }

}