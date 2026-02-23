package id.koneko096.classy.math;

import java.util.Arrays;
import java.util.Objects;

public class Vector {
  private final double[] components;

  public Vector(double... components) {
    Objects.requireNonNull(components, "Components array cannot be null.");
    if (components.length == 0) {
      throw new IllegalArgumentException("Vector cannot be empty.");
    }
    this.components = Arrays.copyOf(components, components.length);
  }

  public int size() {
    return components.length;
  }

  public double get(int index) {
    if (index < 0 || index >= components.length) {
      throw new IndexOutOfBoundsException(
          "Index " + index + " is out of bounds for vector of size " + components.length);
    }
    return components[index];
  }

  public double[] getComponents() {
    return Arrays.copyOf(components, components.length);
  }

  public Vector add(Vector other) {
    if (this.size() != other.size()) {
      throw new IllegalArgumentException("Vectors must have the same size for addition.");
    }
    double[] resultComponents = new double[this.size()];
    for (int i = 0; i < this.size(); i++) {
      resultComponents[i] = this.components[i] + other.components[i];
    }
    return new Vector(resultComponents);
  }

  public Vector subtract(Vector other) {
    if (this.size() != other.size()) {
      throw new IllegalArgumentException("Vectors must have the same size for subtraction.");
    }
    double[] resultComponents = new double[this.size()];
    for (int i = 0; i < this.size(); i++) {
      resultComponents[i] = this.components[i] - other.components[i];
    }
    return new Vector(resultComponents);
  }

  public Vector multiply(double scalar) {
    double[] resultComponents = new double[this.size()];
    for (int i = 0; i < this.size(); i++) {
      resultComponents[i] = this.components[i] * scalar;
    }
    return new Vector(resultComponents);
  }

  public double dotProduct(Vector other) {
    if (this.size() != other.size()) {
      throw new IllegalArgumentException("Vectors must have the same size for dot product.");
    }
    double sum = 0.0;
    for (int i = 0; i < this.size(); i++) {
      sum += this.components[i] * other.components[i];
    }
    return sum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vector vector = (Vector) o;
    return Arrays.equals(components, vector.components);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(components);
  }

  @Override
  public String toString() {
    return "Vector" + Arrays.toString(components);
  }
}
