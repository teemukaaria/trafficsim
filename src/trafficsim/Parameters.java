package trafficsim;
/**
 * Parameters
 * 1 tick equals 0,25s
 */
public class Parameters {
  public static final int TICKS_PER_SECOND = 4;
  public static final int BLOCK_DURATION = 2; // 1s // should be a multiple of 2
  public static final int SIMULATION_DURATION = 28800; // 2 hours
  public static final int LIGHT_CYCLE_DUARTION = 360; // 90s
  public static final int BASE_ALPHA = 20; // 5s (average time between two cars on the busyest road)
  public static final int SEED = 13;
  public static final int AMBER_LOOKAHEAD = 3 * Parameters.TICKS_PER_SECOND;

  public static final int GENERATION_SIZE = 300;
  public static final int NUMBER_OF_GENERATIONS = 150;
  public static final int ENCODING_BLOCKS = 30;
  public static final int ENCODING_BLOCK_DURATION = 3;
  public static final int PARENT_POOL_SIZE = 10;
  public static final double INITIAL_MUTATION_TRESHOLD = 0.75;
  public static final double MUTATION_TRESHOLD_MODIFIER = 0.98;
}