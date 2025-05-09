
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person other = (Person) obj;
        return this.name.equals(other.name) && this.role.equals(other.role);
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + role.hashCode();
    }

}