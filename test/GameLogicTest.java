import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class GameLogicTest {
    private Movie movie1, movie2, movie3;
    private Player player;
    private Validator validator;

    @Before
    public void setUp() {
        List<Person> castA = Arrays.asList(new Person("Tom Hanks", "actor"));
        List<Person> crewA = Arrays.asList(new Person("Steven Spielberg", "director"));

        List<Person> castB = Arrays.asList(new Person("Tom Hanks", "actor"));
        List<Person> crewB = Arrays.asList(new Person("James Cameron", "director"));

        List<Person> castC = Arrays.asList(new Person("Meryl Streep", "actor"));
        List<Person> crewC = Arrays.asList(new Person("Steven Spielberg", "director"));

        movie1 = new Movie("Movie One", 1995, Arrays.asList("drama", "history"), castA, crewA);
        movie2 = new Movie("Movie Two", 2000, Arrays.asList("drama"), castB, crewB);
        movie3 = new Movie("Movie Three", 1995, Arrays.asList("comedy"), castC, crewC);

        player = new Player("Alice", "genre:drama");
        validator = new Validator();
    }

    @Test
    public void testConnectedByGenre() {
        assertTrue(movie1.isConnectedByGenre(movie2));
        assertFalse(movie1.isConnectedByGenre(movie3));
    }

    @Test
    public void testConnectedByCast() {
        assertTrue(movie1.isConnectedByCast(movie2));
        assertFalse(movie1.isConnectedByCast(movie3));
    }

    @Test
    public void testConnectedByCrew() {
        assertTrue(movie1.isConnectedByCrew(movie3));
        assertFalse(movie2.isConnectedByCrew(movie3));
    }

    @Test
    public void testConnectedByYear() {
        assertTrue(movie1.isConnectedByYear(movie3));
        assertFalse(movie1.isConnectedByYear(movie2));
    }

    @Test
    public void testValidatorValidMove() {
        assertTrue(validator.isValidMovie(movie1, movie2, "cast"));
        assertFalse(validator.isValidMovie(movie1, movie3, "cast"));
    }

    @Test
    public void testPlayerWinCondition() {
        assertTrue(validator.isWinConditionMet(player, movie1));
        assertFalse(validator.isWinConditionMet(player, movie3));
    }
}
