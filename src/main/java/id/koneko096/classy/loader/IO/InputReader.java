package id.koneko096.classy.loader.IO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import lombok.Getter;
import lombok.SneakyThrows;

public class InputReader {
  private BufferedReader reader;
  private StringTokenizer tokenizer;
  @Getter private String name;

  public InputReader(InputStream stream, String name) {
    this.reader = new BufferedReader(new InputStreamReader(stream), 32768);
    this.tokenizer = null;
    this.name = name;
  }

  @SneakyThrows
  public String next() {
    return reader.readLine();
  }

  @SneakyThrows
  public String nextToken() {
    while (tokenizer == null || !tokenizer.hasMoreTokens()) {
      String line = reader.readLine();
      if (line == null) return null;

      tokenizer = new StringTokenizer(line);
    }
    return tokenizer.nextToken();
  }
}
