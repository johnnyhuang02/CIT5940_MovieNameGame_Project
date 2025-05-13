import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDatabaseUnitTest {
    private MovieDatabase db;

    @Before
    public void setUp() throws Exception {
        db = new MovieDatabase();

        // Inject a small local index into moviesById
        Map<Integer, Movie> moviesById = new HashMap<>();
        moviesById.put(1, new Movie("Alpha", 2020,
                List.of("Action"), List.of(), List.of()));
        moviesById.put(2, new Movie("Beta", 2021,
                List.of("Drama"), List.of(), List.of()));

        Field moviesField = MovieDatabase.class.getDeclaredField("moviesById");
        moviesField.setAccessible(true);
        moviesField.set(db, moviesById);

        // Inject corresponding idsByTitle
        Map<String, List<Integer>> idsByTitle = new HashMap<>();
        idsByTitle.put("alpha", List.of(1));
        idsByTitle.put("beta",  List.of(2));

        Field idsField = MovieDatabase.class.getDeclaredField("idsByTitle");
        idsField.setAccessible(true);
        idsField.set(db, idsByTitle);
    }

    /**
     * Unit test for local lookup only.
     * Should return the Movie we injected, without touching CSV or API.
     */
    @Test
    public void testLocalLookupFound() throws Exception {
        List<Movie> result = db.getMovieByTitle("Alpha");
        assertEquals("Should return exactly one movie", 1, result.size());
        assertEquals("Title must match 'Alpha'", "Alpha", result.get(0).getTitle());
    }

    /**
     * Unit test for getSuggestions, mocking out AutoComplete.
     * We inject an AutoComplete subclass so that getSuggestions()
     * simply returns our terms.
     */
    @Test
    public void testGetSuggestionsUsesAutoComplete() throws Exception {
        // Create a dummy AutoComplete (subclass) that returns fixed terms
        AutoComplete dummy = new AutoComplete() {
            @Override
            public List<ITerm> getSuggestions(String prefix) {
                return List.of(new Term("foo", 10), new Term("bar", 5));
            }
        };

        Field autoField = MovieDatabase.class.getDeclaredField("auto");
        autoField.setAccessible(true);
        autoField.set(db, dummy);

        List<String> suggestions = db.getSuggestions("any");
        assertEquals("Should return two suggestions", 2, suggestions.size());
        assertEquals("First suggestion should be 'foo'", "foo", suggestions.get(0));
        assertEquals("Second suggestion should be 'bar'", "bar", suggestions.get(1));
    }

    /**
     * Test selectRandomMovie when moviesById is empty:
     * should return default Inception movie.
     */
    @Test
    public void testSelectRandomMovieEmpty() throws Exception {
        // Clear local index
        Field moviesField = MovieDatabase.class.getDeclaredField("moviesById");
        moviesField.setAccessible(true);
        moviesField.set(db, new HashMap<Integer, Movie>());

        Movie m = db.selectRandomMovie();
        assertEquals("Default title should be Inception", "Inception", m.getTitle());
        assertEquals("Default year should be 2010", 2010, m.getYear());
    }

    /**
     * Test selectRandomMovie when moviesById has entries:
     * should return one of the injected movies.
     */
    @Test
    public void testSelectRandomMovieNonEmpty() throws Exception {
        // Inject a known map with a single entry
        Map<Integer, Movie> moviesById = new HashMap<>();
        Movie custom = new Movie("Custom", 1999, List.of("X"), List.of(), List.of());
        moviesById.put(42, custom);

        Field moviesField = MovieDatabase.class.getDeclaredField("moviesById");
        moviesField.setAccessible(true);
        moviesField.set(db, moviesById);

        Movie picked = db.selectRandomMovie();
        assertEquals("Should return our custom movie", "Custom", picked.getTitle());
        assertEquals("Year should match injected", 1999, picked.getYear());
    }
}
