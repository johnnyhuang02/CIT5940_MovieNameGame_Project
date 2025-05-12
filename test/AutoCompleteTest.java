import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class AutoCompleteTest {
    private AutoComplete ac;

    @Before
    public void setUp() {
        ac = new AutoComplete();
    }

    // Test addWord throws on null term
    @Test(expected = IllegalArgumentException.class)
    public void testAddWordNullThrows() {
        ac.addWord(null, 1);
    }

    // Test addWord does nothing on empty string
    @Test
    public void testAddWordEmptyDoesNothing() {
        ac.addWord("", 10);
        // no exception, root prefixes remain 0
        assertEquals("Empty word should not increase prefixes", 0, ac.countPrefixes(""));
    }

    // Test addWord throws on negative weight
    @Test(expected = IllegalArgumentException.class)
    public void testAddWordNegativeWeightThrows() {
        ac.addWord("a", -5);
    }

    // Test addWord skips invalid words containing non-letter chars
    @Test
    public void testAddWordInvalidCharsSkipped() {
        ac.addWord("app1", 5);
        // 'app1' is invalid, so no insertion; countPrefixes returns 0
        assertEquals("Invalid word should not be added", 0, ac.countPrefixes("app"));
    }

    // Test countPrefixes throws on null prefix
    @Test(expected = IllegalArgumentException.class)
    public void testCountPrefixesNullThrows() {
        ac.countPrefixes(null);
    }

    // Test getSubTrie behavior for null, empty, valid, and missing prefixes
    @Test
    public void testGetSubTrie() {
        // null prefix returns null
        assertNull("Null prefix should return null subtrie", ac.getSubTrie(null));

        // empty prefix returns the root Node
        Node root = ac.getSubTrie("");
        assertNotNull("Empty prefix returns a non-null node", root);
        // the root term should be the default empty string
        assertEquals("Root node term should be empty string", "", root.getTerm().getTerm());

        // unknown prefix returns null
        assertNull("Unknown prefix returns null", ac.getSubTrie("xyz"));

        // after adding words, valid prefix returns non-null
        ac.addWord("app", 1);
        ac.addWord("apple", 2);
        Node sub = ac.getSubTrie("app");
        assertNotNull("Existing prefix 'app' returns non-null node", sub);
        // And that sub.prefixes matches number of inserted terms with that prefix
        assertEquals("Subtrie prefixes count should be 2", 2, sub.getPrefixes());
    }

    // Test countPrefixes for various prefixes after insertion
    @Test
    public void testCountPrefixes() {
        ac.addWord("app", 1);
        ac.addWord("apple", 2);
        assertEquals("Prefix 'app' should count 2", 2, ac.countPrefixes("app"));
        assertEquals("Prefix 'a' should count 2", 2, ac.countPrefixes("a"));
        assertEquals("Prefix 'appl' should count 1", 1, ac.countPrefixes("appl"));
        assertEquals("Prefix 'apple' should count 1", 1, ac.countPrefixes("apple"));
        assertEquals("Prefix 'apples' should count 0", 0, ac.countPrefixes("apples"));
    }

    // Test getSuggestions returns an empty list for null or missing prefixes
    @Test
    public void testGetSuggestionsNullOrMissing() {
        assertTrue("Null prefix yields empty suggestions", ac.getSuggestions(null).isEmpty());
        assertTrue("Unknown prefix yields empty suggestions", ac.getSuggestions("zzz").isEmpty());
    }

    // Test getSuggestions basic behavior with multiple words
    @Test
    public void testGetSuggestionsBasic() {
        ac.addWord("app", 5);
        ac.addWord("apple", 10);
        List<ITerm> terms = ac.getSuggestions("app");
        assertEquals("Should return two suggestions", 2, terms.size());
        // The code returns a list in insertion order of terminal nodes:
        // first "app", then "apple"
        assertEquals("First term should be 'app'", "app", terms.get(0).getTerm());
        assertEquals("Second term should be 'apple'", "apple", terms.get(1).getTerm());
    }

    // Test that maxSuggestions cap is enforced
    @Test
    public void testMaxSuggestionsLimit() {
        // insert more than default max (10) distinct single-letter words
        for (char c = 'a'; c <= 'k'; c++) {
            ac.addWord(String.valueOf(c), c);
        }
        // countPrefixes("") should be 11
        assertEquals("Root prefixes should count 11", 11, ac.countPrefixes(""));
        List<ITerm> all = ac.getSuggestions("");
        // default maxSuggestions is 10
        assertEquals("Should return at most 10 suggestions", 10, all.size());
    }
}
