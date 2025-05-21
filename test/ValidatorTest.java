import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;

public class ValidatorTest {
    private Validator validator;
    private Movie movieA;
    private Movie movieB;
    private Movie movieC;
    private Player player;

    @Before
    public void setUp() {
        validator = new Validator();

        // prepare Person instances
        Person actor1 = new Person("Actor One", "actor");
        Person actor2 = new Person("Actor Two", "actor");
        Person director1 = new Person("Director One", "director");

        // movieA and movieB share genre "drama" and actor1
        movieA = new Movie(
            "Movie A",
            2000,
            Arrays.asList("drama", "comedy"),
            Arrays.asList(actor1),
            Arrays.asList(director1)
        );
        movieB = new Movie(
            "Movie B",
            2001,
            Arrays.asList("drama", "action"),
            Arrays.asList(actor1),
            Arrays.asList(actor2) // actor2 in crew role for test
        );
        // movieC shares year with movieA but no genre or cast overlap
        movieC = new Movie(
            "Movie C",
            2000,
            Arrays.asList("horror"),
            Arrays.asList(actor2),
            Arrays.asList(director1)
        );

        // player with win condition on genre "drama"
        player = new Player("Test Player", "genre:drama");
    }

    // Test invalid genre connection
    @Test
    public void testInvalidByGenre() {
        assertFalse("Genre connection should be invalid", validator.isValidMovie(movieA, movieC, "genre"));
    }

    // Test valid connection by cast
    @Test
    public void testValidByCast() {
        assertTrue("Cast connection should be valid", validator.isValidMovie(movieA, movieB, "cast"));
    }

    // Test invalid cast connection
    @Test
    public void testInvalidByCast() {
        assertFalse("Cast connection should be invalid", validator.isValidMovie(movieA, movieC, "cast"));
    }

    // Test valid connection by crew
    @Test
    public void testValidByCrew() {
        assertTrue("Crew connection should be valid", validator.isValidMovie(movieA, movieC, "crew"));
    }

    // Test invalid crew connection
    @Test
    public void testInvalidByCrew() {
        assertFalse("Crew connection should be invalid", validator.isValidMovie(movieA, movieB, "crew"));
    }

    // Test valid connection by year
    @Test
    public void testValidByYear() {
        assertTrue("Year connection should be valid", validator.isValidMovie(movieA, movieC, "year"));
    }

    // Test invalid year connection
    @Test
    public void testInvalidByYear() {
        assertFalse("Year connection should be invalid", validator.isValidMovie(movieA, movieB, "year"));
    }

    // Test unknown connection type returns false
    @Test
    public void testUnknownConnectionType() {
        assertFalse("Unknown connection type should return false", validator.isValidMovie(movieA, movieB, "unknown"));
    }

    // Test isWinConditionMet for matching genre
    @Test
    public void testWinConditionMet() {
        assertTrue("Win condition should be met for genre 'drama'", validator.isWinConditionMet(player, movieA));
    }

    // Test isWinConditionMet for non-matching genre
    @Test
    public void testWinConditionNotMet() {
        assertFalse("Win condition should not be met for non-drama movie", validator.isWinConditionMet(player, movieC));
    }
}