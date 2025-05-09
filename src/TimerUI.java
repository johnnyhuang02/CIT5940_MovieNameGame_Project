public class TimerUI {
    private long endTime;
    private boolean running = false;

    public void startTimer(int seconds) {
        endTime = System.currentTimeMillis() + seconds * 1000;
        running = true;
    }

    public void stopTimer() {
        running = false;
    }

    public boolean isTimeUp() {
        return running && System.currentTimeMillis() > endTime;
    }
}
