package id.koneko096.classy.transformer;

import id.koneko096.classy.data.Header;
import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import id.koneko096.classy.math.Matrix;
import id.koneko096.classy.math.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PCATransformer extends BaseTransformer {

  private int componentNum;
  private Vector mean;
  private Matrix principalComponents; // Eigenvectors as columns

  public PCATransformer(int componentNum) {
    if (componentNum <= 0) {
      throw new IllegalArgumentException("Number of components must be positive.");
    }
    this.componentNum = componentNum;
  }

  @Override
  public void fit(InstanceSet data) {
    if (data.isEmpty()) {
      throw new IllegalArgumentException("Cannot fit PCA on an empty dataset.");
    }

    // 1. Convert InstanceSet to a Matrix
    int numInstances = data.size();
    List<String> attributeNames = data.getAttributeNames();
    if (attributeNames == null) {
      // Create temporary dummy names if header is missing
      int n = data.get(0).values().length;
      attributeNames = IntStream.range(0, n).mapToObj(i -> "attr" + i).collect(Collectors.toList());
    }
    int numFeatures = attributeNames.size();

    if (componentNum > numFeatures) {
      throw new IllegalArgumentException(
          "n_components cannot be greater than the number of features.");
    }

    double[][] dataMatrix = new double[numInstances][numFeatures];
    for (int i = 0; i < numInstances; i++) {
      dataMatrix[i] = data.get(i).values();
    }
    Matrix X = new Matrix(dataMatrix);

    // 2. Perform data centering (subtract mean)
    this.mean = calculateMean(X);
    Matrix centeredX = centerData(X, this.mean);

    // 3. Calculate covariance matrix
    Matrix covarianceMatrix = calculateCovarianceMatrix(centeredX);

    // 4. Perform eigenvalue decomposition on the covariance matrix
    double[][] covData = covarianceMatrix.getData();
    int size = covData.length;
    double[][] V = new double[size][size];
    for (int i = 0; i < size; i++) V[i][i] = 1.0;

    double[][] A = new double[size][size];
    for (int i = 0; i < size; i++) {
      System.arraycopy(covData[i], 0, A[i], 0, size);
    }

    // Jacobi eigenvalue algorithm
    int maxIterations = 1000;
    double eps = 1e-10;
    for (int iter = 0; iter < maxIterations; iter++) {
      // Find the largest off-diagonal element
      int p = 0, q = 1;
      double maxVal = Math.abs(A[0][1]);
      for (int i = 0; i < size; i++) {
        for (int j = i + 1; j < size; j++) {
          if (Math.abs(A[i][j]) > maxVal) {
            maxVal = Math.abs(A[i][j]);
            p = i;
            q = j;
          }
        }
      }

      if (maxVal < eps) break;

      // Compute rotation angle
      double theta = 0.5 * Math.atan2(2 * A[p][q], A[q][q] - A[p][p]);
      double cos = Math.cos(theta);
      double sin = Math.sin(theta);

      // Update A
      double app = A[p][p];
      double aqq = A[q][q];
      double apq = A[p][q];
      A[p][p] = cos * cos * app - 2 * sin * cos * apq + sin * sin * aqq;
      A[q][q] = sin * sin * app + 2 * sin * cos * apq + cos * cos * aqq;
      A[p][q] = A[q][p] = 0;

      for (int i = 0; i < size; i++) {
        if (i != p && i != q) {
          double aip = A[i][p];
          double aiq = A[i][q];
          A[i][p] = A[p][i] = cos * aip - sin * aiq;
          A[i][q] = A[q][i] = sin * aip + cos * aiq;
        }
      }

      // Update V (eigenvectors)
      for (int i = 0; i < size; i++) {
        double vip = V[i][p];
        double viq = V[i][q];
        V[i][p] = cos * vip - sin * viq;
        V[i][q] = sin * vip + cos * viq;
      }
    }

    // Extract eigenvalues (diagonal of A) and sort eigenvectors
    Integer[] indices = new Integer[size];
    for (int i = 0; i < size; i++) indices[i] = i;
    Arrays.sort(indices, (i, j) -> Double.compare(A[j][j], A[i][i]));

    double[][] principalComponentsData = new double[size][componentNum];
    for (int j = 0; j < componentNum; j++) {
      int eigenvectorIdx = indices[j];
      for (int i = 0; i < size; i++) {
        principalComponentsData[i][j] = V[i][eigenvectorIdx];
      }
    }
    this.principalComponents = new Matrix(principalComponentsData);
  }

  @Override
  public InstanceSet transform(InstanceSet data) {
    if (this.mean == null || this.principalComponents == null) {
      throw new IllegalStateException("Transformer has not been fitted yet.");
    }

    int numInstances = data.size();
    List<String> attributeNames = data.getAttributeNames();
    int numFeatures =
        (attributeNames != null) ? attributeNames.size() : data.get(0).values().length;

    double[][] dataMatrix = new double[numInstances][numFeatures];
    for (int i = 0; i < numInstances; i++) {
      dataMatrix[i] = data.get(i).values();
    }
    Matrix X = new Matrix(dataMatrix);

    // Center the new data using the learned mean
    Matrix centeredX = centerData(X, this.mean);

    // Project the data onto the principal components
    // Result: centeredX (numInstances x numFeatures) * principalComponents
    // (numFeatures x
    // n_components)
    // Result is (numInstances x n_components)
    Matrix transformedMatrix = centeredX.multiply(this.principalComponents);

    List<Instance> transformedInstances = new ArrayList<>();
    List<String> featureNames = getFeatureNames();
    for (int i = 0; i < transformedMatrix.getRows(); i++) {
      double[] values = new double[transformedMatrix.getCols()];
      for (int j = 0; j < transformedMatrix.getCols(); j++) {
        values[j] = transformedMatrix.get(i, j);
      }
      transformedInstances.add(new Instance(values, data.get(i).getLabel()));
    }

    Header newHeader =
        Header.builder()
            .attributeNames(featureNames)
            .attributeNameSet(new HashSet<>(featureNames))
            .attributeCandidates(new HashMap<>())
            .attributeTypes(
                IntStream.range(0, componentNum)
                    .mapToObj(i -> Double.class)
                    .collect(Collectors.toList()))
            .build();

    return new InstanceSet(transformedInstances, newHeader, data.getName());
  }

  @Override
  public Instance transform(Instance instance) {
    if (this.mean == null || this.principalComponents == null) {
      throw new IllegalStateException("Transformer has not been fitted yet.");
    }

    // Center the instance features
    double[] centeredFeatures = new double[instance.values().length];
    for (int i = 0; i < instance.values().length; i++) {
      centeredFeatures[i] = instance.values()[i] - this.mean.get(i);
    }
    Vector centeredVector = new Vector(centeredFeatures);

    // Project the instance onto the principal components
    Vector transformedVector = Matrix.multiply(centeredVector, this.principalComponents);

    double[] values = new double[transformedVector.size()];
    for (int i = 0; i < transformedVector.size(); i++) {
      values[i] = transformedVector.get(i);
    }

    return new Instance(values, instance.getLabel());
  }

  private Vector calculateMean(Matrix data) {
    int numFeatures = data.getCols();
    double[] meanComponents = new double[numFeatures];
    for (int j = 0; j < numFeatures; j++) {
      double sum = 0.0;
      for (int i = 0; i < data.getRows(); i++) {
        sum += data.get(i, j);
      }
      meanComponents[j] = sum / data.getRows();
    }
    return new Vector(meanComponents);
  }

  private Matrix centerData(Matrix data, Vector mean) {
    double[][] centeredData = new double[data.getRows()][data.getCols()];
    for (int i = 0; i < data.getRows(); i++) {
      for (int j = 0; j < data.getCols(); j++) {
        centeredData[i][j] = data.get(i, j) - mean.get(j);
      }
    }
    return new Matrix(centeredData);
  }

  private Matrix calculateCovarianceMatrix(Matrix centeredData) {
    int numFeatures = centeredData.getCols();
    int numInstances = centeredData.getRows();

    // Covariance Matrix = (X^T * X) / (n-1)
    Matrix centeredDataTranspose = centeredData.transpose();
    Matrix covarianceMatrix = centeredDataTranspose.multiply(centeredData);
    return covarianceMatrix.multiply(1.0 / (numInstances - 1)); // Bessel's correction
  }

  @Override
  public List<String> getFeatureNames() {
    if (principalComponents == null) {
      return null;
    }
    return IntStream.range(0, componentNum)
        .mapToObj(i -> "PC" + (i + 1))
        .collect(Collectors.toList());
  }
}
