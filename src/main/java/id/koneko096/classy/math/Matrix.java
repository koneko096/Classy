package id.koneko096.classy.math;

import java.util.Arrays;
import java.util.Objects;

public class Matrix {
  private final double[][] data;
  private final int rows;
  private final int cols;

  public Matrix(double[][] data) {
    Objects.requireNonNull(data, "Matrix data cannot be null.");
    if (data.length == 0 || data[0].length == 0) {
      throw new IllegalArgumentException("Matrix cannot be empty.");
    }
    this.rows = data.length;
    this.cols = data[0].length;

    // Ensure all rows have the same number of columns
    for (int i = 0; i < rows; i++) {
      Objects.requireNonNull(data[i], "Matrix row cannot be null.");
      if (data[i].length != cols) {
        throw new IllegalArgumentException(
            "All rows in the matrix must have the same number of columns.");
      }
    }

    // Deep copy the input data to ensure immutability
    this.data = new double[rows][cols];
    for (int i = 0; i < rows; i++) {
      System.arraycopy(data[i], 0, this.data[i], 0, cols);
    }
  }

  public Matrix(int rows, int cols) {
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException("Matrix dimensions must be positive.");
    }
    this.rows = rows;
    this.cols = cols;
    this.data = new double[rows][cols];
  }

  public int getRows() {
    return rows;
  }

  public int getCols() {
    return cols;
  }

  public double get(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      throw new IndexOutOfBoundsException("Matrix index out of bounds: (" + row + ", " + col + ")");
    }
    return data[row][col];
  }

  // --- Sub-task 1.4: Implement Matrix Operations ---

  public Matrix add(Matrix other) {
    if (this.rows != other.rows || this.cols != other.cols) {
      throw new IllegalArgumentException("Matrices must have the same dimensions for addition.");
    }
    double[][] resultData = new double[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        resultData[i][j] = this.data[i][j] + other.data[i][j];
      }
    }
    return new Matrix(resultData);
  }

  public Matrix multiply(double scalar) {
    double[][] resultData = new double[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        resultData[i][j] = this.data[i][j] * scalar;
      }
    }
    return new Matrix(resultData);
  }

  public Matrix transpose() {
    double[][] resultData = new double[cols][rows];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        resultData[j][i] = this.data[i][j];
      }
    }
    return new Matrix(resultData);
  }

  public Matrix multiply(Matrix other) {
    if (this.cols != other.rows) {
      throw new IllegalArgumentException(
          "Number of columns in the first matrix must equal the number of rows in the second matrix"
              + " for multiplication.");
    }

    double[][] resultData = new double[this.rows][other.cols];
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < other.cols; j++) {
        double sum = 0.0;
        for (int k = 0; k < this.cols; k++) {
          sum += this.data[i][k] * other.data[k][j];
        }
        resultData[i][j] = sum;
      }
    }
    return new Matrix(resultData);
  }

  public Vector multiply(Vector vector) {
    if (this.cols != vector.size()) {
      throw new IllegalArgumentException(
          "Number of columns in the matrix must equal the size of the vector for matrix-vector"
              + " multiplication.");
    }

    double[] resultComponents = new double[this.rows];
    for (int i = 0; i < this.rows; i++) {
      double sum = 0.0;
      for (int j = 0; j < this.cols; j++) {
        sum += this.data[i][j] * vector.get(j);
      }
      resultComponents[i] = sum;
    }
    return new Vector(resultComponents);
  }

  public static Vector multiply(Vector vector, Matrix matrix) {
    if (vector.size() != matrix.rows) {
      throw new IllegalArgumentException(
          "Vector size must equal the number of matrix rows for vector-matrix multiplication.");
    }

    // Conceptually, (1xn) * (nxm) = (1xm)
    // This is equivalent to multiplying the transposed vector by the matrix,
    // or a row vector times a matrix.
    // We can achieve this by creating a 1xN matrix from the vector and then performing matrix
    // multiplication,
    // or by directly calculating the result.

    // Direct calculation:
    double[] resultComponents = new double[matrix.cols];
    for (int j = 0; j < matrix.cols; j++) { // Iterate through columns of the matrix
      double sum = 0.0;
      for (int i = 0;
          i < vector.size();
          i++) { // Iterate through components of the vector (rows of the matrix)
        sum += vector.get(i) * matrix.get(i, j);
      }
      resultComponents[j] = sum;
    }
    return new Vector(resultComponents);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Matrix matrix = (Matrix) o;
    if (rows != matrix.rows || cols != matrix.cols) return false;
    for (int i = 0; i < rows; i++) {
      if (!Arrays.equals(data[i], matrix.data[i])) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(rows, cols);
    result = 31 * result + Arrays.deepHashCode(data);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Matrix(\n");
    for (int i = 0; i < rows; i++) {
      sb.append("  ").append(Arrays.toString(data[i]));
      if (i < rows - 1) {
        sb.append(",\n");
      }
    }
    sb.append("\n)");
    return sb.toString();
  }
}
