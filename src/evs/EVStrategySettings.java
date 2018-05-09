package evs;

/**
 * Created by Thesis on 27/4/2018.
 */
public class EVStrategySettings {

    private int minArrival;
    private int maxDeparture;
    private int minEnergy;
    private int rounds;
    private int probability;
    private double maxWindowRange;
    private String priority;

    public EVStrategySettings(int minArrival, int maxDeparture, int minEnergy, int rounds, int probability, double maxWindowRange, String priority) {
        this.minArrival = minArrival;
        this.maxDeparture = maxDeparture;
        this.minEnergy = minEnergy;
        this.rounds = rounds;
        this.probability = probability;
        this.maxWindowRange = maxWindowRange;
        this.priority = priority;
    }

    public int getMinArrival() {
        return minArrival;
    }

    public int getMaxDeparture() {
        return maxDeparture;
    }

    public int getMinEnergy() {
        return minEnergy;
    }

    public int getRounds() {
        return rounds;
    }

    public int getProbability() {
        return probability;
    }

    public double getMaxWindowRange() {
        return maxWindowRange;
    }

    public String getPriority() {
        return priority;
    }

    public String toString () {
        return minArrival + "-" + maxDeparture + "/" + minEnergy;
    }
}
