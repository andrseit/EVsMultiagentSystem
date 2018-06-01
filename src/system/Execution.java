package system;

import evs.EV;
import station.Station;
import various.ArrayTransformations;

import java.util.ArrayList;

/**
 * Created by Thesis on 10/5/2018.
 */
public abstract class Execution {

    private ArrayList<EV> evs;
    private ArrayList<Station> stations;
    private int[] finishedStations;
    private int slotsNumber;

    public Execution(ArrayList<EV> evs, ArrayList<Station> stations, int slotsNumber) {
        this.evs = evs;
        this.stations = stations;
        this.slotsNumber = slotsNumber;
        finishedStations = new int[stations.size()];
    }

    public abstract void execute ();

    // checks if all stations have finished their duties
    protected boolean executionOver () {
        return ArrayTransformations.arraySum(finishedStations) == stations.size();
    }

    protected void resetFinishedStations () {
        finishedStations = new int[stations.size()];
    }

    public ArrayList<EV> getEvs() {
        return evs;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public int[] getFinishedStations() {
        return finishedStations;
    }

    public int getSlotsNumber() {
        return slotsNumber;
    }

    protected void printAgents () {
        System.out.println("Stations in the system: ");
        for (Station s: getStations())
            System.out.println(s.toString());

        System.out.println("\nEVs in the system: ");
        for (EV ev: getEvs())
            System.out.println(ev.toString());
    }

    protected void checkResults () {
        for (Station s: stations) {
            s.checkSchedule();
        }
    }
}
