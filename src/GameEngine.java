
import edu.upenn.cit5940.model.*;

public class GameEngine {
    private GameState gameState;
    private Validator validator;

    public GameEngine(Movie startingMovie, Player p1, Player p2) {
        this.gameState = new GameState(startingMovie, p1, p2);
        this.validator = new Validator();
    }

    // Main game loop
    public void startGame() {
        initializeGame();
        // processMovie()
        while (!isGameOver()) {
            // 1. Display current state
            // 2. Get player input
            // 3. Validate move
            // 4. Update game state
        }
    }

    private boolean isGameOver() {
        // Check win/loss conditions
        return false;
    }

    private void initializeGame() {
        //
    }
}