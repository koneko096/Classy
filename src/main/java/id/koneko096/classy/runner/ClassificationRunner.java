package id.koneko096.classy.runner;

import id.koneko096.classy.classifier.BaseClassifier;
import id.koneko096.classy.data.CrossSplit;
import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import id.koneko096.classy.transformer.BaseTransformer;
import id.koneko096.classy.util.DelayedFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class ClassificationRunner
 *
 * @author Afrizal Fikri
 */
@Slf4j
@Builder
@AllArgsConstructor
@Getter
public class ClassificationRunner {
  private BaseClassifier classifier;
  private List<BaseTransformer> transformers;

  public void train(InstanceSet trainSet) {
    InstanceSet newTrainSet = trainSet;
    for (BaseTransformer transformer : transformers) {
      transformer.fit(newTrainSet);
      newTrainSet = transformer.transform(newTrainSet);
    }
    this.classifier.train(trainSet);
  }

  public List<String> classify(InstanceSet testSet) {
    InstanceSet newTestSet = testSet;
    for (BaseTransformer transformer : transformers) {
      newTestSet = transformer.transform(newTestSet);
    }
    return newTestSet.parallelStream().map(this.classifier::classify).collect(Collectors.toList());
  }

  /**
   * Do cross validation Fold must be larger than data train size
   *
   * @param trainSet
   * @param fold
   * @return accuracy
   */
  public double crossValidate(InstanceSet trainSet, int fold) {
    log.info("Performing cross validate with {} fold", fold);

    CrossSplit splitted = trainSet.split(fold);
    List<InstanceSet> trainSets = splitted.getTrainSets();
    List<InstanceSet> testSets = splitted.getTestSets();

    List<List<String>> actualClasses =
        IntStream.range(0, fold)
            .boxed()
            .map(
                i -> {
                  this.classifier.train(trainSets.get(i));
                  return classify(testSets.get(i));
                })
            .collect(Collectors.toList());

    List<List<String>> expectedClasses =
        testSets.stream()
            .map(
                instances ->
                    instances.stream().map(Instance::getLabel).collect(Collectors.toList()))
            .collect(Collectors.toList());

    double accuracyRate =
        IntStream.range(0, splitted.size())
            .boxed()
            .collect(
                Collectors.averagingDouble(
                    i -> validate(expectedClasses.get(i), actualClasses.get(i))));

    log.debug(
        "Cross validation accuracy rate: {} %",
        DelayedFormatter.format("%.02f", accuracyRate * 100));
    return accuracyRate;
  }

  private double validate(List<String> expecteds, List<String> actuals) {
    return IntStream.range(0, expecteds.size())
        .boxed()
        .collect(
            Collectors.averagingDouble(i -> expecteds.get(i).equals(actuals.get(i)) ? 1.0 : 0.0));
  }
}
