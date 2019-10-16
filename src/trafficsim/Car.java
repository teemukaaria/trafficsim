package trafficsim;
/**
 * Car
 */
public class Car {

  private int path;
  private int enterTime;
  private AdvancableBlock currentBlock;
  private int counter;

  public Car(int enterTime, int path) {
    this.enterTime = enterTime;
    this.path = path;
  }
  public Car(int[] params) {
    this.enterTime = params[0];
    this.path = params[1];
  }

  public int getPath() {
    return path;
  }
  public int getEnterTime() {
    return enterTime;
  }

  public void checkOut(int tick) {
    currentBlock.removeOccupant();
    Simulation.increaseTotalTicksInSimulation(tick - enterTime);
  }

  public void introduce(AdvancableBlock block) {
    currentBlock = block;
    currentBlock.setOccupant(this);
    counter = block.getDuration();
  }

  private boolean checkIfRoomOnTheOtherSide(TrafficLight lightBlock) {
    int carsInIntersection = 0;
    PathBlock checkingBlock = lightBlock;
    while (checkingBlock.hasNextBlock()
      && checkingBlock.getNextBlock(this) instanceof IntersectionBlock) {
      checkingBlock = checkingBlock.getNextBlock(this);
      if (((IntersectionBlock) checkingBlock).isOccupied()) carsInIntersection++;
    }
    while (carsInIntersection > 0
      && checkingBlock.hasNextBlock()
      && !((RoadBlock) checkingBlock.getNextBlock(this)).isOccupied()) {
      checkingBlock = checkingBlock.getNextBlock(this);
      carsInIntersection--;
    }
    return carsInIntersection <= 0;
  }

  private void moveToBlock(AdvancableBlock block) {
    currentBlock.removeOccupant();
    currentBlock = block;
    block.setOccupant(this);
    counter = block.getDuration();
  }

  public void tick(int tick) {
    // if current action is in progress, do nothing
    if (counter > 0) {
      counter--;
      return;
    }
    // endgame, car is out of simulation
    if (!currentBlock.hasNextBlock()) {
      checkOut(tick);
    }
    // try to move to the next block
    else {
      PathBlock nextBlock = currentBlock.getNextBlock();
      // at traffic light sitting or at speed
      if (nextBlock instanceof TrafficLight) {
        switch (((TrafficLight) nextBlock).getColor()) {
          case GREEN:
            // don't move unless there's room on the other side
            if (checkIfRoomOnTheOtherSide((TrafficLight) nextBlock))
              moveToBlock((AdvancableBlock) nextBlock.getNextBlock(this));
            break;
          case AMBER:
          case RED:
            break;
        }
      }
      // next block is either road or second intersection block
      else {
        if (!((AdvancableBlock) nextBlock).isOccupied())
          moveToBlock((AdvancableBlock) nextBlock);
      }
    }
  }
}