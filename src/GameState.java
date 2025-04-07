
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Movie currentMovie;
    private List<Movie> playedMovies;
    private Player player1;
    private Player player2;
    private Player activePlayer;

    public GameState(Movie startingMovie, Player p1, Player p2) {
        this.currentMovie = startingMovie;
        this.playedMovies = new ArrayList<>();
        this.player1 = p1;
        this.player2 = p2;
        this.activePlayer = p1;
    }

    // Getters and setters
    public Movie getCurrentMovie() { return currentMovie; }
    public List<Movie> getPlayedMovies() { return playedMovies; }
    public Player getActivePlayer() { return activePlayer; }

    // Update game state after a valid move
    public void updateGameState(Movie newMovie) {
        playedMovies.add(currentMovie);
        currentMovie = newMovie;
        activePlayer = (activePlayer == player1) ? player2 : player1;
    }
}