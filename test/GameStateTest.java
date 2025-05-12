import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;

public class GameStateTest {
    private Movie startMovie;
    private Movie nextMovie;
    private Movie anotherMovie;
    private Player player1;
    private Player player2;
    private GameState gameState;

    @Before
    public void setUp() {
        // create dummy Movie instances
        startMovie = new Movie("Start", 2000, 
            Arrays.asList("genre"), Collections.emptyList(), Collections.emptyList());
        nextMovie = new Movie("Next", 2001, 
            Arrays.asList("genre"), Collections.emptyList(), Collections.emptyList());
        anotherMovie = new Movie("Another", 2002, 
            Arrays.asList("genre"), Collections.emptyList(), Collections.emptyList());

        // create dummy Player instances
        player1 = new Player("Alice", "genre:genre");
        player2 = new Player("Bob", "genre:genre");

        // initialize GameState with starting movie and two players
        gameState = new GameState(startMovie, player1, player2);
    }

    // Test constructor initializes state correctly
    @Test
    public void testInitialState() {
        assertEquals("Current movie should be the start movie", startMovie, gameState.getCurrentMovie());
        assertTrue("Played movies list should be empty at start", gameState.getPlayedMovies().isEmpty());
        assertEquals("Active player should be player1 initially", player1, gameState.getActivePlayer());
    }

    // Test updateGameState updates current movie, adds to played list, and switches active player
    @Test
    public void testUpdateGameStateSwitchPlayerAndMoves() {
        gameState.updateGameState(nextMovie);
        // after first update
        assertEquals("Current movie should be nextMovie", nextMovie, gameState.getCurrentMovie());
        assertTrue("Played movies should contain the start movie", gameState.getPlayedMovies().contains(startMovie));
        assertEquals("Active player should switch to player2", player2, gameState.getActivePlayer());

        // apply another move
        gameState.updateGameState(anotherMovie);
        assertEquals("Current movie should be anotherMovie", anotherMovie, gameState.getCurrentMovie());
        assertTrue("Played movies should contain nextMovie", gameState.getPlayedMovies().contains(nextMovie));
        assertEquals("Active player should switch back to player1", player1, gameState.getActivePlayer());
    }

    // Test isUsed returns true for current movie
    @Test
    public void testIsUsedCurrentMovie() {
        assertTrue("isUsed should return true for current movie", gameState.isUsed(startMovie));
    }

    // Test isUsed returns true for a movie in played list
    @Test
    public void testIsUsedPlayedMovie() {
        gameState.updateGameState(nextMovie);
        assertTrue("isUsed should return true for a movie in played list", gameState.isUsed(startMovie));
    }

    // Test isUsed returns false for a movie not used
    @Test
    public void testIsUsedUnusedMovie() {
        assertFalse("isUsed should return false for a movie not used", gameState.isUsed(anotherMovie));
    }
}
