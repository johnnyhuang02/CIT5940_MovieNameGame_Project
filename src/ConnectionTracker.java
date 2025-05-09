import java.util.Map;

public class ConnectionTracker {
    private Map<String, Integer> connectionCounter;

    // check if a connection has been used for less than 3 times
    public boolean canUse(String connection) {
        return (connectionCounter.get(connection) < 3);
    }

    public void updateCondition(String connection) {
        // update on the count, increment by 1
    	connectionCounter.put(connection, getUsage(connection) + 1);
    }
    public int getUsage(String connection) {
        // check the usage of a connection
        // if not found, initialize to 0
        // else return get(connection)
    	return connectionCounter.getOrDefault(connection, 0);
    }
}
