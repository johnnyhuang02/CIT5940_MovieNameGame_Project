import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;

public class PlayerTest {
    private Player player;
    private Movie matchingMovie;
    private Movie nonMatchingMovie;

    @Before
    public void setUp() {
        // Initialize a player with a win condition on genre "drama"
        player = new Player("Alice", "genre:drama");

        // Create a movie that matches the win condition
        matchingMovie = new Movie(
            "Dramatic Film",
            2021,
            Arrays.asList("drama", "comedy"),
            Collections.emptyList(),
            Collections.emptyList()
        );
        // Create a movie that does not match the win condition
        nonMatchingMovie = new Movie(
            "Action Movie",
            2022,
            Arrays.asList("action"),
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    // Test that getters return the correct initial values
    @Test
    public void testGetters() {
        assertEquals("Name should be 'Alice'", "Alice", player.getName());
        assertEquals("Score should be initialized to 0", 0, player.getScore());
        assertEquals("Win condition should be 'genre:drama'", "genre:drama", player.getWinCondition());
    }

    // Test incrementScore increases the score by one
    @Test
    public void testIncrementScore() {
        player.incrementScore();
        assertEquals("Score should increment to 1", 1, player.getScore());
        player.incrementScore();
        assertEquals("Score should increment to 2", 2, player.getScore());
    }

    // Test checkWinCondition default implementation returns false
    @Test
    public void testCheckWinConditionDefaultFalse() {
        assertFalse("checkWinCondition should return false by default", player.checkWinCondition(matchingMovie));
        assertFalse("checkWinCondition should return false for non-matching movie", player.checkWinCondition(nonMatchingMovie));
    }
}
