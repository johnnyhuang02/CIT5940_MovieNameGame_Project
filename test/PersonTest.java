import static org.junit.Assert.*;
import org.junit.Test;

public class PersonTest {

    // Test reflexivity: an object must be equal to itself
    @Test
    public void testEqualsSameObject() {
        Person person = new Person("Tom Hanks", "actor");
        assertTrue(person.equals(person));
    }

    // Test equality for two distinct objects with same name and role
    @Test
    public void testEqualsEqualPersons() {
        Person p1 = new Person("Tom Hanks", "actor");
        Person p2 = new Person("Tom Hanks", "actor");
        assertTrue(p1.equals(p2));
        // also test that hashCode is the same for equal objects
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    // Test inequality when names differ
    @Test
    public void testEqualsDifferentName() {
        Person p1 = new Person("Tom Hanks", "actor");
        Person p2 = new Person("Leonardo DiCaprio", "actor");
        assertFalse(p1.equals(p2));
    }

    // Test inequality when roles differ
    @Test
    public void testEqualsDifferentRole() {
        Person p1 = new Person("Tom Hanks", "actor");
        Person p2 = new Person("Tom Hanks", "director");
        assertFalse(p1.equals(p2));
    }

    // Test equals returns false when comparing to null
    @Test
    public void testEqualsNull() {
        Person p = new Person("Tom Hanks", "actor");
        assertFalse(p.equals(null));
    }

    // Test equals returns false when comparing to different class
    @Test
    public void testEqualsDifferentClass() {
        Person p = new Person("Tom Hanks", "actor");
        String other = "not a person";
        assertFalse(p.equals(other));
    }

    // Test hashCode consistency: multiple invocations return the same value
    @Test
    public void testHashCodeConsistency() {
        Person p = new Person("Tom Hanks", "actor");
        int initialHash = p.hashCode();
        assertEquals(initialHash, p.hashCode());
    }

    // Test hashCode difference for objects that are not equal
    @Test
    public void testHashCodeDifferentForDifferentObjects() {
        Person p1 = new Person("Tom Hanks", "actor");
        Person p2 = new Person("Tom Hanks", "director");
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }
}
