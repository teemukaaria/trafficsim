package trafficsim;
/**
 * Car
 */
public class Car {

  private int path;
  private int enterTime;
  private AdvancableBlock currentBlock;
  private int counter;
  private int speed = Parameters.FULL_SPEED;

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
  public int getSpeed() {
    return speed;
  }

  public void checkOut(int tick) {
    currentBlock.removeOccupant();
    currentBlock = null;
    Simulation.increaseTotalTicksInSimulation(tick - enterTime);
  }

  public void introduce(AdvancableBlock block) {
    currentBlock = block;
    currentBlock.setOccupant(this);
    counter = block.getDuration();
    speed = Parameters.FULL_SPEED;
    checkSpeed();
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
    counter += block.getDuration();
  }

  private void accelerate() {
    speed = Math.min(Parameters.FULL_SPEED, speed + Parameters.ACCELERATE_INCREMENT);
  }
  private void checkSpeed() {
    PathBlock nextBlock = currentBlock.getNextBlock(this);
    if (nextBlock == null) {
      accelerate();
      return;
    }
    // complete stop if facing red or yellow light on the next block;
    if (nextBlock instanceof TrafficLight) {
      if (((TrafficLight)nextBlock).getColor() != LightColor.GREEN) {
        speed = 0;
        return;
      } else {
        nextBlock = nextBlock.getNextBlock(this);
      }
    }
    if (nextBlock == null) {
      accelerate();
      return;
    }
    // if next block is occupied adopt the same speed
    if (((AdvancableBlock)nextBlock).isOccupied()) {
      speed = ((AdvancableBlock)nextBlock).getOccupant().getSpeed();
      return;
    }
    int i = 1;
    while (i < 4) {
      nextBlock = nextBlock.getNextBlock(this);
      if (nextBlock == null) {
        accelerate();
        return;
      }
      if (nextBlock instanceof TrafficLight) {
        // red light is coming -> start deaccelerating
        if (((TrafficLight)nextBlock).getColor() != LightColor.GREEN) {
          speed = Math.max(0, Math.min(speed - Parameters.ACCELERATE_INCREMENT, speed / 4 * (4 - i)));
          return;
        }
      } else {
        if (((AdvancableBlock)nextBlock).isOccupied()) {
          Car car = ((AdvancableBlock)nextBlock).getOccupant();
          // car cruising faster -> start accelerating
          if (car.getSpeed() >= this.getSpeed()) {
            accelerate();
          }
          // car is going slower -> be prepared to stop
          else {
            speed = Math.max(
              0,
              speed + ((car.getSpeed() - speed) / 4 * (4 - i))
            );
          }
          return;
        }
        i++;
      }
    }
    // no cars occupying the next 4 block -> accelerate
    accelerate();
  }

  public void tick(int tick) {
    checkSpeed();
    // if current action is in progress, do nothing
    if (counter > 0) {
      counter -= speed;
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