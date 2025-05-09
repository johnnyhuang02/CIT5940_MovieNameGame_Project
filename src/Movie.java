
import java.util.List;

public class Movie {
    private String title;
    private int year;
    private List<String> genres;
    private List<Person> cast;
    private List<Person> crew; // Actors, directors, etc.

    public Movie(String title, int year, List<String> genres, List<Person> cast, List<Person> crew) {
        this.title = title;
        this.year = year;
        this.genres = genres;
        this.cast = cast;
        this.crew = crew;
    }

    // Getters
    public String getTitle() { return title; }
    public int getYear() { return year; }
    public List<String> getGenres() { return genres; }
    public List<Person> getCast() {return cast; }
    public List<Person> getCrew() { return crew; }

    // Check connection to another movie (shared actor/director/etc.)
    public boolean isConnectedTo(Movie other, String connectionType) {
        // Logic to compare crew based on connectionType
        return this.crew.stream().anyMatch(person -> 
            other.getCrew().contains(person) && person.getRole().equals(connectionType)
        );
        // use switch case loop for connectionType,
        // connectionType from {"genres", "cast", "crew"}
        // add more allowed connectionType if needed
        // in each case, call corresponding isConnectedBy*()
    }

    public boolean isConnectedByGenre(Movie other) {
        for (String genre : genres) {
            if (other.getGenres().contains(genre)) return true;
        }
        return false;
    }

    public boolean isConnectedByCast(Movie other) {
        for (Person p : cast) {
            if (other.getCast().contains(p)) return true;
        }
        return false;
    }

    public boolean isConnectedByCrew(Movie other) {
        for (Person p : crew) {
            if (other.getCrew().contains(p)) return true;
        }
        return false;
    }

    public boolean isConnectedByYear(Movie other) {
        return this.year == other.getYear();
    }

}