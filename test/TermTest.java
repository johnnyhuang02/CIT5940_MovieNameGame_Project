import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

public class TermTest {

    // Test valid constructor and getters
    @Test
    public void testConstructorAndGetters() {
        Term t = new Term("apple", 10);
        assertEquals("Term text should match", "apple", t.getTerm());
        assertEquals("Term weight should match", 10, t.getWeight());
    }

    // Test constructor throws on null term
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullTerm() {
        new Term(null, 5);
    }

    // Test constructor throws on negative weight
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativeWeight() {
        new Term("banana", -1);
    }

    // Test toString format "weight<TAB>term"
    @Test
    public void testToString() {
        Term t = new Term("orange", 7);
        assertEquals("toString should be '7\torange'", "7\torange", t.toString());
    }

    // Test compareTo lexicographic order
    @Test
    public void testCompareTo() {
        Term t1 = new Term("alpha", 1);
        Term t2 = new Term("beta", 2);
        assertTrue("alpha < beta", t1.compareTo(t2) < 0);
        assertTrue("beta > alpha", t2.compareTo(t1) > 0);
        assertEquals("same term compareTo=0", 0, t1.compareTo(new Term("alpha", 5)));
    }

    // Test setTerm updates term and returns new value
    @Test
    public void testSetTerm() {
        Term t = new Term("first", 1);
        String returned = t.setTerm("second");
        assertEquals("Returned term should be 'second'", "second", returned);
        assertEquals("getTerm should reflect new value", "second", t.getTerm());
    }

    // Test setTerm throws on null
    @Test(expected = IllegalArgumentException.class)
    public void testSetTermNull() {
        Term t = new Term("x", 1);
        t.setTerm(null);
    }

    // Test setWeight updates weight
    @Test
    public void testSetWeight() {
        Term t = new Term("x", 1);
        t.setWeight(20);
        assertEquals("getWeight should reflect new weight", 20, t.getWeight());
    }

    // Test setWeight throws on negative
    @Test(expected = IllegalArgumentException.class)
    public void testSetWeightNegative() {
        Term t = new Term("x", 1);
        t.setWeight(-5);
    }

    // Test ITerm.byReverseWeightOrder comparator
    @Test
    public void testByReverseWeightOrder() {
        Term t1 = new Term("a", 5);
        Term t2 = new Term("b", 10);
        List<ITerm> list = new ArrayList<>(List.of(t1, t2));
        list.sort(ITerm.byReverseWeightOrder());
        assertEquals("First should be weight 10", 10, list.get(0).getWeight());
        assertEquals("Second should be weight 5", 5, list.get(1).getWeight());
    }

    // Test ITerm.byPrefixOrder comparator
    @Test
    public void testByPrefixOrder() {
        Term t1 = new Term("apple", 1);
        Term t2 = new Term("apricot", 2);
        List<ITerm> list = new ArrayList<>(List.of(t2, t1));
        // compare first 2 chars: "ap" vs "ap" -> equal, so original order
        list.sort(ITerm.byPrefixOrder(2));
        assertEquals("Order should remain the same when prefixes equal", "apricot", list.get(0).getTerm());
        // compare first 3 chars: "apr" vs "app" -> "app" < "apr"
        list = new ArrayList<>(List.of(t2, t1));
        list.sort(ITerm.byPrefixOrder(3));
        assertEquals("apple should come before apricot for r=3", "apple", list.get(0).getTerm());
    }

    // Test byPrefixOrder throws on negative r
    @Test(expected = IllegalArgumentException.class)
    public void testByPrefixOrderNegative() {
        ITerm.byPrefixOrder(-1);
    }
}
