package trafficsim;
/**
 * RoadBlock
 */
public class RoadBlock extends AdvancableBlock {

  public RoadBlock(String identifier, PathBlock nextBlock) {
    super("Road(" + identifier + ")", nextBlock, Parameters.BLOCK_DURATION);
  }
  public RoadBlock(String identifier, PathBlock nextBlock, int duration) {
    super("Road(" + identifier + ")", nextBlock, duration);
  }
}