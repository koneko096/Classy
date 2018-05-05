package id.koneko096.RunnerExample;

import id.koneko096.Classy.Classifier.*;
import id.koneko096.Classy.Data.*;
import id.koneko096.Classy.Loader.*;
import id.koneko096.Classy.Runner.*;

import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
        PrintStream out = System.out;
        BaseLoader csvLoader = new CsvLoader();

        BaseClassifier knn = new KNearestNeighbor(4);
        ClassificationRunner knnRunner = new ClassificationRunner(knn);
        csvLoader.loadInput(FileInputReaderFactory.make("data/glass/glass.csv"));

        InstanceSet glassDataset = InstanceSetFactory.make(csvLoader, "glass dataset");
        out.println(String.format("KNN: %f", knnRunner.crossValidate(glassDataset, 10)));

        BaseLoader arffLoader = new ArffLoader();

        BaseClassifier nb = new NaiveBayes();
        ClassificationRunner nbRunner = new ClassificationRunner(nb);
        arffLoader.loadInput(FileInputReaderFactory.make("data/car/car.arff"));

        InstanceSet carDataset = InstanceSetFactory.make(arffLoader, "car dataset");
        out.println(String.format("NB: %f", nbRunner.crossValidate(carDataset, 10)));
    }
}