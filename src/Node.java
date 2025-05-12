/**
 * @author Harry Smith
 */

public class Node {

    private Term term;
    private int words;
    private int prefixes;
    private Node[] references;

    /**
     * Initialize a Node with an empty string and 0 weight; useful for
     * writing tests.
     */
    public Node() {
        // TODO!
        term = new Term("", 0);
        words = 0;
        prefixes = 0;
        references = new Node[26];
    }

    /**
     * Initialize a Node with the given query string and weight.
     * @throws IllegalArgumentException if query is null or if weight is negative.
     */
    public Node(String query, long weight) {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("weight cannot be negative");
        }
        term = new Term(query, weight);
//        words = 1;
        words = 0;
//        prefixes = 1;
        prefixes = 0;
        references = new Node[26];
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }

    public int getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(int prefixes) {
        this.prefixes = prefixes;
    }

    public Node[] getReferences() {
        return references;
    }

    public void setReferences(Node[] references) {
        this.references = references;
    }
}
