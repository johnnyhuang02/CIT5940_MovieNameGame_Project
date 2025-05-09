import java.util.*;

public class MovieNameGame {
    public static void main(String[] args) {
        TextUI ui = new TextUI();
        TimerUI timer = new TimerUI();

        ui.displayWelcomeMessage();

        String name1 = ui.promptPlayerName(1);
        String name2 = ui.promptPlayerName(2);
        Player p1 = new Player(name1, ui.promptWinCondition(new Player(name1, "")));
        Player p2 = new Player(name2, ui.promptWinCondition(new Player(name2, "")));

        Movie startingMovie = new Movie("Inception", 2010, Arrays.asList("sci-fi", "thriller"), new ArrayList<>(), new ArrayList<>());

        List<String> recent = new LinkedList<>();
        recent.add(startingMovie.getTitle());

        Player current = p1;
        while (true) {
            ui.displayGameState(startingMovie.getTitle(), recent, current);
            timer.startTimer(30);
            System.out.println("You have 30 seconds...");
            String input = ui.getInput();
            timer.stopTimer();

            if (input.trim().equalsIgnoreCase("exit")) break;
            recent.add(input);
            if (recent.size() > 5) recent.remove(0);
            current = (current == p1) ? p2 : p1;
        }

        ui.showMessage("Game ended. Thanks for playing!");
    }
}
