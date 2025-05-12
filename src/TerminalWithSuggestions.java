import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class TerminalWithSuggestions {
    private Terminal terminal;
    private Screen screen;
    private List<String> dictionary;
    private StringBuilder currentInput = new StringBuilder();
    private List<String> suggestions = new ArrayList<>();
    private int cursorPosition = 0;

    // Timer variables
    private int                      secondsRemaining = 60;
    private boolean                  timerRunning = true;
    private ScheduledExecutorService scheduler;

    public TerminalWithSuggestions() throws IOException {
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();

        dictionary = Arrays.asList(
                "java", "javascript", "python", "terminal", "program",
                "code", "compiler", "development", "interface", "application"
                                  );

        // Initialize timer thread
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (timerRunning && secondsRemaining > 0) {
                secondsRemaining--;
                try {
                    updateScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void run() throws IOException {
        boolean running = true;

        screen.clear();
        printString(0, 0, "> ");
        cursorPosition = 2;
        updateScreen();

        while (running) {
            KeyStroke keyStroke = terminal.pollInput();
            if (keyStroke != null) {
                switch (keyStroke.getKeyType()) {
                    case Character:
                        handleCharacter(keyStroke.getCharacter());
                        break;
                    case Backspace:
                        handleBackspace();
                        break;
                    case Enter:
                        handleEnter();
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

            // Small delay to prevent CPU hogging
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Shutdown timer
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

    private void handleEnter() throws IOException {
        int currentRow = screen.getCursorPosition().getRow();
        currentRow += 1 + suggestions.size();
        printString(0, currentRow, "> ");
        currentInput = new StringBuilder();
        cursorPosition = 2;
        suggestions.clear();
    }

    private void updateSuggestions() {
        suggestions.clear();
        String prefix = currentInput.toString();

        if (!prefix.isEmpty()) {
            for (String word : dictionary) {
                if (word.startsWith(prefix.toLowerCase()) && suggestions.size() < 5) {
                    suggestions.add(word);
                }
            }
        }
    }

    private void updateScreen() throws IOException {
        synchronized (screen) {
            screen.clear();

            // Print timer at top right
            String timerText = "Time: " + secondsRemaining + "s";
            TerminalSize size = screen.getTerminalSize();
            printString(size.getColumns() - timerText.length(), 0, timerText);

            // Print current command line
            printString(0, 0, "> " + currentInput.toString());

            // Print suggestions
            int row = 1;
            for (String suggestion : suggestions) {
                printString(2, row++, suggestion);
            }

            screen.setCursorPosition(new TerminalPosition(cursorPosition, 0));
            screen.refresh();
        }
    }

    private void printString(int column, int row, String text) {
        for (int i = 0; i < text.length(); i++) {
            screen.setCharacter(column + i, row,
                                new TextCharacter(text.charAt(i),
                                                  TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));
        }
    }

    public static void main(String[] args) {
        try {
            TerminalWithSuggestions app = new TerminalWithSuggestions();
            app.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}