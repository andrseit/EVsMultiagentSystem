package various;

/**
 * Created by Thesis on 30/4/2018.
 */
public class ArrayTransformations {

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
}
