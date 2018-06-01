package debugging;

import various.ArrayTransformations;

/**
 * Created by Thesis on 31/5/2018.
 */
public class Debugging {

    //given a schedule map it checks if the sum of evs charging at
    // a time point exceeds the numbers of chargers
    public static boolean exceedChargers (int[][] schedule, int chargers) {
        if (schedule.length == 0)
            return false;
        int[] columnsSum = ArrayTransformations.getColumnsCount(schedule);
        for (int s = 0; s < columnsSum.length; s++) {
            if (columnsSum[s] > chargers)
                return true;
        }
        return false;
    }

    // checks if the EVs in the final schedule are more or less
    // than the EVs accepted
    public static boolean exceedAccepted (int[][] schedule, int accepted) {
        int rows = schedule.length;
        if (rows != accepted) {
            if (rows > accepted) {
                System.err.println("More rows than accepted!");
            } else if (rows < accepted) {
                System.err.println("Less rows than accepted!");
            }
            return true;
        }
        return false;
    }
}
