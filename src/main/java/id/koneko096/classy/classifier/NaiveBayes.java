package id.koneko096.classy.classifier;

import id.koneko096.classy.data.Attribute;
import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

/**
 * Class NaiveBayes
 *
 * @author Afrizal Fikri
 */
@Slf4j
public class NaiveBayes implements BaseClassifier {

  private static final String INFO_CLASSIFIER_NAIVE_BAYES = "Classifier: Naive Bayes\n";

  private List<Map<String, Integer>> attrValIdx;

  private Map<String, Integer> classIdx;
  private List<String> classVal;

  private int[][][] table;
  private int[] classCum;
  private int total = 0;

  private double[] classProbs;
  private double[][][] attrLikelihood;

  @Override
  public void train(InstanceSet trainSet) {
    writeLog(log, trainSet.getName());
    prepareTable(trainSet);
    fillTable(trainSet);
    fillLikelihoodTable();
  }

  private void fillLikelihoodTable() {
    IntStream.range(0, this.classProbs.length)
        .forEach(i -> this.classProbs[i] = (double) this.classCum[i] / this.total);
    IntStream.range(0, this.attrLikelihood.length)
        .forEach(
            i ->
                IntStream.range(0, this.attrLikelihood[i].length)
                    .forEach(
                        j ->
                            IntStream.range(0, this.attrLikelihood[j].length)
                                .forEach(
                                    k -> {
                                      if (this.classProbs[k] == 0)
                                        this.attrLikelihood[i][j][k] = 0.0;

                                      this.attrLikelihood[i][j][k] =
                                          this.table[i][j][k] / this.classProbs[k] / this.total;
                                    })));
  }

  private void fillTable(InstanceSet trainSet) {
    Map<Integer, List<Instance>> grouped =
        trainSet.stream()
            .collect(
                Collectors.groupingBy(
                    instance -> {
                      String label = instance.getLabel();
                      return this.classIdx.get(label);
                    }));

    // TODO: improve performance
    List<String> attrNames = trainSet.getAttributeNames();
    grouped.entrySet().stream()
        .forEach(
            entry -> {
              int classId = entry.getKey();
              List<Instance> instances = entry.getValue();

              instances.stream()
                  .forEach(
                      instance ->
                          IntStream.range(0, attrNames.size())
                              .forEach(
                                  id -> {
                                    String attrName = attrNames.get(id);
                                    Attribute attr = instance.get(attrName);
                                    int attrIdx =
                                        this.attrValIdx.get(id).get(attr.getValue().toString());
                                    this.table[id][attrIdx][classId]++;
                                  }));

              this.classCum[classId] += instances.size();
            });

    this.total = trainSet.size();
  }

  private void prepareTable(InstanceSet trainSet) {
    List<List<String>> attrVal =
        trainSet.getAttributeNames().stream()
            .map(trainSet.getAttributeCandidates()::get)
            .collect(Collectors.toList());
    this.attrValIdx =
        attrVal.stream()
            .map(
                av ->
                    IntStream.range(0, av.size())
                        .boxed()
                        .collect(Collectors.toMap(av::get, Function.identity())))
            .collect(Collectors.toList());

    this.classVal =
        trainSet.stream().map(Instance::getLabel).sorted().distinct().collect(Collectors.toList());
    this.classIdx =
        IntStream.range(0, this.classVal.size())
            .boxed()
            .collect(Collectors.toMap(this.classVal::get, Function.identity()));

    int maxSize = attrVal.stream().map(List::size).max(Integer::compareTo).orElse(0);

    this.table = new int[attrVal.size()][maxSize][this.classVal.size()];
    this.classCum = new int[this.classVal.size()];

    this.classProbs = new double[this.classVal.size()];
    this.attrLikelihood = new double[attrVal.size()][maxSize][this.classVal.size()];
  }

  @Override
  public String classify(Instance instance) {
    List<Map.Entry<Double, Integer>> x =
        IntStream.range(0, this.classProbs.length)
            .boxed()
            .map(
                k -> {
                  double classLikelihood =
                      IntStream.range(0, this.attrValIdx.size())
                          .boxed()
                          .map(
                              i -> {
                                String attrName = (String) instance.getAttributeNames().get(i);
                                String attrVal = instance.get(attrName).getValue().toString();
                                int j = this.attrValIdx.get(i).get(attrVal);
                                return this.attrLikelihood[i][j][k];
                              })
                          .reduce(1.0, (a, b) -> a * b);
                  return new AbstractMap.SimpleEntry<>(-classLikelihood, k);
                })
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .collect(Collectors.toList());

    return x.isEmpty() ? null : this.classVal.get(x.get(0).getValue());
  }

  @Override
  public String showInfo() {
    return INFO_CLASSIFIER_NAIVE_BAYES;
  }
}
