import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;

public class GameEngineTest {

    // A trivial in‑memory DB that only knows the two movies we care about
    static class FakeMovieDatabase extends MovieDatabase {
        private final Map<String,Movie> map = new HashMap<>();
        void add(Movie m) { map.put(m.getTitle(), m); }
        @Override
        public List<Movie> getMovieByTitle(String title) {
            Movie m = map.get(title);
            return (m == null) ? Collections.emptyList()
                    : Collections.singletonList(m);
        }
    }

    @Test
    public void testPlayValidMove_ByYear() throws IOException, InterruptedException {
        // Arrange
        Movie m1 = new Movie("Movie1", 2000,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
        Movie m2 = new Movie("Movie2", 2000,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
        FakeMovieDatabase db = new FakeMovieDatabase();
        db.add(m1);
        db.add(m2);

        Player alice = new Player("Alice", "");
        Player bob   = new Player("Bob", "");

        GameEngine engine = new GameEngine(m1, alice, bob, db);

        // Act
        boolean ok = engine.playMove("Movie2");

        // Assert
        assertTrue(ok, "Should accept a year–match move");
        assertTrue(engine.getDisplayLines()
                        .stream()
                        .anyMatch(l -> l.contains("Movie1 connected by year to Movie2")),
                "Display must show the successful year connection");
    }

    @Test
    public void testPlayInvalidMovie_NotFound() throws IOException, InterruptedException {
        // Arrange: same setup, but DB has no entry for “Unknown”
        Movie m1 = new Movie("Movie1", 2000,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
        FakeMovieDatabase db = new FakeMovieDatabase();
        db.add(m1);

        GameEngine engine = new GameEngine(m1,
                new Player("Alice", ""),
                new Player("Bob", ""),
                db);

        // Act
        boolean ok = engine.playMove("Unknown");

        // Assert
        assertFalse(ok, "Should reject a title not in the DB");
        assertTrue(engine.getDisplayLines()
                        .get(engine.getDisplayLines().size() - 1)
                        .contains("movie Unknown not found"),
                "Last line must mention the movie was not found");
    }

    @Test
    public void testTimeOut_LosesGame() {
        // Arrange
        Movie m1 = new Movie("Movie1", 2000,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
        GameEngine engine = new GameEngine(m1,
                new Player("Alice", ""),
                new Player("Bob", ""),
                new FakeMovieDatabase());

        // Act
        engine.timeOut();

        // Assert
        assertTrue(engine.isGameOver(), "GameOver should flip on timeout");
        assertTrue(engine.getDisplayLines()
                        .get(engine.getDisplayLines().size() - 1)
                        .contains("Time’s up! Alice loses."),
                "Timeout message must name the active player");
    }
}
