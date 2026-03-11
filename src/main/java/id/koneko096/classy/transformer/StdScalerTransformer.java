package id.koneko096.classy.transformer;

import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import java.util.List;
import java.util.stream.Collectors;

public class StdScalerTransformer extends BaseTransformer {

  private double[] means;
  private double[] stdDevs;
  private List<String> featureNames;

  @Override
  public void fit(InstanceSet data) {
    if (data.isEmpty()) {
      throw new IllegalArgumentException("Cannot fit StdScaler on an empty dataset.");
    }

    int numInstances = data.size();
    int numFeatures = data.get(0).values().length;
    this.featureNames = data.getAttributeNames();

    this.means = new double[numFeatures];
    this.stdDevs = new double[numFeatures];

    double[] sum = new double[numFeatures];
    double[] sumSq = new double[numFeatures];

    for (Instance instance : data) {
      double[] values = instance.values();
      for (int j = 0; j < numFeatures; j++) {
        sum[j] += values[j];
        sumSq[j] += values[j] * values[j];
      }
    }

    for (int j = 0; j < numFeatures; j++) {
      this.means[j] = sum[j] / numInstances;
      double variance = (sumSq[j] / numInstances) - (this.means[j] * this.means[j]);
      // Small epsilon to prevent precision issues for zero variance
      this.stdDevs[j] = Math.sqrt(Math.max(variance, 0.0));
    }
  }

  @Override
  public InstanceSet transform(InstanceSet data) {
    if (this.means == null) {
      throw new IllegalStateException("Transformer has not been fitted yet.");
    }

    List<Instance> transformedInstances =
        data.stream().map(this::transform).collect(Collectors.toList());

    return new InstanceSet(transformedInstances, data.getHeader(), data.getName());
  }

  @Override
  public Instance transform(Instance instance) {
    if (this.means == null) {
      throw new IllegalStateException("Transformer has not been fitted yet.");
    }

    double[] originalValues = instance.values();
    double[] scaledValues = new double[originalValues.length];

    for (int j = 0; j < originalValues.length; j++) {
      double scaledValue = originalValues[j] - this.means[j];
      if (this.stdDevs[j] > 1e-15) { // Protect against division by zero
        scaledValue /= this.stdDevs[j];
      }
      scaledValues[j] = scaledValue;
    }

    return new Instance(scaledValues, instance.getLabel());
  }

  @Override
  public List<String> getFeatureNames() {
    return this.featureNames;
  }
}
