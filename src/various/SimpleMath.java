package various;

/**
 * Created by Thesis on 29/4/2018.
 */
public class SimpleMath {

    public static int manhattanDistance (int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static int manhattanDistance (int x1, int y1, int z1, int x2, int y2, int z2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2);
    }

    public static int worstWindowDifference(int initArrival, int initDeparture, int slotsNumber) {
        int width = initDeparture - initArrival + 1;
        int finalArrival, finalDeparture;
        if (initArrival > slotsNumber - width - initArrival)
            finalArrival = 0;
        else
            finalArrival = slotsNumber - width;
        finalDeparture = finalArrival + width - 1;

        return computeWindowDifference(initArrival, initDeparture, finalArrival, finalDeparture);
    }

    public static int computeWindowDifference(int initArrival, int initDeparture, int finalArrival, int finalDeparture) {
        int distance = 0;
        if (finalArrival > initDeparture) {
            distance += Math.abs(finalArrival - initDeparture) + Math.abs(finalDeparture - initDeparture);
        } else if (finalDeparture < initArrival) {
            distance += Math.abs(finalDeparture - initArrival) + Math.abs(finalArrival - initArrival);
        } else {
            distance += nonNegative(initArrival - finalArrival) + nonNegative(finalDeparture - initDeparture);
        }
        return distance;
    }

    public static double computeUtility (int initArrival, int initDeparture, int initEnergy,
                                  int finalArrival, int finalDeparture, int finalEnergy,
                                  int slotsNumber) {

        if (finalArrival > finalDeparture) {
            System.err.println("Debugging: @computeUtility -> Arrival > Departure!");
            System.exit(1);
        }
        double maxEnergyLoss = initEnergy;
        double maxWindowLoss = worstWindowDifference(initArrival, initDeparture, slotsNumber);
        double energyDifference = initEnergy - finalEnergy;
        double windowDifference = computeWindowDifference(initArrival, initDeparture, finalArrival, finalDeparture);

        System.out.println("Energy Difference: " + energyDifference);
        System.out.println("Max Energy Loss: " + maxEnergyLoss);
        System.out.println("Window Difference: " + windowDifference);
        System.out.println("Max Window Loss: " + maxWindowLoss);

        double energy = energyDifference/maxEnergyLoss;
        double window = windowDifference/maxWindowLoss;

        double utility =  1 - (0.5*energy + 0.5*window);
        System.out.println("Utility: " + utility);
        return utility;
    }

    private static int nonNegative (int i) {
        if (i < 0)
            return 0;
        return i;
    }

    public static double round (double number, int precision) {
        double t = Math.pow(10, precision);
        return Math.round(number*t)/t;
    }

}
