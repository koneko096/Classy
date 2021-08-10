# Classy ![Build](https://github.com/koneko096/Classy/actions/workflows/BuildImage.yml/badge.svg) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=koneko096_Classy&metric=alert_status)](https://sonarcloud.io/dashboard?id=koneko096_Classy)
 
Simple kNN and NaiveBayes classifier implementation

## Usage example

```java

public class Test {
    public static void main(String[] args) {
        PrintStream out = System.out;
        BaseLoader csvLoader = new CsvLoader();

        BaseClassifier knn = new KNearestNeighbor(4);
        ClassificationRunner knnRunner = new ClassificationRunner(knn);
        csvLoader.loadInput(FileInputReaderFactory.make("data/glass/glass.csv"));

        InstanceSet glassDataset = InstanceSetFactory.make(csvLoader, "glass dataset");

        try {
            glassDataset.dropFields(Arrays.asList("Al", "K"));
        } catch (UndefinedFieldException e) {
            // handle error
        }

        out.println(String.format("KNN: %f", knnRunner.crossValidate(glassDataset, 10)));
    }
}

```

Example project see on `example` directory
