package id.koneko096.RunnerExample;

import java.util.*;

import id.koneko096.classy.classifier.*;
import id.koneko096.classy.data.*;
import id.koneko096.classy.loader.*;
import id.koneko096.classy.loader.IO.FileInputReaderFactory;
import id.koneko096.classy.runner.*;
import id.koneko096.classy.transformer.PCATransformer;
import id.koneko096.classy.transformer.StdScalerTransformer;

import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
        PrintStream out = System.out;
        BaseLoader csvLoader = new CsvLoader();

        // 1. Basic KNN Classification
        BaseClassifier knn = new KNearestNeighbor(4);
        ClassificationRunner knnRunner = ClassificationRunner.builder().classifier(knn).build();
        csvLoader.loadInput(FileInputReaderFactory.make("example/data/glass/glass.csv"));

        InstanceSet glassDataset = InstanceSetFactory.make(csvLoader, "glass dataset");
        out.println(String.format("Initial KNN: %f", knnRunner.crossValidate(glassDataset, 10)));

        // 2. Demonstrate Hard Delete (Permanently dropping fields)
        try {
            out.println("Dropping fields Al, K...");
            glassDataset.dropFields(Arrays.asList("Al", "K"));
            out.println(String.format("KNN after permanent drop: %f", knnRunner.crossValidate(glassDataset, 10)));
        } catch (UndefinedFieldException e) {
            out.println("Unexpected Error: " + e.getMessage());
        }

        // 3. Demonstrate PCA Transformer with ClassificationRunner Builder
        out.println("--- PCA Example (Glass Dataset) ---");
        // Reload fresh dataset for clean PCA demo
        csvLoader.loadInput(FileInputReaderFactory.make("example/data/glass/glass.csv"));
        InstanceSet freshGlass = InstanceSetFactory.make(csvLoader, "fresh glass");

        ClassificationRunner pcaKnnRunner = ClassificationRunner.builder()
                .classifier(new KNearestNeighbor(4))
                .transformers(Arrays.asList(new PCATransformer(3))) // Reduce 9 features to 3 components
                .build();

        try {
            out.println(String.format("KNN with PCA (3 components): %f", pcaKnnRunner.crossValidate(freshGlass, 10)));
        } catch (Exception e) {
            out.println("PCA Error: " + e.getMessage());
        }

        // 4. Demonstrate Scaling + PCA (Improving PCA accuracy)
        out.println("--- PCA with Scaling Example (Glass Dataset) ---");
        csvLoader.loadInput(FileInputReaderFactory.make("example/data/glass/glass.csv"));
        InstanceSet glassForScaledPCA = InstanceSetFactory.make(csvLoader, "glass scaled pca");

        ClassificationRunner scaledPcaRunner = ClassificationRunner.builder()
                .classifier(new KNearestNeighbor(4))
                .transformers(Arrays.asList(
                        new StdScalerTransformer(),
                        new PCATransformer(3)))
                .build();

        try {
            out.println(String.format("KNN with Scaling + PCA (3 components): %f",
                    scaledPcaRunner.crossValidate(glassForScaledPCA, 10)));
        } catch (Exception e) {
            out.println("Scaled PCA Error: " + e.getMessage());
        }

        // 5. Example with Naive Bayes on categorical data
        out.println("--- Naive Bayes (Car Dataset) ---");
        BaseLoader arffLoader = new ArffLoader();
        arffLoader.loadInput(FileInputReaderFactory.make("example/data/car/car.arff"));
        InstanceSet carDataset = InstanceSetFactory.make(arffLoader, "car dataset");

        ClassificationRunner nbRunner = ClassificationRunner.builder().classifier(new NaiveBayes()).build();
        try {
            out.println(String.format("Naive Bayes Accuracy: %f", nbRunner.crossValidate(carDataset, 10)));
        } catch (ModelEmptyException e) {
            out.println("NB Error: " + e.getMessage());
        }
    }
}