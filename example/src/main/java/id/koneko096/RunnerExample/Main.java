package id.koneko096.RunnerExample;

import java.util.*;

import id.koneko096.classy.classifier.*;
import id.koneko096.classy.data.*;
import id.koneko096.classy.loader.*;
import id.koneko096.classy.loader.IO.FileInputReaderFactory;
import id.koneko096.classy.runner.*;

import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
        PrintStream out = System.out;
        BaseLoader csvLoader = new CsvLoader();

        BaseClassifier knn = new KNearestNeighbor(4);
        ClassificationRunner knnRunner = new ClassificationRunner(knn);
        csvLoader.loadInput(FileInputReaderFactory.make("example/data/glass/glass.csv"));

        InstanceSet glassDataset = InstanceSetFactory.make(csvLoader, "glass dataset");
        out.println(String.format("KNN: %f", knnRunner.crossValidate(glassDataset, 10)));

        try {
            glassDataset.dropFields(Arrays.asList("Al", "K", "Abc"));
        } catch (UndefinedFieldException e) {
            // handle error
        }

        try {
            ((KNearestNeighbor) knn).setDistanceCalculator(new HammingDistanceCalculator());
            out.println(String.format("KNN: %f", knnRunner.crossValidate(glassDataset, 10)));
        } catch (ModelEmptyException e) {
            // handle error
        }

        BaseLoader arffLoader = new ArffLoader();

        BaseClassifier nb = new NaiveBayes();
        ClassificationRunner nbRunner = new ClassificationRunner(nb);
        arffLoader.loadInput(FileInputReaderFactory.make("example/data/car/car.arff"));

        InstanceSet carDataset = InstanceSetFactory.make(arffLoader, "car dataset");
        try {
            out.println(String.format("NB: %f", nbRunner.crossValidate(carDataset, 10)));
        } catch (ModelEmptyException e) {
            // handle error
        }
    }
}