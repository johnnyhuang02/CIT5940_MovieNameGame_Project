

public class Validator {
    public boolean isValidMove(Movie currentMovie, Movie newMovie, String connectionType) {
        return currentMovie.isConnectedTo(newMovie, connectionType);
    }

    public boolean isWinConditionMet(Player player, Movie movie) {
        String condition = player.getWinCondition();
        // e.g., Check if movie is in player's target genre
        return movie.getGenres().contains(condition.split(":")[1]);
    }
}