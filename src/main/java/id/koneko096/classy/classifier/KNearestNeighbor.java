package id.koneko096.classy.classifier;

import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class KNearestNeighbor k Nearest Neighbourhood classifier
 *
 * @author Afrizal Fikri
 */
@Slf4j
public class KNearestNeighbor implements BaseClassifier {

  private static final String INFO_CLASSIFIER_K_NEAREST_NEIGHBORS =
      "Classifier: K Nearest Neighbors\nNeighbor number: %d \nDistance metric: %s\n";

  private InstanceSet trainSet;
  @Setter private int k;
  @Setter private DistanceCalculator distanceCalculator;

  public KNearestNeighbor(int k) {
    this.k = k;
    this.distanceCalculator = new EuclideanDistanceCalculator();
  }

  @Override
  public void train(InstanceSet trainSet) {
    writeLog(log, trainSet.getName());
    this.trainSet = trainSet;
  }

  public void train(InstanceSet trainSet, DistanceCalculator distanceCalculator) {
    writeLog(log, trainSet.getName());
    this.trainSet = trainSet;
    this.distanceCalculator = distanceCalculator;
  }

  @Override
  public String classify(Instance instance) {
    List<Double> dist;
    try {
      dist =
          trainSet.stream()
              .map(i -> this.distanceCalculator.calculate(i, instance))
              .collect(Collectors.toList());
    } catch (RuntimeException e) {
      String errorMessage = "Error while computing distance\n";
      log.error(errorMessage, e);
      throw new RuntimeException(errorMessage, e);
    }

    List<String> label = trainSet.stream().map(Instance::getLabel).collect(Collectors.toList());
    List<Integer> sortedIdx =
        IntStream.range(0, trainSet.size())
            .boxed()
            .sorted(Comparator.comparingDouble(dist::get))
            .collect(Collectors.toList());

    Map<String, Long> counter =
        sortedIdx.stream()
            .limit(Math.min(sortedIdx.size(), this.k))
            .map(label::get)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    return counter.entrySet().stream()
        .max((label1, label2) -> label1.getValue() > label2.getValue() ? 1 : -1)
        .map(Map.Entry::getKey)
        .orElse(null);
  }

  @Override
  public String showInfo() {
    return String.format(
        INFO_CLASSIFIER_K_NEAREST_NEIGHBORS, this.k, this.distanceCalculator.getName());
  }
}
