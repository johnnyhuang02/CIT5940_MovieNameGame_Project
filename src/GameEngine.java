import java.io.IOException;
import java.util.*;

public class GameEngine {

    private GameState gameState;
    private Validator validator;
    private ConnectionTracker connectionTracker;
    private MovieDatabase movieDB;

    private boolean gameOver = false;

    // for TUI display
    private final List<String> displayBuffer = new ArrayList<>();

    // storing recent movies up to 5
    private final Deque<String> recentTitles = new ArrayDeque<>();

//    private String lastMessage = "";
//    private final List<String> finalMessages = new ArrayList<>();


    public GameEngine(Movie startingMovie, Player p1, Player p2, MovieDatabase movieDB) {
        this.movieDB = movieDB;
//        this.ui = new TextUI();
//        this.timer = new TimerUI();
        this.validator = new Validator();
        this.connectionTracker = new ConnectionTracker();
        this.gameState = new GameState(startingMovie, p1, p2);
        recentTitles.addLast(startingMovie.getTitle());
    }

    public void startTurn() {
//        lastMessage = "";
        connectionTracker.resetAll();
        displayBuffer.clear();
    }


    public void playMove(String title) throws IOException, InterruptedException {
        if (gameOver) {
            return;
        }


        Player player = gameState.getActivePlayer();
        Movie current = gameState.getCurrentMovie();
        List<Movie> found = movieDB.getMovieByTitle(title);

        if (found.isEmpty()) {
            displayBuffer.add("movie " + title + " not found");
            return;
        }
        Movie next = found.get(0);
        if (gameState.isUsed(next)) {
            displayBuffer.add(next.getTitle() + " has already been used.");
            return;
        }

        String conn = detectConnectionType(current, next);
        if (conn == null || !connectionTracker.canUse(conn)) {
            displayBuffer.add("failed connecting to previous movie");
            return;
        }

        if (!validator.isValidMovie(current, next, conn)) {
            displayBuffer.add("Invalid connection by " + conn);
            return;
        }

        // all checks passed → commit the move
        connectionTracker.updateCondition(conn);
        gameState.updateGameState(next);

        // update recents
        recentTitles.addLast(next.getTitle());
        if (recentTitles.size() > 5) recentTitles.removeFirst();

        displayBuffer.add( gameState.getActivePlayer().getName()
                + " connected by " + conn
                + " to " + next.getTitle());

        if (validator.isWinConditionMet(player, next)) {
            player.incrementScore();
            displayBuffer.add(player.getName() +
                    " get score. Curr Score: " + player.getScore());
            if (player.getScore() >= 5) {
                displayBuffer.add(player.getName() + " Wins the GAME!!!!!!");
                gameOver = true;
            }
        }
    }

    // called by tui when play exceeded 30 seconds limit
    public void timeOut() {
        Player loser = gameState.getActivePlayer();
        displayBuffer.add("Time’s up! " + loser.getName() + " loses.");
        gameOver = true;
    }

    // check if gameOver status
    public boolean isGameOver() {
        return gameOver; // Enhanced win logic is handled in loop
    }

    // display helper functions:
    public List<String> getDisplayLines() {
        List<String> lines = new ArrayList<>();

        Movie curr = gameState.getCurrentMovie();
        lines.add("Current: " + curr.getTitle());
        lines.add("Recent: " + String.join(", ", recentTitles));
        Player pl = gameState.getActivePlayer();
        lines.add("Turn:   " + pl.getName() +
                "   (Score: " + pl.getScore() + ")");
        lines.add("WinIf:  " + pl.getWinCondition());

        lines.addAll(displayBuffer);
        return lines;
    }

    // called by TUI when game over
    public List<String> getFinalMessages() {
        return new ArrayList<>(displayBuffer);
    }


    // find the connection type
    private String detectConnectionType(Movie m1, Movie m2) {
        if (m1.isConnectedByGenre(m2)) return "genre";
        if (m1.isConnectedByCast(m2)) return "cast";
        if (m1.isConnectedByCrew(m2)) return "crew";
        if (m1.isConnectedByYear(m2)) return "year";
        return null;
    }
}
