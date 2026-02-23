package id.koneko096.classy.classifier;

import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import id.koneko096.classy.util.Constants;
import id.koneko096.classy.util.Loggable;
import org.slf4j.Logger;

/**
 * Interface classifier
 *
 * @author Afrizal Fikri
 */
public interface BaseClassifier extends Loggable {

  /**
   * Do training by given dataset Implementation depend on each class
   *
   * @param trainSet
   */
  void train(InstanceSet trainSet);

  default void writeLog(Logger log, String trainSetName) {
    writeLog(log, Constants.LOG_TRAINING_DATASET, trainSetName);
  }

  /**
   * Do classifying by algorithm used
   *
   * @param instance
   * @return class
   */
  String classify(Instance instance);

  String showInfo();
}
