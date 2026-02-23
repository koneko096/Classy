package id.koneko096.classy.loader.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileInputReaderFactory {

  private FileInputReaderFactory() {}

  public static InputReader make(String fileName) {
    InputReader inputReaderTemp;
    File initialFile = new File(fileName);

    try {
      InputStream is = new FileInputStream(initialFile);
      inputReaderTemp = new InputReader(is, "File input path: " + fileName);
    } catch (FileNotFoundException e) {
      inputReaderTemp = null;
      log.error("File={} not found", fileName, e);
    }

    return inputReaderTemp;
  }
}
