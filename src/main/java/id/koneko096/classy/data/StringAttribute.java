package id.koneko096.classy.data;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class StringAttribute extends Attribute {
  public StringAttribute(String value, String name) {
    super(value, name, AttributeType.NOMINAL);
  }

  public StringAttribute(StringAttribute stringAttribute) {
    super(stringAttribute.getValue(), stringAttribute.getName(), stringAttribute.getType());
  }
}
