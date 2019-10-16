package trafficsim;

enum LightColor {
  GREEN, AMBER, RED
}

/**
 * TrafficLight
 */
public class TrafficLight extends PathBlock {
  private LightColor[] configuration;
  private LightColor color = LightColor.GREEN;

  public TrafficLight(String nro, IntersectionBlock[] nextBlocks, LightColor[] config) {
    super("Light(" + nro + ")", nextBlocks);
    init(config);
  }
  public TrafficLight(String nro, IntersectionBlock nextBlock, LightColor[] config) {
    super("Light(" + nro + ")", new PathBlock[]{ nextBlock });
    init(config);
  }

  public void reconfigure(LightColor[] config) {
    configuration = config;
  }

  public LightColor getColor() {
    return color;
  }

  public void init(LightColor[] configuration) {
    this.configuration = configuration;
    color = configuration[0];
  }

  public void tick(int tick) {
    LightColor nextColor =  configuration[tick % Parameters.LIGHT_CYCLE_DUARTION];
    if (nextColor == LightColor.GREEN
      && configuration[(tick + Parameters.AMBER_LOOKAHEAD) % Parameters.LIGHT_CYCLE_DUARTION] == LightColor.RED)
      nextColor = LightColor.AMBER;
    color = nextColor;
  }
}