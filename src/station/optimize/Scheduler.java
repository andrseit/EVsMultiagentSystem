package station.optimize;

import station.EVObject;

import java.util.ArrayList;

/**
 * Created by Thesis on 2/5/2018.
 */
public abstract class Scheduler {

    // return
    private int[][] schedule;
    private int[] whoCharged;

    // Do not forget to set the arrays
    public abstract void compute (ArrayList<EVObject> evs, int[] chargers, int[] price);

    public int[][] getSchedule() {
        if (schedule == null)
            System.err.println("Schedule array not set!");;
        return schedule;
    }

    public void setSchedule(int[][] schedule) {
        this.schedule = schedule;
    }

    public int[] getWhoCharged() {
        if (whoCharged == null)
            System.err.println("WhoCharged array not set!");
        return whoCharged;
    }

    public void setWhoCharged(int[] whoCharged) {
        this.whoCharged = whoCharged;
    }
}
