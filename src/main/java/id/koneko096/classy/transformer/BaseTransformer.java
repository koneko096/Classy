package id.koneko096.classy.transformer;

import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import java.util.List;

public abstract class BaseTransformer {

  /**
   * Fits the transformer to the provided dataset. This method is used by transformers that need to
   * learn parameters (e.g., means, principal components) from the data.
   *
   * @param data The dataset to fit the transformer on.
   */
  public abstract void fit(InstanceSet data);

  /**
   * Applies the learned transformation to a new dataset.
   *
   * @param data The dataset to transform.
   * @return A new InstanceSet containing the transformed instances.
   */
  public abstract InstanceSet transform(InstanceSet data);

  /**
   * Applies the learned transformation to a single instance.
   *
   * @param instance The instance to transform.
   * @return A new Instance containing the transformed features.
   */
  public abstract Instance transform(Instance instance);

  /**
   * Returns a list of feature names after transformation. This can be useful if the transformation
   * changes the feature space (e.g., PCA reduces dimensions).
   *
   * @return A list of strings representing the names of the transformed features, or null if not
   *     applicable.
   */
  public List<String> getFeatureNames() {
    return null; // Default implementation, can be overridden by concrete transformers
  }
}
