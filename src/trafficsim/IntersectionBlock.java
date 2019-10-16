package trafficsim;
/**
 * IntersectionBlock
 */
public class IntersectionBlock extends AdvancableBlock {

  public IntersectionBlock(String identifier, AdvancableBlock nextBlock, int duration) {
    super("Intersection(" + identifier + ")", nextBlock, duration);
  }
}