package id.koneko096.classy.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MatrixTest {

  private static final double EPSILON = 1e-9; // Tolerance for double comparisons

  @Test
  void testConstructorWith2DArray() {
    double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
    Matrix m = new Matrix(data);
    assertEquals(2, m.getRows());
    assertEquals(2, m.getCols());
    assertEquals(1.0, m.get(0, 0), EPSILON);
    assertEquals(4.0, m.get(1, 1), EPSILON);
  }

  @Test
  void testConstructorWithDimensions() {
    Matrix m = new Matrix(3, 2);
    assertEquals(3, m.getRows());
    assertEquals(2, m.getCols());
    // All elements should be 0.0 by default
    assertEquals(0.0, m.get(0, 0), EPSILON);
    assertEquals(0.0, m.get(2, 1), EPSILON);
  }

  @Test
  void testConstructorWithNullDataThrowsException() {
    assertThrows(NullPointerException.class, () -> new Matrix((double[][]) null));
  }

  @Test
  void testConstructorWithEmptyDataThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Matrix(new double[0][0]));
    assertThrows(IllegalArgumentException.class, () -> new Matrix(new double[1][0]));
    assertThrows(IllegalArgumentException.class, () -> new Matrix(new double[0][1]));
  }

  @Test
  void testConstructorWithRaggedArrayThrowsException() {
    double[][] data = {{1.0, 2.0}, {3.0}};
    assertThrows(IllegalArgumentException.class, () -> new Matrix(data));
  }

  @Test
  void testGetOutOfBoundsThrowsException() {
    Matrix m = new Matrix(2, 2);
    assertThrows(IndexOutOfBoundsException.class, () -> m.get(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> m.get(0, -1));
    assertThrows(IndexOutOfBoundsException.class, () -> m.get(2, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> m.get(0, 2));
  }

  @Test
  void testAdd() {
    double[][] data1 = {{1.0, 2.0}, {3.0, 4.0}};
    double[][] data2 = {{5.0, 6.0}, {7.0, 8.0}};
    Matrix m1 = new Matrix(data1);
    Matrix m2 = new Matrix(data2);
    Matrix expected = new Matrix(new double[][] {{6.0, 8.0}, {10.0, 12.0}});
    assertEquals(expected, m1.add(m2));
  }

  @Test
  void testAddWithDifferentDimensionsThrowsException() {
    Matrix m1 = new Matrix(new double[][] {{1.0, 2.0}});
    Matrix m2 = new Matrix(2, 2);
    assertThrows(IllegalArgumentException.class, () -> m1.add(m2));
  }

  @Test
  void testScalarMultiply() {
    double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
    Matrix m = new Matrix(data);
    double scalar = 2.0;
    Matrix expected = new Matrix(new double[][] {{2.0, 4.0}, {6.0, 8.0}});
    assertEquals(expected, m.multiply(scalar));
  }

  @Test
  void testTranspose() {
    double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
    Matrix m = new Matrix(data);
    Matrix expected = new Matrix(new double[][] {{1.0, 4.0}, {2.0, 5.0}, {3.0, 6.0}});
    assertEquals(expected, m.transpose());
  }

  @Test
  void testMatrixMultiply() {
    double[][] data1 = {{1.0, 2.0}, {3.0, 4.0}}; // 2x2
    double[][] data2 = {{5.0, 6.0, 7.0}, {8.0, 9.0, 10.0}}; // 2x3
    Matrix m1 = new Matrix(data1);
    Matrix m2 = new Matrix(data2);
    // Expected: (1*5+2*8, 1*6+2*9, 1*7+2*10) = (21, 24, 27)
    //           (3*5+4*8, 3*6+4*9, 3*7+4*10) = (47, 54, 61)
    Matrix expected = new Matrix(new double[][] {{21.0, 24.0, 27.0}, {47.0, 54.0, 61.0}}); // 2x3
    assertEquals(expected, m1.multiply(m2));
  }

  @Test
  void testMatrixMultiplyWithIncompatibleDimensionsThrowsException() {
    Matrix m1 = new Matrix(2, 3);
    Matrix m2 = new Matrix(2, 2);
    assertThrows(IllegalArgumentException.class, () -> m1.multiply(m2));
  }

  @Test
  void testMatrixVectorMultiply() {
    double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}}; // 2x3
    Matrix m = new Matrix(data);
    Vector v = new Vector(7.0, 8.0, 9.0); // 3x1
    // Expected: (1*7+2*8+3*9) = (7+16+27) = 50
    //           (4*7+5*8+6*9) = (28+40+54) = 122
    Vector expected = new Vector(50.0, 122.0); // 2x1
    assertEquals(expected, m.multiply(v));
  }

  @Test
  void testMatrixVectorMultiplyWithIncompatibleDimensionsThrowsException() {
    Matrix m = new Matrix(2, 3);
    Vector v = new Vector(1.0, 2.0); // Size 2
    assertThrows(IllegalArgumentException.class, () -> m.multiply(v));
  }

  @Test
  void testStaticVectorMatrixMultiply() {
    Vector v = new Vector(1.0, 2.0); // 1x2
    double[][] data = {{3.0, 4.0, 5.0}, {6.0, 7.0, 8.0}}; // 2x3
    Matrix m = new Matrix(data);
    // Expected: (1*3+2*6, 1*4+2*7, 1*5+2*8) = (3+12, 4+14, 5+16) = (15, 18, 21)
    Vector expected = new Vector(15.0, 18.0, 21.0); // 1x3
    assertEquals(expected, Matrix.multiply(v, m));
  }

  @Test
  void testStaticVectorMatrixMultiplyWithIncompatibleDimensionsThrowsException() {
    Vector v = new Vector(1.0, 2.0, 3.0); // Size 3
    Matrix m = new Matrix(2, 2);
    assertThrows(IllegalArgumentException.class, () -> Matrix.multiply(v, m));
  }

  @Test
  void testEqualsAndHashCode() {
    double[][] data1 = {{1.0, 2.0}, {3.0, 4.0}};
    double[][] data2 = {{1.0, 2.0}, {3.0, 4.0}};
    double[][] data3 = {{5.0, 6.0}, {7.0, 8.0}};

    Matrix m1 = new Matrix(data1);
    Matrix m2 = new Matrix(data2);
    Matrix m3 = new Matrix(data3);

    assertEquals(m1, m2);
    assertNotEquals(m1, m3);
    assertEquals(m1.hashCode(), m2.hashCode());
    assertNotEquals(m1.hashCode(), m3.hashCode());
    assertNotEquals(m1, null);
    assertNotEquals(m1, new Object());
  }

  @Test
  void testToString() {
    double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
    Matrix m = new Matrix(data);
    String expected = "Matrix(\n  [1.0, 2.0],\n  [3.0, 4.0]\n)";
    assertEquals(expected, m.toString());
  }
}
