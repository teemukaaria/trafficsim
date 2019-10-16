package trafficsim;
/**
 * PathBlock
 */
public abstract class PathBlock {

  private String name = "";
  private PathBlock[] nextBlocks = null;

  public PathBlock(String name, PathBlock[] nextBlocks) {
    this.name = name;
    this.nextBlocks = nextBlocks;
  }
  
  public String getName() {
    return name;
  }
  public PathBlock getNextBlock(Car occupant) {
    if (nextBlocks == null) return null;
    if (nextBlocks.length == 1) return nextBlocks[0];
    if (occupant == null) return null;
    return nextBlocks[occupant.getPath()];
  }
  public boolean hasNextBlock() {
    return nextBlocks != null && nextBlocks.length > 0 && nextBlocks[0] != null;
  }

  public String toString() {
    return name;
  }
}