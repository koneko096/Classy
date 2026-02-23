package id.koneko096.classy.classifier;

import id.koneko096.classy.data.Instance;
import id.koneko096.classy.util.DataPreparationUtil;
import java.util.List;
import java.util.stream.IntStream;

public class HammingDistanceCalculator implements DistanceCalculator {

  private static final String HAMMING = "Hamming";

  @Override
  public double calculate(Instance a, Instance b) {
    List<Integer> la = DataPreparationUtil.convertToListOfInteger(a);
    List<Integer> lb = DataPreparationUtil.convertToListOfInteger(b);

    return IntStream.range(0, la.size())
        .boxed()
        .map(i -> !la.get(i).equals(lb.get(i)))
        .mapToInt(bl -> (bl ? 1 : 0))
        .sum();
  }

  @Override
  public String getName() {
    return HAMMING;
  }
}
