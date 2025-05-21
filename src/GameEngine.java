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
    // storing link types, up to 4;
    private final Deque<String> recentConn = new ArrayDeque<>();
    private int roundCounter = 0;

    public GameEngine(Movie startingMovie, Player p1, Player p2, MovieDatabase movieDB) {
        this.movieDB = movieDB;
        this.validator = new Validator();
        this.connectionTracker = new ConnectionTracker();
        this.gameState = new GameState(startingMovie, p1, p2);

        recentTitles.addLast(startingMovie.getTitle());
    }

    public void clearDisplayBuffer() {
        displayBuffer.clear();
    }

    public void startTurn() {
//        connectionTracker.resetAll();
        roundCounter++;
//        displayBuffer.clear();
    }


    public boolean playMove(String title) throws IOException, InterruptedException {
        displayBuffer.clear();
        if (gameOver) {
            return false;
        }

        Player player = gameState.getActivePlayer();
        Movie current = gameState.getCurrentMovie();
        List<Movie> found = movieDB.getMovieByTitle(title);

        if (found.isEmpty()) {
            displayBuffer.add("movie " + title + " not found");
            return false;
        }
        Movie next = found.get(0);
        if (gameState.isUsed(next)) {
            displayBuffer.add(next.getTitle() + " has already been used.");
            return false;
        }

        // call string conn = public String getConnectionType(movie1, movie2) {
        // if conn == null : s

        String conn = detectConnectionType(current, next);
        if (conn == null) {
            displayBuffer.add("failed connecting to previous movie");
            return false;
        }

        String value = extractConnectionValue(conn, current, next);
        if (value == null || !connectionTracker.canUse(conn, value)) {
            displayBuffer.add("Cannot connect by " + conn + ": \"" + value + "\" used too many times.");
            return false;
        }

//        if (conn == null || !connectionTracker.canUse(conn)) {
//            displayBuffer.add("failed connecting to previous movie");
//            return false;
//        }

        // no need
        /*
        if (!validator.isValidMovie(current, next, conn)) {
            displayBuffer.add("Invalid connection by " + conn);
            return false;
        }*/

        // all checks passed → commit the move
        connectionTracker.updateCondition(conn, value);
        String playerName = player.getName();
        gameState.updateGameState(next);

        // update recent movies
        recentConn.addLast(conn);
        recentTitles.addLast(next.getTitle());

        if (recentTitles.size() > 5) {
            recentTitles.removeFirst();
            recentConn.removeFirst();
        }

        displayBuffer.add(current.getTitle()
                + " connected by " + conn
                + " to " + next.getTitle());

//        displayBuffer.add(gameState.getActivePlayer().getName()
//                + " connected by " + conn
//                + " to " + next.getTitle());

        if (validator.isWinConditionMet(player, next)) {
            player.incrementScore();
            displayBuffer.add(player.getName() +
                    " get score. Curr Score: " + player.getScore());
            if (player.getScore() >= 5) { // was 5
                displayBuffer.add(player.getName() + " Wins the GAME!!!!!!");
                gameOver = true;
            }
        }

        return true;
    }

    private String extractConnectionValue(String type, Movie m1, Movie m2) {
        switch (type) {
            case "year":
                return String.valueOf(m2.getYear());
            case "cast":
                for (Person p : m1.getCast()) {
                    if (m2.getCast().contains(p)) return p.getName();
                }
                break;
            case "crew":
                for (Person p : m1.getCrew()) {
                    if (m2.getCrew().contains(p)) return p.getName();
                }
                break;
            case "genre":
                for (String g : m1.getGenres()) {
                    if (m2.getGenres().contains(g)) return g;
                }
                break;
        }
        return null;  // no specific match found
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
        lines.add("Round: " + roundCounter);
        lines.add("Current: " + curr.getTitle());
        lines.add("===============================");
        lines.add("Recent: " + String.join(", ", recentTitles));

        List<String> titlesArr = new ArrayList<>(recentTitles);
        List<String> connArr = new ArrayList<>(recentConn);
        for (int i = 0; i < recentConn.size(); i++) {
            String from = titlesArr.get(i);
            String to   = titlesArr.get(i+1);
            String conn  = connArr.get(i);
            lines.add(from + " connected by " + conn + " to " + to);
        }
        lines.add("===============================");

        Player pl = gameState.getActivePlayer();
        lines.add("Turn:   " + pl.getName() +
                "   (Score: " + pl.getScore() + ")");
        lines.add("WinIf:  " + pl.getWinCondition());
        lines.add("===============================");

        lines.addAll(displayBuffer);
        return lines;
    }

    // called by TUI when game over
    public List<String> getFinalMessages() {
        return new ArrayList<>(displayBuffer);
    }


    // find the connection type
    private String detectConnectionType(Movie m1, Movie m2) {
        // not able to connect by genre
//        if (m1.isConnectedByGenre(m2)) return "genre";
        if (m1.isConnectedByYear(m2)) return "year";
        if (m1.isConnectedByCast(m2)) return "cast";
        if (m1.isConnectedByCrew(m2)) return "crew";
        return null;
    }
}
