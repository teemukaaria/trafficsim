package trafficsim;

import java.util.List;
import java.util.Random;

/**
 * Evolution
 */
public class Evolution {
  private static double mutationTreshold = Parameters.INITIAL_MUTATION_TRESHOLD;

  public static void resetMutationTreshold() {
    mutationTreshold = Parameters.INITIAL_MUTATION_TRESHOLD;
  }

  public static CycleEncoding[] getInitialGeneration() {
    CycleEncoding[] generation = new CycleEncoding[Parameters.GENERATION_SIZE];
    for (int i = 0; i < Parameters.GENERATION_SIZE; i++) {
      generation[i] = new CycleEncoding();
    }
    return generation;
  }

  public static CycleEncoding[] getNextGeneration(List<Tuple<CycleEncoding, Double>> sortedPrevGen) {
    Random rnd = new Random();
    CycleEncoding[] generation = new CycleEncoding[Parameters.GENERATION_SIZE];
    for (int i = 0; i < Parameters.GENERATION_SIZE; i++) {
      generation[i] = new CycleEncoding(
        sortedPrevGen.get(rnd.nextInt(Parameters.PARENT_POOL_SIZE)).x,
        sortedPrevGen.get(rnd.nextInt(Parameters.PARENT_POOL_SIZE)).x,
        mutationTreshold
        );
      }
    mutationTreshold *= Parameters.MUTATION_TRESHOLD_MODIFIER;
    return generation;
  }
  public static double getMutationTreshold() {
    return mutationTreshold;
  }
}