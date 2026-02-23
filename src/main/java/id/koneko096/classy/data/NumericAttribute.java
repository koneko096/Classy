package id.koneko096.classy.data;

public class NumericAttribute extends Attribute<Double> {
  public NumericAttribute(double value, String name) {
    super(value, name, AttributeType.NUMERIC);
  }

  public NumericAttribute(NumericAttribute numericAttribute) {
    super(numericAttribute);
  }
}
