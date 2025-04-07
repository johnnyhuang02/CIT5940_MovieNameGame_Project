
public class Person {
    private String name;
    private String role; // "actor", "director", etc.

    public Person(String name, String role) {
        this.name = name;
        this.role = role;
    }

    // Getters
    public String getName() { return name; }
    public String getRole() { return role; }
}