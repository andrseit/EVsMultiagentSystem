package stats;

/**
 * Created by Thesis on 1/6/2018.
 */
public class TimeStats {

    private long start;
    private long end;
    private long elapsedTime;

    public TimeStats() {
        elapsedTime = 0;
    }

    public void startTimer () {
        start = System.nanoTime();
    }

    public void stopTimer () {
        end = System.nanoTime();
        elapsedTime += end - start;
    }

    public double getMillis () {
        return elapsedTime/Math.pow(10, 6);
    }

}
