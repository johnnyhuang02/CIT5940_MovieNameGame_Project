import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.*;

public class MovieNameGame {
    public static void main(String[] args) throws IOException, InterruptedException {
        // build the initial movieDB from csv files
        String moviesCsv  = "tmdb_5000_movies.csv";
        String creditsCsv = "tmdb_5000_credits.csv";
        MovieDatabase movieDB = new MovieDatabase();
        try {
            movieDB.loadFromCSV(moviesCsv, creditsCsv);
        } catch (IOException | CsvValidationException e) {
//            System.err.println("Failed to load movie data: " + e.getMessage());
            return;
        }

        // use TextUI for initial prompts
        TextUI ui = new TextUI(movieDB);
        ui.displayWelcomeMessage();
        // get player 1 and 2
        String name1 = ui.promptPlayerName(1);
        String name2 = ui.promptPlayerName(2);
        Player p1 = new Player(name1, ui.promptWinCondition(new Player(name1, "")));
        Player p2 = new Player(name2, ui.promptWinCondition(new Player(name2, "")));

        // pick a random movie to start with
//        Movie start = movieDB.selectRandomMovie();
        // can pick known movie for demo purpose
        Movie start = movieDB.getMovieByTitle("The Fugitive").get(0);

        // create the GameEngine, and run in TUI
        GameEngine engine = new GameEngine(start, p1, p2, movieDB);
        try {
            // run the TUI with our movieDB and the gameEngine we design
            TerminalWithSuggestions tui = new TerminalWithSuggestions(engine, movieDB);
            tui.run();
        } catch (IOException e) {
//            System.err.println("Error running TUI: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ui.showMessage("Game ended. Thanks for playing!");
    }
}
