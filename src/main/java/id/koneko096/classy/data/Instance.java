package id.koneko096.classy.data;

import java.util.List;
import lombok.Data;

/**
 * Class instance
 *
 * @author Afrizal Fikri
 */
@Data
public class Instance {
  private double[] values;
  private String label;

  /**
   * Constructor
   *
   * @param values of the attributes
   */
  public Instance(double[] values) {
    this.values = values;
    this.label = null;
  }

  /**
   * Constructor
   *
   * @param values of the attributes
   * @param label classification label
   */
  public Instance(double[] values, String label) {
    this.values = values;
    this.label = label;
  }

  public double[] values() {
    return this.values;
  }

  public double[] getValues() {
    return this.values;
  }

  public String getLabel() {
    return this.label;
  }

  public void updateValues(List<Integer> keptIndices) {
    double[] newValues = new double[keptIndices.size()];
    for (int i = 0; i < keptIndices.size(); i++) {
      newValues[i] = this.values[keptIndices.get(i)];
    }
    this.values = newValues;
  }
}
