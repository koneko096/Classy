package id.koneko096.classy.data;

public class AttributeFactory {

  private AttributeFactory() {}

  public static Attribute make(Class type, String value, String name) {
    switch (type.getName()) {
      case "java.lang.Double":
        return new NumericAttribute(Double.parseDouble(value), name);
      case "java.lang.String":
        return new StringAttribute(value, name);
      default:
        return new StringAttribute(value, name);
    }
  }
}
