import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class AutoComplete implements IAutoComplete {

    private Node root;
    private int maxSuggestions = 10;

    // these are the characters accepted, consider legal char
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789 '.,:!&-";
    private static final int   ALPHABET_SIZE = 44; // 26 eng char, 10 num, 8 special char
    private static final Map<Character,Integer> INDEX = new HashMap<>();
    static {
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            INDEX.put(ALPHABET.charAt(i), i);
        }
    }


    // empty trie
    public AutoComplete() {
        root = new Node();
    }


    private boolean isValid(String str) {
        // check if invalid, and convert to lower case
        for (char c : str.toCharArray()) {
            if (!INDEX.containsKey(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addWord(String word, long weight) {
        if (word == null) {
            throw new IllegalArgumentException("term cannot be null");
        }
        if (word.isEmpty()) {
            return;
        }
        if (weight < 0) {
            throw new IllegalArgumentException("weight cannot be negative");
        }

        // convert to all lowercase
        String lower = word.toLowerCase();

        // check if invalid, and convert to lower case
        if (!isValid(lower)) {
            return;
        }

        Node currNode = root;
        // increment by one
        currNode.setPrefixes(currNode.getPrefixes() + 1);
        // declare a prefixesPlusOne() method?

        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
//            int index = c - 'a';
            Integer idx = INDEX.get(c);
            if (idx == null) {
                return;
            }
            int index = idx;
            Node[] children = currNode.getReferences();
            if (children[index] == null) {
                children[index] = new Node();
            }
            // update currNode, and increment prefixes
            currNode = children[index];
            currNode.setPrefixes(currNode.getPrefixes() + 1);
        }

        currNode.setTerm(new Term(lower, weight));
        currNode.setWords(1);

    }

    @Override
    public Node getSubTrie(String prefix) {
        if (prefix == null) {
            return null;
        }
        if (prefix.isEmpty()) {
            return root;
        }

        String lower = prefix.toLowerCase();
        if (!isValid(lower)) {
            return null;
        }
        Node currNode = root; // set to beginning

        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
//            int index = c - 'a';
            int index = INDEX.get(c);
            Node[] children = currNode.getReferences();
            if (children[index] == null) {
                return null;
            } else {
                currNode = children[index];
            }
        }
        return currNode;
    }

    @Override
    public int countPrefixes(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        prefix = prefix.toLowerCase();
        Node subTrie = getSubTrie(prefix);
        if (subTrie == null) {
            return 0;
        }
        return subTrie.getPrefixes();
    }

    @Override
    public List<ITerm> getSuggestions(String prefix) {
        if (prefix == null) {
            return new ArrayList<>();
        }

        Node subTrie = getSubTrie(prefix);
        if (subTrie == null) {
            return new ArrayList<>();
        } // return empty list if no such prefix

        List<ITerm> termList = new ArrayList<>();
        collectTerms(subTrie, termList);

        List<ITerm> suggestions = new ArrayList<>();
        for (ITerm term : termList) {
            suggestions.add(new Term(term.getTerm(), term.getWeight()));
        }

        suggestions.sort(ITerm.byReverseWeightOrder());

        int end = Math.min(maxSuggestions, termList.size());
        return new ArrayList<>(termList.subList(0, end));
    }

    private void collectTerms(Node node, List<ITerm> list) {
        // if the node represents a complete word, add the Term.
        if (node.getWords() == 1) {
            list.add(node.getTerm());
        }

        // check all children
        Node[] children = node.getReferences();
        for (Node child : children) {
            if (child != null) {
                collectTerms(child, list);
            }
        }
    }



}
