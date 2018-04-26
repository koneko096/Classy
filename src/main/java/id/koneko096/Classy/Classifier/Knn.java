package id.koneko096.Classy.Classifier;

import java.util.*;
import java.lang.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import id.koneko096.Classy.Data.Attribute;
import id.koneko096.Classy.Data.Instance;
import id.koneko096.Classy.Data.InstanceSet;

/**
 * Class Knn
 * k Nearest Neighbourhood classifier
 *
 * @author Afrizal Fikri
 */
public class Knn implements BaseClassifier {

    private InstanceSet trainSet;
    private int k;

    public Knn(int k) {
        this.k = k;
    }

    /**
     * Initialize kNN
     */
    @Override
    public void init() {
    }

    /**
     * Add new instances to saved train set before
     * Or create new if doesnt exist
     *
     * @param trainSet
     */
    @Override
    public void train(InstanceSet trainSet) {
        this.trainSet = trainSet;
    }

    /**
     * Do classifying by given train set
     *
     * @param instance
     * @return string class
     */
    @Override
    public String classify(Instance instance) {
        List<Integer> dist = trainSet.stream()
                .map(i -> HammingDistance(i, instance))
                .collect(Collectors.toList());
        List<String> label = trainSet.stream()
                .map(Instance::getLabel)
                .collect(Collectors.toList());
        List<Integer> sortedIdx = IntStream.range(0, trainSet.size()).boxed()
                .sorted(Comparator.comparingInt(dist::get))
                .collect(Collectors.toList());

        Map<String, Long> counter = sortedIdx.stream()
                .limit(Math.min(sortedIdx.size(), this.k))
                .map(label::get).collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

        return counter.entrySet().stream().max((label1, label2)
                        -> label1.getValue() > label2.getValue() ? 1 : -1)
                .map(Map.Entry::getKey).get();
    }

    /**
     * HammingDistance of two instance
     *
     * @param a
     * @param b
     * @return integer distance
     */
    private int HammingDistance(Instance a, Instance b) {
        List<Integer> la = a.stream().map(Attribute::hashCode).sorted().collect(Collectors.toList());
        List<Integer> lb = b.stream().map(Attribute::hashCode).sorted().collect(Collectors.toList());

        return IntStream.range(0, la.size()).boxed()
                .map(i -> !la.get(i).equals(lb.get(i)))
                .mapToInt(bl -> (bl ? 1 : 0))
                .sum();
    }
} 