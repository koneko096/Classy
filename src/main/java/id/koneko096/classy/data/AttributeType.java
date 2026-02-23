package id.koneko096.classy.data;

/**
 * Enum attribute-type
 *
 * @author Afrizal Fikri
 */
public enum AttributeType {
  STRING(String.class),
  NOMINAL(String.class),
  NUMERIC(Double.class);

  private final Class<?> type;

  AttributeType(Class<?> type) {
    this.type = type;
  }

  public Class<?> getType() {
    return this.type;
  }
}
