import java.util.HashMap;
import java.util.Map;

public class ConnectionTracker {
    private Map<String, Integer> connectionCounter = new HashMap<>();

    // check if a connection has been used for less than 3 times
    public boolean canUse(String type, String value) {
        return getUsage(type, value) < 3;
    }

    public void updateCondition(String type, String value) {
        String key = type + ":" + value.toLowerCase();
        connectionCounter.put(key, getUsage(type, value) + 1);
    }
    public int getUsage(String type, String value) {
        String key = type + ":" + value.toLowerCase();
        return connectionCounter.getOrDefault(key, 0);
    }
    public void resetAll() {
        connectionCounter.clear();
    }
}
