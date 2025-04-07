
import java.util.List;

public class Movie {
    private String title;
    private int year;
    private List<String> genres;
    private List<Person> crew; // Actors, directors, etc.

    public Movie(String title, int year, List<String> genres, List<Person> crew) {
        this.title = title;
        this.year = year;
        this.genres = genres;
        this.crew = crew;
    }

    // Getters
    public String getTitle() { return title; }
    public int getYear() { return year; }
    public List<String> getGenres() { return genres; }
    public List<Person> getCrew() { return crew; }

    // Check connection to another movie (shared actor/director/etc.)
    public boolean isConnectedTo(Movie other, String connectionType) {
        // Logic to compare crew based on connectionType
        return this.crew.stream().anyMatch(person -> 
            other.getCrew().contains(person) && person.getRole().equals(connectionType)
        );
    }
}