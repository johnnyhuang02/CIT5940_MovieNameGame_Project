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
        assertTrue(genres.contains("Action"));
        assertTrue(genres.contains("Adventure"));
        assertTrue(genres.contains("Fantasy"));
        assertTrue(genres.contains("Science Fiction"));

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
        assertFalse("There should be at least one movie called Avatar", avatars.isEmpty());

        Movie avatar = avatars.get(0);
        // Check the year
        assertEquals(2009, avatar.getYear());

        // Check that Sam Worthington appears in the cast
        boolean hasSam = avatar.getCast().stream()
                .anyMatch(p -> "Sam Worthington".equals(p.getName()));
        assertTrue("Cast should contain Sam Worthington", hasSam);

        // testing use
        List<Person> crew = avatar.getCrew();
//        System.out.println(crew.size());
        List<Person> cast = avatar.getCast();
//        System.out.println(cast.size());
        assertNotNull(cast);
        assertNotNull(crew);
    }



}
