package station;

import station.optimize.Scheduler;
import various.ArrayTransformations;

import java.util.ArrayList;

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

    private ArrayList<Integer> accepted; // for debugging, how many EVs accepted
                                        // can be later used as a log of who charged
    private ArrayList<EVObject> chargedEVs; // as the above, delete the above when debugging ends

    private ArrayList<Integer> rejected; // same as above

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

        accepted = new ArrayList<>();
        chargedEVs = new ArrayList<>();
        rejected = new ArrayList<>();
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
        System.arraycopy(remainingChargers, 0, tempRemainingChargers, 0, remainingChargers.length);
        System.out.println("Why?");
        ArrayTransformations.printOneDimensionalArray(tempRemainingChargers);
        System.out.println("CP Schedule");
        ArrayTransformations.printTwoDimensionalArray(cpSchedule);
        for (int s = 0; s < slotsNumber; s++) {
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

    public void addAccepted (int i) {
        if (!alreadyAccepted(i))
            accepted.add(i);
    }

    public int getAcceptedNumber() {
        return accepted.size();
    }

    public boolean alreadyAccepted(int i) {
        if (accepted.contains(i)) {
            System.err.println("EV " + i + " has already accepted!");
            System.exit(1);
        }
        return false;
    }


    public void addRejected (int i) {
        if (!alreadyRejected(i))
            rejected.add(i);
    }

    public boolean alreadyRejected(int i) {
        if (rejected.contains(i)) {
            System.err.println("EV " + i + " has already accepted!");
            System.exit(1);
        }
        return false;
    }

    public void addChargedEV (EVObject ev) {
        chargedEVs.add(ev);
    }

    public ArrayList<EVObject> getChargedEVs () {
        return chargedEVs;
    }
}
