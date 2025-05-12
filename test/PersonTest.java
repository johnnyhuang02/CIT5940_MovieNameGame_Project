import static org.junit.Assert.*;
import org.junit.Test;

public class PersonTest {

    // Test reflexivity: an object must equal itself
    @Test
    public void testEqualsSameObject() {
        Person person = new Person("Alice", "actor");
        assertTrue(person.equals(person));
    }

    // Test equality for two distinct objects with same name and role
    @Test
    public void testEqualsEqualPersons() {
        Person p1 = new Person("Alice", "actor");
        Person p2 = new Person("Alice", "actor");
        assertTrue(p1.equals(p2));
    }

    // Test inequality when names differ
    @Test
    public void testEqualsDifferentName() {
        Person p1 = new Person("Alice", "actor");
        Person p2 = new Person("Bob", "actor");
        assertFalse(p1.equals(p2));
    }

    // Test inequality when roles differ
    @Test
    public void testEqualsDifferentRole() {
        Person p1 = new Person("Alice", "actor");
        Person p2 = new Person("Alice", "director");
        assertFalse(p1.equals(p2));
    }

    // Test equals returns false when comparing to null
    @Test
    public void testEqualsNull() {
        Person person = new Person("Alice", "actor");
        assertFalse(person.equals(null));
    }

    // Test equals returns false when comparing to different class
    @Test
    public void testEqualsDifferentClass() {
        Person person = new Person("Alice", "actor");
        Object other = new Object();
        assertFalse(person.equals(other));
    }

    // Test hashCode consistency for equal objects
    @Test
    public void testHashCodeEqualForEqualObjects() {
        Person p1 = new Person("Alice", "actor");
        Person p2 = new Person("Alice", "actor");
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    // Test hashCode difference for objects that are not equal
    @Test
    public void testHashCodeDifferentForDifferentObjects() {
        Person p1 = new Person("Alice", "actor");
        Person p2 = new Person("Alice", "director");
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }
}
