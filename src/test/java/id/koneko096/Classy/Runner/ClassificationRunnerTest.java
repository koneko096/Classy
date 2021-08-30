package id.koneko096.Classy.Runner;

import id.koneko096.Classy.Classifier.BaseClassifier;
import id.koneko096.Classy.Classifier.ModelEmptyException;
import id.koneko096.Classy.Data.Instance;
import id.koneko096.Classy.Data.InstanceSet;
import id.koneko096.Classy.Data.InstanceSetFactory;
import id.koneko096.Classy.Loader.ArffLoader;
import id.koneko096.Classy.Loader.IO.FileInputReaderFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ClassificationRunnerTest {

  @Mock
  private BaseClassifier classifier;

  private ClassificationRunner runner;

  @BeforeEach
  void setUp() {
    Mockito.doReturn("dummy").when(classifier).showInfo();
    runner = new ClassificationRunner(classifier);
  }

  @Test
  void classify() {
    Instance instance = new Instance(Collections.emptyList());
    runner.classify(instance);
    Mockito.verify(classifier).classify(eq(instance));
  }

  @Test
  void crossValidate() {
    ArffLoader arffLoader = new ArffLoader();
    arffLoader.loadInput(FileInputReaderFactory.make("example/data/car/car.arff"));

    InstanceSet carDataset = InstanceSetFactory.make(arffLoader, "car dataset");
    try {
      runner.crossValidate(carDataset, 10);
    } catch (ModelEmptyException e) {
      fail();
    }
    Mockito.verify(classifier, Mockito.times(1707)).classify(any(Instance.class));
  }
}