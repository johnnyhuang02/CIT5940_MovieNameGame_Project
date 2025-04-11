
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

    public boolean equals(Person other) {
        return (this.name.equals(other.getName())) && (this.role.equals(other.getRole()));
    }
}