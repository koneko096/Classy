package id.koneko096.classy.util;

import id.koneko096.classy.data.Attribute;
import id.koneko096.classy.data.Instance;
import java.util.List;
import java.util.stream.Collectors;

public class DataPreparationUtil {

  private DataPreparationUtil() {}

  public static <T> List<Double> convertToListOfDouble(Instance<T> instance) {
    try {
      return instance.stream().map(i -> (Double) i.getValue()).collect(Collectors.toList());
    } catch (ClassCastException e) {
      throw new RuntimeException("Fails to convert instance property to float\n", e);
    }
  }

  public static <T> List<Integer> convertToListOfInteger(Instance<T> instance) {
    return instance.stream().map(Attribute::hashCode).collect(Collectors.toList());
  }
}
