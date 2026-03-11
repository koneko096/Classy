package id.koneko096.classy.runner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import id.koneko096.classy.classifier.BaseClassifier;
import id.koneko096.classy.classifier.ModelEmptyException;
import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import id.koneko096.classy.data.InstanceSetFactory;
import id.koneko096.classy.loader.ArffLoader;
import id.koneko096.classy.loader.IO.FileInputReaderFactory;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ClassificationRunnerTest {

  @Mock private BaseClassifier classifier;

  private ClassificationRunner runner;

  @BeforeEach
  void setUp() {
    runner = ClassificationRunner.builder().classifier(classifier).build();
  }

  @Test
  void classify() {
    InstanceSet dataset = InstanceSet.builder().instanceList(Collections.emptyList()).build();
    runner.classify(dataset);
    Mockito.verify(classifier, Mockito.never()).classify(any(Instance.class));
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
