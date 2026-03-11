# Classy ![Build](https://github.com/koneko096/Classy/actions/workflows/build.yml/badge.svg) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=koneko096_Classy&metric=alert_status)](https://sonarcloud.io/dashboard?id=koneko096_Classy)
 
Simple Java library for kNN and Naive Bayes classification with built-in support for data preprocessing and dimensionality reduction.

## Features

- **Classifiers**: k-Nearest Neighbors (kNN) and Naive Bayes.
- **Preprocessing (Transformers)**: 
    - `StdScalerTransformer`: Standardizes features by removing the mean and scaling to unit variance.
    - `PCATransformer`: Performs Principal Component Analysis (PCA) for dimensionality reduction.
- **Data Loaders**: Built-in support for CSV and ARFF file formats.
- **Validation**: 10-fold cross-validation support via `ClassificationRunner`.
- **Data Manipulation**: Hard-delete functionality for dropping irrelevant attributes from datasets.

## Usage Example

### Basic Classification (kNN)

```java

public class Test {
    public static void main(String[] args) {
        PrintStream out = System.out;
        BaseLoader csvLoader = new CsvLoader();

        BaseClassifier knn = new KNearestNeighbor(4);
        csvLoader.loadInput(FileInputReaderFactory.make("data/glass/glass.csv"));

        // Build the dataset
        InstanceSet glassDataset = InstanceSetFactory.make(csvLoader, "glass dataset");

        // Build the runner using the Builder pattern
        ClassificationRunner runner = ClassificationRunner.builder()
                .classifier(new KNearestNeighbor(4))
                .build();

        // Perform 10-fold cross validation
        double accuracy = runner.crossValidate(dataset, 10);
        System.out.println("KNN Accuracy: " + accuracy);
    }
}
```

### Advanced Pipeline (Scaling + PCA)

Standardize your data before applying PCA to ensure best results.

```java
ClassificationRunner advancedRunner = ClassificationRunner.builder()
        .classifier(new KNearestNeighbor(4))
        .transformers(Arrays.asList(
            new StdScalerTransformer(), // Step 1: Scale features (mean=0, variance=1)
            new PCATransformer(3)       // Step 2: Reduce to 3 Principal Components
        ))
        .build();

double accuracy = advancedRunner.crossValidate(dataset, 10);
```

### Dropping Attributes

You can permanently remove fields from a dataset before classification.

```java
try {
    dataset.dropFields(Arrays.asList("Al", "K"));
} catch (UndefinedFieldException e) {
    // handle error
}
```

## Installation

Add the library to your `build.gradle`:

```gradle
dependencies {
    implementation 'id.koneko096:classy-core:1.1.1'
}
```

Check the `example` directory for a more comprehensive demonstration.
