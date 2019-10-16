package trafficsim;

import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * Simulation
 */
public class Simulation {
  private int totalNumberOfCars = 0;
  public static long totalTickInSimulation = 0;
  private TrafficLight[] lights;
  private AdvancableBlock[] advancables;
  private RoadBlock[] inroads;
  private List<Queue<Car>> untouchedCars;
  private List<Queue<Car>> incomingCars;


  public Simulation(
    TrafficLight[] lights,
    AdvancableBlock[] advancables,
    RoadBlock[] inroads,
    List<Queue<Car>> incomingCars
  ) {
    this.lights = lights;
    this.advancables = advancables;
    this.inroads = inroads;
    this.incomingCars = new ArrayList<Queue<Car>>();
    this.untouchedCars = incomingCars;
    for (Queue<Car> cars : incomingCars) {
      totalNumberOfCars += cars.size();
      this.incomingCars.add(new LinkedList<Car>(cars));
    }
  }

  public void reconfigure(LightColor[][] lightConfigs) {
    Simulation.totalTickInSimulation = 0;
    incomingCars = new ArrayList<Queue<Car>>();
    for (Queue<Car> cars : untouchedCars) {
      incomingCars.add(new LinkedList<Car>(cars));
    }
    for (int i = 0; i < lightConfigs.length; i++) {
      lights[i].reconfigure(lightConfigs[i]);
    }
  }

  public static long getTotalTickInSimulation() {
    return totalTickInSimulation;
  }
  public static void increaseTotalTicksInSimulation(long ticks) {
    totalTickInSimulation += ticks;
  }

  private void changeLights(int tick) {
    for (TrafficLight light : lights) {
      light.tick(tick);
    }
  }

  private void advanceCars(int tick) {
    for (AdvancableBlock block : advancables) {
      if (block.isOccupied()) block.getOccupant().tick(tick);
    }
    for (AdvancableBlock block : inroads) {
      if (block.isOccupied()) block.getOccupant().tick(tick);
    }
  }

  private void introduceNewCars(int tick) {
    for (int i = 0; i < incomingCars.size(); i++) {
      if (incomingCars.get(i).size() == 0) continue;
      if (incomingCars.get(i).element().getEnterTime() <= tick
        && !inroads[i].isOccupied()) {
        incomingCars.get(i).poll().introduce(inroads[i]);
      }
    }
  }

  private void doTick(int tick) {
    advanceCars(tick);
    introduceNewCars(tick);
    changeLights(tick);
  }

  private void registerAndClearUnfinishedCars() {
    for (Queue<Car> cars : incomingCars) {
      for (Car car : cars) {
        increaseTotalTicksInSimulation(Parameters.SIMULATION_DURATION - car.getEnterTime());
      }
    }
    for (AdvancableBlock block : advancables) {
      if (block.isOccupied()) {
        increaseTotalTicksInSimulation(Parameters.SIMULATION_DURATION - block.getOccupant().getEnterTime());
        block.removeOccupant();
      }
    }
    for (RoadBlock block : inroads) {
      if (block.isOccupied()) {
        increaseTotalTicksInSimulation(Parameters.SIMULATION_DURATION - block.getOccupant().getEnterTime());
        block.removeOccupant();
      }
    }
  }

  public double run() {
    int tick = 0;
    while (tick < Parameters.SIMULATION_DURATION) {
      doTick(tick);
      tick++;
    }
    registerAndClearUnfinishedCars();
    return 1.0 * totalTickInSimulation / totalNumberOfCars;
  }

}