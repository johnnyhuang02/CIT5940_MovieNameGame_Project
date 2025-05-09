

public class Validator {
	public boolean isValidMovie(Movie current, Movie next, String connectionType) {
	    switch (connectionType.toLowerCase()) {
	        case "genre": return current.isConnectedByGenre(next);
	        case "cast": return current.isConnectedByCast(next);
	        case "crew": return current.isConnectedByCrew(next);
	        case "year": return current.isConnectedByYear(next);
	        default: return false;
	    }
	}


    public boolean isWinConditionMet(Player player, Movie movie) {
        String condition = player.getWinCondition();
        // e.g., Check if movie is in player's target genre
        return movie.getGenres().contains(condition.split(":")[1]);
    }
}