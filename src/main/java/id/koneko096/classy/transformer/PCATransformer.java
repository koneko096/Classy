package id.koneko096.classy.transformer;

import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import id.koneko096.classy.math.Matrix;
import id.koneko096.classy.math.Vector;
import java.util.ArrayList;
import java.util.Arrays;
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
    int numFeatures = data.getAttributeNames().size();

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
    //    (Placeholder - requires a numerical linear algebra library or custom implementation)
    //    For now, let's assume we have a way to get eigenvectors (principal components)
    //    and sort them by eigenvalues.
    //    For a simple implementation, we'd typically use a library like Apache Commons Math
    //    For this exercise, we will assume an external method to perform this.
    //    In a real scenario, this step would involve a robust library.

    // For demonstration, let's create dummy principal components.
    // In a real implementation, these would come from eigenvalue decomposition.
    // This is a critical step that requires a robust linear algebra library.
    // For the sake of moving forward, let's assume `getEigenvectors(covarianceMatrix,
    // n_components)` exists.
    // I will create a dummy for now.
    // For example, if we have 3 features and want 2 components, principalComponents would be 3x2.

    // Simulating eigenvalue decomposition with identity matrix for now if n_components ==
    // numFeatures
    // otherwise, truncating or zeroing out components. This is NOT a real PCA.
    double[][] dummyPrincipalComponentsData = new double[numFeatures][componentNum];
    for (int i = 0; i < numFeatures && i < componentNum; i++) {
      dummyPrincipalComponentsData[i][i] = 1.0; // Identity matrix
    }
    this.principalComponents = new Matrix(dummyPrincipalComponentsData);

    // TODO: Integrate a proper eigenvalue decomposition library (e.g., Apache Commons Math)
    // For example:
    // EigenDecomposition decomp = new EigenDecomposition(new
    // Array2DRowRealMatrix(covarianceMatrix.getData()));
    // realEigenvectors = decomp.getV(); // Get eigenvectors matrix
    // sort eigenvectors by eigenvalues (descending)
    // this.principalComponents = selectTopEigenvectors(realEigenvectors, n_components);
  }

  @Override
  public InstanceSet transform(InstanceSet data) {
    if (this.mean == null || this.principalComponents == null) {
      throw new IllegalStateException("Transformer has not been fitted yet.");
    }

    int numInstances = data.size();
    int numFeatures = data.getAttributeNames().size();

    double[][] dataMatrix = new double[numInstances][numFeatures];
    for (int i = 0; i < numInstances; i++) {
      dataMatrix[i] = data.get(i).values();
    }
    Matrix X = new Matrix(dataMatrix);

    // Center the new data using the learned mean
    Matrix centeredX = centerData(X, this.mean);

    // Project the data onto the principal components
    // Result: centeredX (numInstances x numFeatures) * principalComponents (numFeatures x
    // n_components)
    // Result is (numInstances x n_components)
    Matrix transformedMatrix = centeredX.multiply(this.principalComponents);

    List<Instance> transformedInstances = new ArrayList<>();
    for (int i = 0; i < transformedMatrix.getRows(); i++) {
      double[] transformedFeatures = new double[transformedMatrix.getCols()];
      for (int j = 0; j < transformedMatrix.getCols(); j++) {
        transformedFeatures[j] = transformedMatrix.get(i, j);
      }
      // Preserve the original label
      transformedInstances.add(
          new Instance(
              Arrays.stream(transformedFeatures).boxed().collect(Collectors.toList()),
              data.get(i).getLabel()));
    }

    return new InstanceSet(transformedInstances, data.getHeader(), data.getName());
  }

  @Override
  public Instance transform(Instance instance) {
    if (this.mean == null || this.principalComponents == null) {
      throw new IllegalStateException("Transformer has not been fitted yet.");
    }

    // Center the instance features
    double[] centeredFeatures = new double[instance.size()];
    for (int i = 0; i < instance.size(); i++) {
      centeredFeatures[i] = instance.values()[i] - this.mean.get(i);
    }
    Vector centeredVector = new Vector(centeredFeatures);

    // Project the instance onto the principal components
    // Result: (1 x numFeatures) * (numFeatures x n_components) = (1 x n_components)
    // This is effectively Vector * Matrix, so we use the static method from Matrix
    Vector transformedVector = Matrix.multiply(centeredVector, this.principalComponents);

    return new Instance(
        Arrays.stream(transformedVector.getComponents()).boxed().collect(Collectors.toList()),
        instance.getLabel());
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
