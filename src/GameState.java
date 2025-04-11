
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Movie currentMovie;
    private List<Movie> playedMovie; // changed to store <String>
    private Player player1;
    private Player player2;
    private Player activePlayer;
    private ConnectionTracker connectionTracker;

    public GameState(Movie startingMovie, Player p1, Player p2) {
        this.currentMovie = startingMovie;
        this.playedMovie = new ArrayList<>();
        this.player1 = p1;
        this.player2 = p2;
        this.activePlayer = p1;
    }

    // Getters and setters
    public Movie getCurrentMovie() { return currentMovie; }
    public List<Movie> getPlayedMovies() { return playedMovie; }
    public Player getActivePlayer() { return activePlayer; }

    // Update game state after a valid move
    public void updateGameState(Movie newMovie) {
        playedMovie.add(currentMovie);
        currentMovie = newMovie;
        activePlayer = (activePlayer == player1) ? player2 : player1;
    }

    // check if a movie if already been used in this game
    public boolean isUsed(Movie movie) {
        return false;
    }
}