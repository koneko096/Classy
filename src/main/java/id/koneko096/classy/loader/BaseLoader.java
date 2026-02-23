package id.koneko096.classy.loader;

import id.koneko096.classy.data.Header;
import id.koneko096.classy.data.Instance;
import id.koneko096.classy.loader.IO.InputReader;
import id.koneko096.classy.util.Constants;
import id.koneko096.classy.util.Loggable;
import java.util.List;
import org.slf4j.Logger;

public interface BaseLoader extends Loggable {

  /** Get input supplier TODO: return chainable object */
  void loadInput(InputReader input);

  default void writeLog(Logger log, String readerName) {
    writeLog(log, Constants.LOG_LOAD_INPUT_DATASET_USING, readerName);
  }

  Header parseHeader();

  List<Instance> parseInstances(Header header);
}
