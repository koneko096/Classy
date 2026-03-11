package id.koneko096.classy.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class VectorTest {

  private static final double EPSILON = 1e-9; // Tolerance for double comparisons

  @Test
  void testConstructorAndGet() {
    Vector v = new Vector(1.0, 2.0, 3.0);
    assertEquals(3, v.size());
    assertEquals(1.0, v.get(0), EPSILON);
    assertEquals(2.0, v.get(1), EPSILON);
    assertEquals(3.0, v.get(2), EPSILON);
  }

  @Test
  void testConstructorWithEmptyArrayThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Vector());
  }

  @Test
  void testConstructorWithNullArrayThrowsException() {
    assertThrows(NullPointerException.class, () -> new Vector((double[]) null));
  }

  @Test
  void testGetOutOfBoundsThrowsException() {
    Vector v = new Vector(1.0, 2.0);
    assertThrows(IndexOutOfBoundsException.class, () -> v.get(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> v.get(2));
  }

  @Test
  void testDotProduct() {
    Vector v1 = new Vector(1.0, 2.0, 3.0);
    Vector v2 = new Vector(4.0, 5.0, 6.0);
    assertEquals(32.0, v1.dotProduct(v2), EPSILON); // 1*4 + 2*5 + 3*6 = 4 + 10 + 18 = 32
  }

  @Test
  void testDotProductWithDifferentSizesThrowsException() {
    Vector v1 = new Vector(1.0, 2.0);
    Vector v2 = new Vector(4.0, 5.0, 6.0);
    assertThrows(IllegalArgumentException.class, () -> v1.dotProduct(v2));
  }

  @Test
  void testAdd() {
    Vector v1 = new Vector(1.0, 2.0, 3.0);
    Vector v2 = new Vector(4.0, 5.0, 6.0);
    Vector expected = new Vector(5.0, 7.0, 9.0);
    assertEquals(expected, v1.add(v2));
  }

  @Test
  void testAddWithDifferentSizesThrowsException() {
    Vector v1 = new Vector(1.0, 2.0);
    Vector v2 = new Vector(4.0, 5.0, 6.0);
    assertThrows(IllegalArgumentException.class, () -> v1.add(v2));
  }

  @Test
  void testSubtract() {
    Vector v1 = new Vector(4.0, 5.0, 6.0);
    Vector v2 = new Vector(1.0, 2.0, 3.0);
    Vector expected = new Vector(3.0, 3.0, 3.0);
    assertEquals(expected, v1.subtract(v2));
  }

  @Test
  void testSubtractWithDifferentSizesThrowsException() {
    Vector v1 = new Vector(4.0, 5.0);
    Vector v2 = new Vector(1.0, 2.0, 3.0);
    assertThrows(IllegalArgumentException.class, () -> v1.subtract(v2));
  }

  @Test
  void testScalarMultiply() {
    Vector v = new Vector(1.0, 2.0, 3.0);
    double scalar = 2.0;
    Vector expected = new Vector(2.0, 4.0, 6.0);
    assertEquals(expected, v.multiply(scalar));
  }

  @Test
  void testEqualsAndHashCode() {
    Vector v1 = new Vector(1.0, 2.0, 3.0);
    Vector v2 = new Vector(1.0, 2.0, 3.0);
    Vector v3 = new Vector(4.0, 5.0, 6.0);

    assertEquals(v1, v2);
    assertNotEquals(v1, v3);
    assertEquals(v1.hashCode(), v2.hashCode());
    assertNotEquals(v1.hashCode(), v3.hashCode());
    assertNotEquals(v1, null);
    assertNotEquals(v1, new Object());
  }

  @Test
  void testToString() {
    Vector v = new Vector(1.0, 2.0, 3.0);
    assertEquals("Vector[1.0, 2.0, 3.0]", v.toString());
  }
}
