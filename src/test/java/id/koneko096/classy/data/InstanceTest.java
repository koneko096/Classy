package id.koneko096.classy.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class InstanceTest {

  private static final double EPSILON = 1e-9; // Tolerance for double comparisons

  @Test
  void testConstructorAndGetters() {
    double[] values = new double[] {1.0, 2.0, 3.0};
    String label = "classA";
    Instance instance = new Instance(values, label);

    assertArrayEquals(new double[] {1.0, 2.0, 3.0}, instance.values(), EPSILON);
    assertEquals(label, instance.getLabel());
  }

  @Test
  void testEqualsAndHashCode() {
    Instance i1 = new Instance(new double[] {1.0}, "classA");
    Instance i2 = new Instance(new double[] {1.0}, "classA");
    Instance i3 = new Instance(new double[] {2.0}, "classA");
    Instance i4 = new Instance(new double[] {1.0}, "classB");

    assertEquals(i1, i2);
    assertNotEquals(i1, i3);
    assertNotEquals(i1, i4);
    assertEquals(i1.hashCode(), i2.hashCode());
  }
}
