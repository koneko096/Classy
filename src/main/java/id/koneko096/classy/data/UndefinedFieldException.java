package id.koneko096.classy.data;

public class UndefinedFieldException extends Exception {
  UndefinedFieldException(String message) {
    super(message);
  }

  UndefinedFieldException(String message, Throwable cause) {
    super(message, cause);
  }
}
