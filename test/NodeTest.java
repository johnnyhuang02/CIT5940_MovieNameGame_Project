import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class NodeTest {
    private Node defaultNode;

    @Before
    public void setUp() {
        defaultNode = new Node();
    }

    /**
     * Test the default constructor initializes fields correctly:
     * - term: empty string, weight 0
     * - words: 0
     * - prefixes: 0
     * - references: non-null array of length 26
     */
    @Test
    public void testDefaultConstructor() {
        assertNotNull("Default node should have a term", defaultNode.getTerm());
        assertEquals("Default term text should be empty", "", defaultNode.getTerm().getTerm());
        assertEquals("Default term weight should be 0", 0, defaultNode.getTerm().getWeight());
        assertEquals("Default words count should be 0", 0, defaultNode.getWords());
        assertEquals("Default prefixes count should be 0", 0, defaultNode.getPrefixes());
        assertNotNull("References array should not be null", defaultNode.getReferences());
        assertEquals("References array length should be 26", 44, defaultNode.getReferences().length);
    }

    /**
     * Test the parameterized constructor with valid inputs:
     * - term text and weight set correctly
     * - words and prefixes initialized to 0
     * - references array length 26
     */
    @Test
    public void testParamConstructorValid() {
        Node node = new Node("hello", 5);
        assertEquals("Term text should be 'hello'", "hello", node.getTerm().getTerm());
        assertEquals("Term weight should be 5", 5, node.getTerm().getWeight());
        assertEquals("Words count should start at 0", 0, node.getWords());
        assertEquals("Prefixes count should start at 0", 0, node.getPrefixes());
        assertNotNull("References array should not be null", node.getReferences());
        assertEquals("References array length should be 26", 44, node.getReferences().length);
    }

    /**
     * Test that constructor throws IllegalArgumentException for null query.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParamConstructorNullQuery() {
        new Node(null, 1);
    }

    /**
     * Test that constructor throws IllegalArgumentException for negative weight.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParamConstructorNegativeWeight() {
        new Node("test", -1);
    }

    /**
     * Test all setter and getter methods:
     * - setTerm and getTerm
     * - setWords and getWords
     * - setPrefixes and getPrefixes
     * - setReferences and getReferences
     */
    @Test
    public void testGettersSetters() {
        // test setTerm and getTerm
        Term customTerm = new Term("world", 10);
        defaultNode.setTerm(customTerm);
        assertEquals("Term should be updated to customTerm", customTerm, defaultNode.getTerm());

        // test setWords and getWords
        defaultNode.setWords(3);
        assertEquals("Words count should be updated to 3", 3, defaultNode.getWords());

        // test setPrefixes and getPrefixes
        defaultNode.setPrefixes(7);
        assertEquals("Prefixes count should be updated to 7", 7, defaultNode.getPrefixes());

        // test setReferences and getReferences
        Node[] newRefs = new Node[26];
        newRefs[0] = new Node("a", 1);
        defaultNode.setReferences(newRefs);
        Node[] fetchedRefs = defaultNode.getReferences();
        assertSame("References array should be the same instance", newRefs, fetchedRefs);
        assertEquals("Child at index 0 should be node with term 'a'", "a", fetchedRefs[0].getTerm().getTerm());
    }
}
