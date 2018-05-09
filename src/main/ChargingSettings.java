package main;

/**
 * Created by Thesis on 26/4/2018.
 */
public class ChargingSettings {
    private int arrival;
    private int departure;
    private int energy;

    public ChargingSettings(int arrival, int departure, int energy) {
        this.arrival = arrival;
        this.departure = departure;
        this.energy = energy;
    }

    public int getArrival() {
        return arrival;
    }

    public int getDeparture() {
        return departure;
    }

    public int getEnergy() {
        return energy;
    }

    public String toString () {
        return arrival + "-" + departure + "/" + energy;
    }
}
