package trafficsim;

public class Tuple<X, Y> { 
  public final X x; 
  public final Y y; 
  public Tuple(X x, Y y) { 
    this.x = x; 
    this.y = y; 
  } 

  public boolean equals(Object obj) {
    return obj instanceof Tuple<?, ?> && x.equals(((Tuple<?,?>)(obj)).x) && y.equals(((Tuple<?,?>)(obj)).y);
  }
}
