package trafficsim;

import java.util.Arrays;
import java.util.Random;

public class PoissonArrivals {
  private static Random rnd = new Random(Parameters.SEED);

  public static double getPoisson(double lambda) {
    return -Math.log(1.0 - Math.random()) * lambda;
  }

  private static int[][] getArrivals(int window, int lambda) {
    int[][] arrivals = new int[(int) Math.ceil(window / lambda)][2];
    for (int i = 0; i < arrivals.length; i++) {
      arrivals[i] = new int[]{rnd.nextInt(window), i % 2}; // getPoisson(lambda);
    }
    Arrays.sort(arrivals, (int[] a, int[] b)->a[0] - b[0]);
    return arrivals;
  }

  public static int[][] getArrivals(int multiplier) {
    return getArrivals(Parameters.SIMULATION_DURATION, multiplier * Parameters.BASE_ALPHA);
  }
}