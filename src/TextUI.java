import java.util.List;
import java.util.Scanner;

public class TextUI {
    private Scanner scanner;

    public TextUI() {
        scanner = new Scanner(System.in);
    }

    public void displayWelcomeMessage() {
        System.out.println("Welcome to the Movie Name Game!");
        System.out.println("Players take turns naming movies connected by actors, directors, or genres.");
    }

    public String promptPlayerName(int number) {
        System.out.print("Enter name for Player " + number + ": ");
        return scanner.nextLine();
    }

    public String promptWinCondition(Player player) {
        System.out.print(player.getName() + ", enter your win condition (e.g. genre:horror): ");
        return scanner.nextLine();
    }

    public void displayGameState(String currentMovie, List<String> recentMovies, Player activePlayer) {
        System.out.println("\n-------------------");
        System.out.println("Current Movie: " + currentMovie);
        System.out.println("Recent Movies: " + String.join(", ", recentMovies));
        System.out.println("Active Player: " + activePlayer.getName());
    }

    public String getInput() {
        System.out.print("Enter a movie title: ");
        return scanner.nextLine();
    }

    public void showSuggestions(List<String> suggestions) {
        if (!suggestions.isEmpty()) {
            System.out.println("Suggestions: " + String.join(", ", suggestions));
        }
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
