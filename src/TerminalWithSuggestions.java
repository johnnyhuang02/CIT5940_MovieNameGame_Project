import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TerminalWithSuggestions {
    private final GameEngine engine;
    private final MovieDatabase movieDB;

    private final Terminal terminal;
    private final Screen screen;

    private StringBuilder currentInput = new StringBuilder();
    private List<String> suggestions = List.of();
    private int cursorPosition = 2;
    private final int maxSuggestions = 10;

    // turn timer
    private final int resetTime = 30;
    private int secondsRemaining = resetTime;
    private boolean timerRunning = false;
    private final ScheduledExecutorService scheduler;

    public TerminalWithSuggestions(GameEngine engine,
                                   MovieDatabase movieDB) throws IOException {
        this.engine   = engine;
        this.movieDB  = movieDB;

        this.terminal = new DefaultTerminalFactory().createTerminal();
        this.screen   = new com.googlecode.lanterna.screen.TerminalScreen(terminal);
        this.screen.startScreen();

        // schedule the countdown
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(() -> {
            if (timerRunning && secondsRemaining > 0) {
                secondsRemaining--;
                try {
                    updateScreen();
                } catch (IOException ignored) {}
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void run() throws IOException, InterruptedException {
        // 1) kickoff first turn
        engine.startTurn();
        timerRunning     = true;
        secondsRemaining = resetTime;
        updateScreen();

        boolean running = true;
        while (running) {
            // 2) timeout?
            if (timerRunning && secondsRemaining <= 0) {
                engine.timeOut();
                break;
            }

            // 3) poll input
            KeyStroke key = terminal.pollInput();
            if (key == null) {
                Thread.sleep(10);
                continue;
            }

            KeyType type = key.getKeyType();
            switch (type) {
                case Character:
                    handleCharacter(key.getCharacter());
                    break;
                case Backspace:
                    handleBackspace();
                    break;
                case Enter:
                    handleEnter();
                    if (engine.isGameOver()) running = false;
                    break;
                case EOF:
                case Escape:
                    running = false;
                    break;
                default:
                    break;
            }

            updateScreen();
        }

        // 4) draw final game‑over screen
        List<String> lines = new ArrayList<>();
//        List<String> lines = engine.getDisplayLines();
        lines.add("=== GAME OVER ===");
        lines.addAll(engine.getFinalMessages());
        synchronized(screen) {
            screen.clear();
            for (int i = 0; i < lines.size(); i++) {
                printString(0, i, lines.get(i));
            }
            screen.refresh();
        }


        // 5) wait for user to press any key
        printString(0, screen.getTerminalSize().getRows() - 1, "Press any key to exit...");
        screen.refresh();

        terminal.readInput();

        // 6) cleanup
        scheduler.shutdown();
        screen.close();
        terminal.close();
    }

    private void handleCharacter(char c) {
        currentInput.insert(cursorPosition - 2, c);
        cursorPosition++;
        updateSuggestions();
    }

    private void handleBackspace() {
        if (cursorPosition > 2) {
            currentInput.deleteCharAt(cursorPosition - 3);
            cursorPosition--;
            updateSuggestions();
        }
    }

    private void handleEnter() throws IOException, InterruptedException {
        String choice = currentInput.toString().trim();
        engine.clearDisplayBuffer(); // added
        boolean success = engine.playMove(choice);
//        updateScreen();
        if (success) {
//        if (!engine.isGameOver()) {
            // reset for next turn
//            engine.clearDisplayBuffer();

            currentInput.setLength(0);
            cursorPosition = 2;
            suggestions = List.of();

            secondsRemaining = resetTime;
            timerRunning = true;
            engine.startTurn();
//            engine.clearDisplayBuffer();
        }
//        } else {
        if (engine.isGameOver()) {
            timerRunning = false;
        }
    }

    private void updateSuggestions() {
        String prefix = currentInput.toString();
        if (prefix.isEmpty()) {
            suggestions = List.of();
        } else {
            suggestions = movieDB
                    .getSuggestions(prefix)
                    .stream()
                    .limit(maxSuggestions)
                    .collect(Collectors.toList());
        }
    }

    private void updateScreen() throws IOException {
        synchronized(screen) {
            screen.clear();

            // draw game state
            List<String> lines = engine.getDisplayLines();
            for (int i = 0; i < lines.size(); i++) {
                printString(0, i, lines.get(i));
            }

            // draw timer
            String tm = "Time: " + secondsRemaining + "s";
            TerminalSize size = screen.getTerminalSize();
            printString(size.getColumns() - tm.length(), 0, tm);

            // draw prompt + suggestions
            int baseRow = lines.size() + 1;
            printString(0, baseRow, "> " + currentInput.toString());
            for (int j = 0; j < suggestions.size(); j++) {
                printString(2, baseRow + 1 + j, suggestions.get(j));
            }

            // reposition cursor
            screen.setCursorPosition(
                    new TerminalPosition(2 + currentInput.length(), baseRow)
            );
            screen.refresh();
//            engine.clearDisplayBuffer();
        }
    }

    private void printString(int col, int row, String text) {
        for (int i = 0; i < text.length(); i++) {
            screen.setCharacter(
                    col + i, row,
                    new TextCharacter(text.charAt(i),
                            TextColor.ANSI.WHITE,
                            TextColor.ANSI.BLACK)
            );
        }
    }
}
