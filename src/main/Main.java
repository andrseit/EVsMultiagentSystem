package main;

import station.EVObject;
import station.negotiation.SuggestionsComputer;
import system.AgentSystem;
import various.ArrayTransformations;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Thesis on 26/4/2018.
 */
public class Main {
    public static void main(String[] args) {
        AgentSystem system = new AgentSystem();
        system.run();

        /*
        int[] array = new int[]{1, 0, 1, 1};
        int[][] schedule = new int[][]{{1, 0, 1}, {0, 0, 0}, {1, 1, 1}, {0, 1, 0}};
        System.out.println("Before: ");
        ArrayTransformations.printOneDimensionalArray(array);
        System.out.println("");
        ArrayTransformations.printTwoDimensionalArray(schedule);

        schedule = ArrayTransformations.removeZeroRows(schedule, array);
        array = ArrayTransformations.removeZeroRows(array);

        System.out.println("After: ");
        ArrayTransformations.printOneDimensionalArray(array);
        System.out.println("");
        ArrayTransformations.printTwoDimensionalArray(schedule);

        int[][] schedule2 = new int[][]{{2, 2, 2}, {0, 2, 0}, {2, 2, 2}, {0, 2, 0}};
        schedule = ArrayTransformations.concatMaps(schedule, schedule2);
        int[] array2 = new int[]{3, 2, 1, 0};
        array = ArrayTransformations.concatOneDimensionMaps(array, array2);

        System.out.println("Concat: ");
        ArrayTransformations.printOneDimensionalArray(array);
        System.out.println("");
        ArrayTransformations.printTwoDimensionalArray(schedule);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(4);
        list2.add(5);
        list2.add(6);

        list.addAll(list2);
        System.out.println(list);
        */


        /*
        ArrayList<EVObject> evs = new ArrayList<>();
        EVObject ev = new EVObject(0, 0, new ChargingSettings(4, 8, 5));
        ev.setDistance(0);
        evs.add(ev);
        ev = new EVObject(0, 0, new ChargingSettings(7, 9, 3));
        ev.setDistance(2);
        evs.add(ev);
        ev = new EVObject(0, 0, new ChargingSettings(6, 7, 2));
        ev.setDistance(1);
        evs.add(ev);

        //OptimizeSuggestions s = new OptimizeSuggestions(10, evs);
        //s.optimize();
        SuggestionsComputer computer = new SuggestionsComputer();
        computer.compute(evs, new int[]{1, 1, 0, 0, 1, 1, 1, 1, 1, 1}, new int[10]);
        */
    }
}
