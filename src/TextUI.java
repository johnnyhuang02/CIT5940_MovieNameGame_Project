import java.util.List;
import java.util.Scanner;

public class TextUI {
    private Scanner scanner;

    public TextUI() {
        this.scanner = new Scanner(System.in);
    }

    // Display game state
    public void displayGameState(String currentMovie, List<String> recentMovies, Player activePlayer) {
        System.out.println("Current Movie: " + currentMovie);
        System.out.println("Recent Movies: " + recentMovies);
        System.out.println("Active Player: " + activePlayer.getName());
    }

    // Get user input (movie name)
    public String getInput() {
        System.out.print("Enter a movie: ");
        return scanner.nextLine();
    }

    // Show autocomplete suggestions
    public void showSuggestions(List<String> suggestions) {
        System.out.println("Suggestions: " + String.join(", ", suggestions));
    }
}