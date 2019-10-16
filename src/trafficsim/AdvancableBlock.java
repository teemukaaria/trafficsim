package trafficsim;

/**
 * AdvancableBlock
 */
public class AdvancableBlock extends PathBlock {

  protected int duration;
  private Car occupant = null;

  public AdvancableBlock(String name, PathBlock nextBlock, int duration) {
    super(name, new PathBlock[]{ nextBlock });
    this.duration = duration;
  }
  
  public int getDuration() {
    return duration;
  }
  public Car getOccupant() {
    return occupant;
  }
  public boolean isOccupied() {
    return occupant != null;
  }
  public PathBlock getNextBlock() {
    return super.getNextBlock(occupant);
  }

  public void setOccupant(Car occupant) {
    this.occupant = occupant;
  }
  public void removeOccupant() {
    occupant = null;
  }
}