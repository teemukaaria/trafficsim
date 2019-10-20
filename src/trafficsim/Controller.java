package trafficsim;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.io.FileNotFoundException;

/**
 * Controller
 */
public class Controller {
  private CycleEncoding[] generation;
  private TrafficLight[] lights;
  private AdvancableBlock[] advancables;
  private RoadBlock[] inroads;
  private List<Queue<Car>> incomingCars;
  private LightColor[][] lightConfigurations;

  private void fillLights(LightColor[][] lights, int[] green, int start, int end) {
    for (int greenIndex : green) {
      Arrays.fill(lights[greenIndex], start, end, LightColor.GREEN);
    }
  }
  private void fillLights(LightColor[][] lights, int encoding, int block) {
    int start = block * Parameters.ENCODING_BLOCK_DURATION * Parameters.TICKS_PER_SECOND;
    int end = (block + 1) * Parameters.ENCODING_BLOCK_DURATION * Parameters.TICKS_PER_SECOND;
    switch (encoding) {
      case 0:
        fillLights(lights, new int[]{0, 1, 2, 3}, start, end);
        break;
      case 1:
        fillLights(lights, new int[]{0, 1, 2, 7}, start, end);
        break;
      case 2:
        fillLights(lights, new int[]{3, 4, 5, 6}, start, end);
        break;
      case 3:
        fillLights(lights, new int[]{3, 8}, start, end);
        break;
      case 4:
        fillLights(lights, new int[]{7, 8}, start, end);
        break;
    }
  }
  private LightColor[][] decodeCycle(CycleEncoding cycle) {
    LightColor[][] leftLights = new LightColor[9][Parameters.LIGHT_CYCLE_DUARTION];
    for (LightColor[] light : leftLights) Arrays.fill(light, LightColor.RED);
    LightColor[][] rightLights = new LightColor[9][Parameters.LIGHT_CYCLE_DUARTION];
    for (LightColor[] light : rightLights) Arrays.fill(light, LightColor.RED);
    for (int i = 0; i < Parameters.ENCODING_BLOCKS; i++) {
      fillLights(leftLights, cycle.getLeft(i), i);
    }
    for (int i = 0; i < Parameters.ENCODING_BLOCKS; i++) {
      fillLights(rightLights, cycle.getRight(i), i);
    }
    return ArrayUtils.addAll(leftLights, rightLights);
  }

  private void initMap() {
    IntersectionBlock[] endings = new IntersectionBlock[]{ // total 12
      new IntersectionBlock("out from light 1", null, 0),
      new IntersectionBlock("out from light 4", null, 0),
      new IntersectionBlock("out from light 5", null, 0),
      new IntersectionBlock("out from light 6", null, 0),
      new IntersectionBlock("out from light a", null, 0),
      new IntersectionBlock("out from light 10", null, 0),
      new IntersectionBlock("out from light 11", null, 0),
      new IntersectionBlock("out from light 14", null, 0),
      new IntersectionBlock("out from light 15", null, 0),
      new IntersectionBlock("out from light 16", null, 0),
      new IntersectionBlock("out from light b", null, 0),
      new IntersectionBlock("out from light 20", null, 0)
    };
    lights = new TrafficLight[]{ // total 18
      new TrafficLight("1", endings[0], lightConfigurations[0]),   
      null,
      null,
      new TrafficLight("4", endings[1], lightConfigurations[3]),   
      new TrafficLight("5", endings[2], lightConfigurations[4]),   
      null,
      null,
      new TrafficLight("a", endings[4], lightConfigurations[7]), 
      new TrafficLight("10", endings[5], lightConfigurations[8]),
      new TrafficLight("11", endings[6], lightConfigurations[9]),
      null,
      null,
      new TrafficLight("14", endings[7], lightConfigurations[12]),
      new TrafficLight("15", endings[8], lightConfigurations[13]),
      null,
      null,
      new TrafficLight("b", endings[10], lightConfigurations[16]),
      new TrafficLight("20", endings[11], lightConfigurations[17]),
    };
    RoadBlock[] underpasses = initUnderpasses();
    IntersectionBlock[] internalIntersections = initInternalIntersections(underpasses);
    advancables = ArrayUtils.addAll(new AdvancableBlock[0], endings);
    advancables = ArrayUtils.addAll(advancables, underpasses);
    advancables = ArrayUtils.addAll(advancables, internalIntersections);
    fillInLights(endings, internalIntersections);
    inroads = new RoadBlock[]{
      new RoadBlock("to light 1", lights[0]),
      new RoadBlock("to light 2", lights[1]),
      new RoadBlock("to light 3", lights[2]),
      new RoadBlock("to light 4", lights[3]),
      new RoadBlock("to light 5", lights[4]),
      new RoadBlock("to light 6", lights[5]),
      new RoadBlock("to light 7", lights[6]),
      new RoadBlock("to light 11", lights[9]),
      new RoadBlock("to light 12", lights[10]),
      new RoadBlock("to light 13", lights[11]),
      new RoadBlock("to light 14", lights[12]),
      new RoadBlock("to light 15", lights[13]),
      new RoadBlock("to light 16", lights[14]),
      new RoadBlock("to light 17", lights[15]),
    };
  }
  private RoadBlock[] initUnderpasses() {
    RoadBlock[] result = new RoadBlock[90];
    RoadBlock endLane1 = new RoadBlock("lane 1 block 15.2", lights[7], Parameters.BLOCK_DURATION / 2);
    RoadBlock endLane4 = new RoadBlock("lane 4 block 15.2", lights[16], Parameters.BLOCK_DURATION / 2);
    RoadBlock[] lastBlocks = new RoadBlock[]{
      endLane1,
      new RoadBlock("lane 1 block 15.1", endLane1, Parameters.BLOCK_DURATION / 2),
      new RoadBlock("lane 2 block 15", lights[8]),
      new RoadBlock("lane 3 block 15", lights[17]),
      endLane4,
      new RoadBlock("lane 4 block 15.1", endLane4, Parameters.BLOCK_DURATION / 2),
    };
    for (int i = 0; i < 15; i++) {
      for (int j = 0; j < 6; j++) {
        result[i * 6 + j] = lastBlocks[j];
      }
      lastBlocks[0] = new RoadBlock("lane 1 block " + (14 - i) + ".2", lastBlocks[1], Parameters.BLOCK_DURATION / 2);
      lastBlocks[1] = new RoadBlock("lane 1 block " + (14 - i) + ".1", lastBlocks[0], Parameters.BLOCK_DURATION / 2);
      lastBlocks[2] = new RoadBlock("lane 2 block " + (14 - i), lastBlocks[2]);
      lastBlocks[3] = new RoadBlock("lane 3 block " + (14 - i), lastBlocks[3]);
      lastBlocks[4] = new RoadBlock("lane 4 block " + (14 - i) + ".2", lastBlocks[5], Parameters.BLOCK_DURATION / 2);
      lastBlocks[5] = new RoadBlock("lane 4 block " + (14 - i) + ".1", lastBlocks[4], Parameters.BLOCK_DURATION / 2);
    }
    return result;
  }
  private IntersectionBlock[] initInternalIntersections(RoadBlock[] underpasses) {
    IntersectionBlock[] ends = new IntersectionBlock[]{
      new IntersectionBlock("end from light 2 to lane 4", underpasses[89], Parameters.BLOCK_DURATION),
      new IntersectionBlock("end from light 3 to lane 3", underpasses[87], Parameters.BLOCK_DURATION),
      new IntersectionBlock("end from light 6 to lane 4", underpasses[89], Parameters.BLOCK_DURATION),
      new IntersectionBlock("end from light 7 to lane 4", underpasses[89], Parameters.BLOCK_DURATION),
      new IntersectionBlock("end from light 12 to lane 1", underpasses[84], Parameters.BLOCK_DURATION),
      new IntersectionBlock("end from light 13 to lane 2", underpasses[86], Parameters.BLOCK_DURATION),
      new IntersectionBlock("end from light 16 to lane 1", underpasses[84], Parameters.BLOCK_DURATION),
      new IntersectionBlock("end from light 17 to lane 1", underpasses[84], Parameters.BLOCK_DURATION),
    };
    IntersectionBlock[] starts = new IntersectionBlock[] {
      new IntersectionBlock("start from light 2 to lane 4", ends[0], Parameters.BLOCK_DURATION),
      new IntersectionBlock("start from light 3 to lane 3", ends[1], Parameters.BLOCK_DURATION),
      new IntersectionBlock("start from light 6 to lane 4", ends[2], Parameters.BLOCK_DURATION),
      new IntersectionBlock("start from light 7 to lane 4", ends[3], Parameters.BLOCK_DURATION),
      new IntersectionBlock("start from light 12 to lane 1", ends[4], Parameters.BLOCK_DURATION),
      new IntersectionBlock("start from light 13 to lane 2", ends[5], Parameters.BLOCK_DURATION),
      new IntersectionBlock("start from light 16 to lane 1", ends[6], Parameters.BLOCK_DURATION),
      new IntersectionBlock("start from light 17 to lane 1", ends[7], Parameters.BLOCK_DURATION),
    };
    return ArrayUtils.addAll(ends, starts);
  }
  private void fillInLights(IntersectionBlock[] endings, IntersectionBlock[] intersections) {
    lights[1] = new TrafficLight("2", intersections[8], lightConfigurations[1]);
    lights[2] = new TrafficLight("3", intersections[9], lightConfigurations[2]);
    lights[5] = new TrafficLight("6", new IntersectionBlock[]{ endings[3], intersections[10] }, lightConfigurations[5]);
    lights[6] = new TrafficLight("7", intersections[11], lightConfigurations[6]);
    lights[10] = new TrafficLight("12", intersections[12], lightConfigurations[10]);
    lights[11] = new TrafficLight("13", intersections[13], lightConfigurations[11]);
    lights[14] = new TrafficLight("16", new IntersectionBlock[]{ endings[9], intersections[14] }, lightConfigurations[14]);
    lights[15] = new TrafficLight("17", intersections[15], lightConfigurations[15]);
  }

  private void initCars() {
    int[][][] arrivals = new int[][][]{
      PoissonArrivals.getArrivals(4), // to nro 1
      PoissonArrivals.getArrivals(4), // to nro 2
      PoissonArrivals.getArrivals(2), // to nro 3
      PoissonArrivals.getArrivals(2), // to nro 4
      PoissonArrivals.getArrivals(2), // to nro 5
      PoissonArrivals.getArrivals(2), // to nro 6
      PoissonArrivals.getArrivals(2), // to nro 7
      PoissonArrivals.getArrivals(2), // to nro 11
      PoissonArrivals.getArrivals(4), // to nro 12
      PoissonArrivals.getArrivals(4), // to nro 13
      PoissonArrivals.getArrivals(1), // to nro 14
      PoissonArrivals.getArrivals(1), // to nro 15
      PoissonArrivals.getArrivals(1), // to nro 16
      PoissonArrivals.getArrivals(1), // to nro 17
    };
    incomingCars = Arrays.stream(arrivals)
      .map((int[][] arrival)->
        Arrays
          .stream(arrival)
          .map((int[] params)->new Car(params))
          .collect(Collectors.toCollection(LinkedList::new))
        )
      .collect(Collectors.toList());
  }

  private List<Tuple<CycleEncoding, Double>> updateTopResults(
    List<Tuple<CycleEncoding, Double>> currentBest,
    List<Tuple<CycleEncoding, Double>> sortedResults
  ) {
    int numberOfBestResults = 10;
    List<Tuple<CycleEncoding, Double>> filtered = sortedResults
      .stream()
      .filter((Tuple<CycleEncoding, Double> a)->!currentBest.contains(a))
      .collect(Collectors.toList());
    currentBest.addAll(
      filtered.subList(0, Math.min(filtered.size(), numberOfBestResults))
    );
    return currentBest
      .stream()
      .sorted((Tuple<CycleEncoding, Double> a, Tuple<CycleEncoding, Double> b)->(int) (100000 * (a.y - b.y)))
      .collect(Collectors.toList())
      .subList(0, numberOfBestResults);
  }

  private PrintWriter prepareOutput() throws FileNotFoundException {
    File outputFile = new File("simu_results_" + System.currentTimeMillis() + ".txt");
    PrintWriter output = new PrintWriter(outputFile);
    output.printf("Generation size %d Number of Generations %d\n", Parameters.GENERATION_SIZE, Parameters.NUMBER_OF_GENERATIONS);
    output.printf("Parent pool %d Don't include parents\n", Parameters.PARENT_POOL_SIZE);
    output.printf("Mutation treshold %f Multiplier %f\n\n", Parameters.INITIAL_MUTATION_TRESHOLD, Parameters.MUTATION_TRESHOLD_MODIFIER);
    output.printf("%10s\t%10s\t%s\n", "Best", "Average", "Took");
    return output;
  }

  public Controller() throws FileNotFoundException {
    // for (int k = 0; k < 12; k++) {
      generation = Evolution.getInitialGeneration();
      lightConfigurations = decodeCycle(generation[0]);
      initMap();
      initCars();
      Simulation simu = new Simulation(lights, advancables, inroads, incomingCars);
      List<Tuple<CycleEncoding, Double>> results = new ArrayList<Tuple<CycleEncoding, Double>>();
      List<Tuple<CycleEncoding, Double>> topTen = new ArrayList<Tuple<CycleEncoding, Double>>();
      PrintWriter output = prepareOutput();
      try {
        for (int j = 0; j < Parameters.NUMBER_OF_GENERATIONS; j++) {
          long start, end;
          start = System.currentTimeMillis();
          // simulate current generation
          for (int i = 0; i < Parameters.GENERATION_SIZE; i++) {
            simu.reconfigure(decodeCycle(generation[i]));
            double averageWait = simu.run();
            results.add(new Tuple<CycleEncoding, Double>(generation[i], averageWait));
          }
          end = System.currentTimeMillis();
          // analyze results
          results = results
            .stream()
            .sorted((Tuple<CycleEncoding, Double> a, Tuple<CycleEncoding, Double> b)->(int)(100000 * (a.y - b.y)))
            .collect(Collectors.toList());
          Tuple<CycleEncoding, Double> bestResult = results.get(0);
          topTen = updateTopResults(topTen, results);
          double average = 0.0;
          for (Tuple<CycleEncoding, Double> result : results) average += result.y;
          average /= Parameters.GENERATION_SIZE;
          double std = 0.0;
          for (Tuple<CycleEncoding, Double> result : results) std += Math.pow(result.y - average, 2);
          std = Math.sqrt(std / Parameters.GENERATION_SIZE);
          output.printf("%3d;%10.4f;%10.4f;%10.4f;%d\n", j, bestResult.y, average, std, end - start);
          System.out.printf("%3d Best: %10.4f\tAverage: %10.4f\tStd: %10.4f\tTook: %dms\tMutation: %5.4f\n", j, bestResult.y, average, std , end - start, Evolution.getMutationTreshold());
          // move to next generation
          generation = Evolution.getNextGeneration(results);
          results = new ArrayList<Tuple<CycleEncoding, Double>>();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      finally {
        for (Tuple<CycleEncoding, Double> globalBest : topTen) {
          output.println("\nGlobal best: " + globalBest.y + "\n" + globalBest.x);
          System.out.println("\nGlobal best: " + globalBest.y + "\n" + globalBest.x);
        }
        output.close();
        Evolution.resetMutationTreshold();
      }
    // }
  }

  public static void main(String[] args) throws FileNotFoundException {
    new Controller();
  }
}