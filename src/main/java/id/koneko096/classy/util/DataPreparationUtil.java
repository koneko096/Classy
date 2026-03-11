package id.koneko096.classy.util;

import id.koneko096.classy.data.Instance;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataPreparationUtil {

  private DataPreparationUtil() {}

  public static List<Double> convertToListOfDouble(Instance instance) {
    return Arrays.stream(instance.values()).boxed().collect(Collectors.toList());
  }

  public static List<Integer> convertToListOfInteger(Instance instance) {
    return Arrays.stream(instance.values())
        .mapToInt(d -> (int) d)
        .boxed()
        .collect(Collectors.toList());
  }
}
