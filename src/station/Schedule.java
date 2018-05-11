package station;

import station.optimize.Scheduler;
import various.ArrayTransformations;

import java.util.Arrays;

/**
 * Created by Thesis on 30/4/2018.
 */
public class Schedule {

    private Scheduler scheduler;

    private int slotsNumber;
    private int[][] schedule;
    private int[] whoCharged;
    private int[][] cpSchedule; // the schedule extracted from cplex, unify with full schedule
    private int[] remainingChargers;
    private int[] tempRemainingChargers; // remaining chargers after optimal computation
    private int[] price;

    public Schedule(int slotsNumber, int chargersNumber, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.slotsNumber = slotsNumber;
        schedule = new int[0][slotsNumber];
        whoCharged = new int[0];
        remainingChargers = new int[slotsNumber];
        tempRemainingChargers = new int[slotsNumber];
        for (int s = 0; s < slotsNumber; s++) {
            remainingChargers[s] = chargersNumber;
            tempRemainingChargers[s] = chargersNumber;
        }
        setPrice();

    }

    // ABSTRACT - method that sets the initial price
    // Create an updatePrice () if you want to change in each slot
    private void setPrice () {
        price = new int[slotsNumber];
        for (int s = 0; s < slotsNumber; s++) {
            price[s] = 1;
        }
    }

    public void setCPSchedule(int[][] cpSchedule) {
        this.cpSchedule = cpSchedule;
    }

    private void updateChargers (int row) {
        for (int s = 0; s < slotsNumber; s++) {
            remainingChargers[s] -= cpSchedule[row][s];
        }
        System.arraycopy(remainingChargers, 0, tempRemainingChargers, 0, remainingChargers.length);
    }

    public void updateTemporaryChargers () {
        for (int s = 0; s < cpSchedule[0].length; s++) {
            for (int e = 0; e < cpSchedule.length; e++) {
                tempRemainingChargers[s] -= cpSchedule[e][s];
                if (tempRemainingChargers[s] < 0) {
                    System.err.println("Temp remaining chargers below 0!");
                    System.exit(1);
                }
            }
        }
    }

    public void addToSchedule (int row) {
        schedule = ArrayTransformations.addLine(schedule, cpSchedule[row]);
        updateChargers(row);
    }

    public int[] getRemainingChargers() {
        return remainingChargers;
    }

    public int[] getTempRemainingChargers() {
        return tempRemainingChargers;
    }

    public int[] getPrice() {
        return price;
    }

    public void setWhoCharged(int[] whoCharged) {
        this.whoCharged = whoCharged;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public int[] getWhoCharged() {
        return whoCharged;
    }

    public int[][] getSchedule() {
        return schedule;
    }

    public int[][] getCpSchedule() {
        return cpSchedule;
    }

    public int getSlotsNumber() {
        return slotsNumber;
    }
}
