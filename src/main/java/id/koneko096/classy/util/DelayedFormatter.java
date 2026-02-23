package id.koneko096.classy.util;

/**
 * @author Afrizal Fikri
 */
public class DelayedFormatter {
  private String format;
  private Object[] args;

  private DelayedFormatter(String format, Object... args) {
    this.format = format;
    this.args = args;
  }

  public static DelayedFormatter format(String format, Object... args) {
    return new DelayedFormatter(format, args);
  }

  @Override
  public String toString() {
    return String.format(format, args);
  }
}
