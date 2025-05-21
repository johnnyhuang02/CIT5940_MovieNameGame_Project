
public class Player {
    private String name;
    private int score;
    private String winCondition; // e.g., "genre:horror"

    public Player(String name, String winCondition) {
        this.name = name;
        this.winCondition = winCondition;
        this.score = 0;
    }

    // Getters and setters
    public String getName() { return name; }
    public int getScore() { return score; }
    public String getWinCondition() { return winCondition; }

    public void incrementScore() { score++; }

    // check if this movie count as a winning count for this player
    public boolean checkWinCondition(Movie movie) {
        return false;
    }
}