import java.util.*;

public class GameEngine {
    private GameState gameState;
    private Validator validator;
    private ConnectionTracker connectionTracker;
    private TextUI ui;
    private TimerUI timer;
    private MovieDatabase movieDB;

    public GameEngine(Movie startingMovie, Player p1, Player p2, MovieDatabase movieDB) {
        this.movieDB = movieDB;
        this.ui = new TextUI();
        this.timer = new TimerUI();
        this.validator = new Validator();
        this.connectionTracker = new ConnectionTracker();
        this.gameState = new GameState(startingMovie, p1, p2);
    }

    public void startGame() {
        List<String> recent = new LinkedList<>();
        recent.add(gameState.getCurrentMovie().getTitle());

        while (!isGameOver()) {
            Movie current = gameState.getCurrentMovie();
            Player player = gameState.getActivePlayer();

            ui.displayGameState(current.getTitle(), recent, player);

            String prefix = ui.getInput();
            List<String> suggestions = movieDB.getSuggestions(prefix);
            ui.showSuggestions(suggestions);

            timer.startTimer(30);
            String input = ui.getInput();
            timer.stopTimer();

            if (timer.isTimeUp()) {
                ui.showMessage("Time's up! " + player.getName() + " loses.");
                break;
            }

            Movie chosen = movieDB.getMovieByTitle(input);
            if (chosen == null || gameState.isUsed(chosen)) {
                ui.showMessage("Invalid movie or already used. Try again.");
                continue;
            }

            String connectionType = detectConnectionType(current, chosen);
            if (connectionType == null || !connectionTracker.canUse(connectionType)) {
                ui.showMessage("No valid connection or connection type overused.");
                continue;
            }

            if (!validator.isValidMovie(current, chosen, connectionType)) {
                ui.showMessage("Movies not connected by: " + connectionType);
                continue;
            }

            connectionTracker.updateCondition(connectionType);
            gameState.updateGameState(chosen);
            recent.add(chosen.getTitle());
            if (recent.size() > 5) recent.remove(0);

            if (validator.isWinConditionMet(player, chosen)) {
                player.incrementScore();
                if (player.getScore() >= 5) {
                    ui.showMessage(player.getName() + " wins by reaching the goal!");
                    break;
                }
            }
        }

        ui.showMessage("Game Over.");
    }

    private boolean isGameOver() {
        return false; // Enhanced win logic is handled in loop
    }

    private String detectConnectionType(Movie m1, Movie m2) {
        if (m1.isConnectedByGenre(m2)) return "genre";
        if (m1.isConnectedByCast(m2)) return "cast";
        if (m1.isConnectedByCrew(m2)) return "crew";
        if (m1.isConnectedByYear(m2)) return "year";
        return null;
    }
}
