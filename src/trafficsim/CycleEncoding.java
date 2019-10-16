package trafficsim;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * CycleEncoding
 * 0: green: 1, 2, 3, 4
 * 1: green: 1, 2, 3, a,
 * 2: green: 4, 5, 6, 7
 * 3: green: 4, 10
 * 4: green: a, 10
 */
public class CycleEncoding {
  private int[] leftSituations = new int[Parameters.ENCODING_BLOCKS];
  private int[] rightSituations = new int[Parameters.ENCODING_BLOCKS];
  private Random rnd = new Random();

  public CycleEncoding() {
    for (int i = 0; i < Parameters.ENCODING_BLOCKS; i++) {
      leftSituations[i] = rnd.nextInt(5);
      rightSituations[i] = rnd.nextInt(5);
    }
  }
  public CycleEncoding(int[] left, int[] right) {
    leftSituations = left;
    rightSituations = right;
  }
  public CycleEncoding(CycleEncoding father, CycleEncoding mother, double mutationTreshold) {
    int crossOverPoint = rnd.nextInt(Parameters.ENCODING_BLOCKS);
    for (int i = 0; i < crossOverPoint; i++) {
      leftSituations[i] = father.getLeft(i);
      rightSituations[i] = father.getRight(i);
    }
    for (int i = crossOverPoint; i < Parameters.ENCODING_BLOCKS; i++) {
      leftSituations[i] = mother.getLeft(i);
      rightSituations[i] = mother.getRight(i);
    }
    if (rnd.nextDouble() < mutationTreshold) {
      int index = rnd.nextInt(2 * Parameters.ENCODING_BLOCKS);
      if (index < Parameters.ENCODING_BLOCKS) {
        leftSituations[index] = rnd.nextInt(5);
      } else {
        rightSituations[index % Parameters.ENCODING_BLOCKS] = rnd.nextInt(5);
      }
    }
  }

  public int getLeft(int index) {
    return leftSituations[index];
  }
  public int getRight(int index) {
    return rightSituations[index];
  }

  public String toString() {
    String left = "Left("
      + String.join(
        ", ",
        Arrays.stream(leftSituations).mapToObj((int a)->""+a).collect(Collectors.toList())
      )
      + ")";
    String right = "Right("
      + String.join(
        ", ",
        Arrays.stream(rightSituations).mapToObj((int a)->""+a).collect(Collectors.toList())
      )
      + ")";
    return left + "\n" + right;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof CycleEncoding)) return false;
    for (int i = 0; i < Parameters.ENCODING_BLOCKS; i++) {
      if (getLeft(i) != ((CycleEncoding)obj).getLeft(i)) return false;
      if (getRight(i) != ((CycleEncoding)obj).getRight(i)) return false;
    }
    return true;
  }
}