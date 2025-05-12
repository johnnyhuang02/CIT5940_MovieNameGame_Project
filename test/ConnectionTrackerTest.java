import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConnectionTrackerTest {
    private ConnectionTracker tracker;

    @Before
    public void setUp() throws Exception {
        tracker = new ConnectionTracker();
        // Inject a fresh HashMap into the private field connectionCounter
        Field field = ConnectionTracker.class.getDeclaredField("connectionCounter");
        field.setAccessible(true);
        field.set(tracker, new HashMap<String, Integer>());
    }

    // Test getUsage returns 0 for unseen connection
    @Test
    public void testGetUsageDefaultZero() {
        assertEquals("Usage should be 0 when not seen before", 0, tracker.getUsage("genre"));
    }

    // Test updateCondition increments usage count
    @Test
    public void testUpdateConditionIncrements() {
        tracker.updateCondition("cast");
        assertEquals("Usage should be 1 after one update", 1, tracker.getUsage("cast"));
        tracker.updateCondition("cast");
        assertEquals("Usage should be 2 after two updates", 2, tracker.getUsage("cast"));
    }

    // Test canUse returns true for usage less than 3
    @Test
    public void testCanUseUnderLimit() {
        // pre-populate usage to 2
        tracker.updateCondition("crew");
        tracker.updateCondition("crew");
        assertTrue("canUse should return true when usage is 2", tracker.canUse("crew"));
    }

    // Test canUse returns false when usage reaches 3
    @Test
    public void testCanUseAtLimit() {
        // increment to 3
        tracker.updateCondition("genre");
        tracker.updateCondition("genre");
        tracker.updateCondition("genre");
        assertFalse("canUse should return false when usage is 3", tracker.canUse("genre"));
    }

    // Test that different keys are tracked independently
    @Test
    public void testIndependentKeys() {
        tracker.updateCondition("year");
        tracker.updateCondition("cast");
        tracker.updateCondition("cast");
        assertEquals("Usage for 'year' should be 1", 1, tracker.getUsage("year"));
        assertEquals("Usage for 'cast' should be 2", 2, tracker.getUsage("cast"));
        assertTrue("canUse should return true for 'year'", tracker.canUse("year"));
        assertTrue("canUse should return true for 'cast' (<3)", tracker.canUse("cast"));
    }
}
