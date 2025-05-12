import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AutoComplete implements IAutoComplete {

    private Node root;
    private int maxSuggestions = 10;

    // empty trie
    public AutoComplete() {
        root = new Node();
    }


    private boolean isValid(String str) {
        // check if invalid, and convert to lower case
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 'a' || c > 'z') {
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
            int index = c - 'a';
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
    // won't actually use this
    public Node buildTrie(String filename, int k) {
        this.maxSuggestions = k;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();

            try {
                int count = Integer.parseInt(line.trim());
                line = br.readLine();
            } catch (NumberFormatException e) {
                // if not, meaning first line is not total count
            }

            while (line != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] weightAndTerm = line.split("\\s+");
                    if (weightAndTerm.length >= 2) {
                        try {
                            long weight = Long.parseLong(weightAndTerm[0]);
                            String word = weightAndTerm[1];
                            addWord(word, weight);
                        } catch (NumberFormatException nfe) {
                            // skip the line if parsing fails, meaning more than 2 components
                        } // end try
                    } // end if
                } // end if
                line = br.readLine(); //update to next line
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return root;
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
            int index = c - 'a';
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
