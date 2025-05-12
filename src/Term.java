
public class Term implements ITerm {

    // instance variables
    private String term;
    private long weight;

    /**
     * Initialize a Term with a given query String and weight
     */
    public Term(String term, long weight) {
        if (term == null) {
            throw new IllegalArgumentException("term cannot be null");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("weight cannot be negative");
        }
        this.term = term;
        this.weight = weight;
    }

    // =================
    // =================
    @Override
    public int compareTo(ITerm that) {
        return this.term.compareTo(that.getTerm());
    }

    @Override
    public String toString() {
        return weight + "\t" + term;
    }

    @Override
    public long getWeight() {
        return weight;
    }

    @Override
    public String getTerm() {
        return term;
    }

    @Override
    public void setWeight(long weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("weight cannot be negative");
        }
        this.weight = weight;
    }

    @Override
    public String setTerm(String term) {
        if (term == null) {
            throw new IllegalArgumentException("term cannot be null");
        }
        this.term = term;
        return this.term;
    }


}
