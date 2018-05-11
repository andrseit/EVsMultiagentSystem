package various;

/**
 * Created by Thesis on 30/4/2018.
 */
public class ArrayTransformations {

    public static int[] addRow (int[] array, int element) {
        int arrayRows = array.length;
        int[] result = new int[arrayRows + 1];
        int[] add = new int[1];
        add[0] = element;
        System.arraycopy(array, 0, result, 0, arrayRows);
        System.arraycopy(add, 0, result, arrayRows, 1);
        return result;
    }

    public static int[][] addLine (int[][] array, int[] line) {
        int arrayRows = array.length;
        int arrayColumns = line.length;
        int[][] result = new int[arrayRows + 1][arrayColumns];
        int[][] add = new int[1][arrayColumns];
        for (int i = 0; i < line.length; i++) {
            add[0][i] = line[i];
        }
        System.arraycopy(array, 0, result, 0, arrayRows);
        System.arraycopy(add, 0, result, arrayRows, 1);
        return result;
    }

    public static void printOneDimensionalArray (int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println("");
    }

    public static void printTwoDimensionalArray (int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public static int arraySum (int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static int[][] removeZeroRows (int[][] array1, int[] array2, int columns) {
        int arrayRows = array1.length;
        int arrayColumns = columns;
        int[][] result1 = new int[0][arrayColumns];
        int[] result2 = new int[0];

        for (int i = 0; i < arrayRows; i++) {
            if (array2[i] > 0) {
                result1 = addLine(result1, array1[i]);
                result2 = addRow(array2, 1);
            }
        }
        return result1;
    }

    public static int[] removeZeroRows (int[] array) {
        int arrayRows = array.length;
        int[] result = new int[0];

        for (int i = 0; i < arrayRows; i++) {
            if (array[i] > 0) {
                result = addRow(result, array[i]);
            }
        }
        return result;
    }

    public static int[][] concatMaps(int[][] first, int[][] second) {
        int columns = 0;
        if (first.length == 0)
            columns = second[0].length;
        else
            columns = first[0].length;

        ArrayTransformations t = new ArrayTransformations();
        if (second.length != 0) {
            //this.scheduleMap = initial;
            int map_length = first.length;
            int[][] new_full_schedule_map = new int[map_length + second.length][columns];
            System.arraycopy(first, 0, new_full_schedule_map, 0, first.length);
            System.arraycopy(second, 0, new_full_schedule_map, first.length, second.length);
            first = new_full_schedule_map;
        }
        return first;
    }

    public static int[] concatOneDimensionMaps(int[] first, int[] second) {

        ArrayTransformations t = new ArrayTransformations();
        if (second.length != 0) {
            //this.scheduleMap = initial;
            int map_length = first.length;
            int[] new_full_schedule_map = new int[map_length + second.length];
            System.arraycopy(first, 0, new_full_schedule_map, 0, first.length);
            System.arraycopy(second, 0, new_full_schedule_map, first.length, second.length);
            first = new_full_schedule_map;
        }
        return first;
    }
}
