import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

public class MovieTest {
    private Movie movie1;
    private Movie movie2;
    private Movie movie3;
    private Person actorA;
    private Person actorB;
    private Person directorA;
    private Person directorB;

    @Before
    public void setUp() {
        // create Person instances for cast and crew
        actorA = new Person("Tom Hanks", "actor");
        actorB = new Person("Meryl Streep", "actor");
        directorA = new Person("Steven Spielberg", "director");
        directorB = new Person("James Cameron", "director");

        // movie1 and movie2 share genre "drama" and actorA
        movie1 = new Movie(
            "Movie One",
            1995,
            Arrays.asList("drama", "history"),
            Arrays.asList(actorA),
            Arrays.asList(directorA)
        );

        movie2 = new Movie(
            "Movie Two",
            2000,
            Arrays.asList("drama", "action"),
            Arrays.asList(actorA),
            Arrays.asList(directorB)
        );

        // movie3 shares release year 1995 and directorA with movie1
        movie3 = new Movie(
            "Movie Three",
            1995,
            Arrays.asList("comedy"),
            Arrays.asList(actorB),
            Arrays.asList(directorA)
        );
    }

    // Test that two movies sharing a genre are connected by genre
    @Test
    public void testConnectedByGenreTrue() {
        assertTrue(movie1.isConnectedByGenre(movie2));
    }

    // Test that two movies without a common genre are not connected by genre
    @Test
    public void testConnectedByGenreFalse() {
        assertFalse(movie1.isConnectedByGenre(movie3));
    }

    // Test that two movies sharing an actor are connected by cast
    @Test
    public void testConnectedByCastTrue() {
        assertTrue(movie1.isConnectedByCast(movie2));
    }

    // Test that two movies without a common actor are not connected by cast
    @Test
    public void testConnectedByCastFalse() {
        assertFalse(movie1.isConnectedByCast(movie3));
    }

    // Test that two movies sharing a crew member are connected by crew
    @Test
    public void testConnectedByCrewTrue() {
        assertTrue(movie1.isConnectedByCrew(movie3));
    }

    // Test that two movies without a common crew member are not connected by crew
    @Test
    public void testConnectedByCrewFalse() {
        assertFalse(movie2.isConnectedByCrew(movie3));
    }

    // Test that two movies from the same year are connected by year
    @Test
    public void testConnectedByYearTrue() {
        assertTrue(movie1.isConnectedByYear(movie3));
    }

    // Test that two movies from different years are not connected by year
    @Test
    public void testConnectedByYearFalse() {
        assertFalse(movie1.isConnectedByYear(movie2));
    }

    // Test isConnectedTo only checks crew with matching role
    @Test
    public void testIsConnectedToDirectorTrue() {
        // movie1 and movie3 share directorA with role "director"
        assertTrue(movie1.isConnectedTo(movie3, "director"));
    }

    // Test isConnectedTo returns false for a crew type not shared
    @Test
    public void testIsConnectedToDirectorFalse() {
        // movie2 and movie3 share no "director" in movie2 crew
        assertFalse(movie2.isConnectedTo(movie3, "director"));
    }

    // Test isConnectedTo returns false for cast even if cast overlaps,
    // because current implementation only checks crew
    @Test
    public void testIsConnectedToCastFalse() {
        // movie1 and movie2 share actorA but isConnectedTo only checks crew
        assertFalse(movie1.isConnectedTo(movie2, "actor"));
    }

    // Test isConnectedTo returns false for unknown connection type
    @Test
    public void testIsConnectedToUnknownType() {
        assertFalse(movie1.isConnectedTo(movie2, "unknown"));
    }
}