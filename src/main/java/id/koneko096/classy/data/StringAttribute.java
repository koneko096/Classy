package id.koneko096.classy.data;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class StringAttribute extends Attribute<String> implements Comparable<StringAttribute> {
  public StringAttribute(String value, String name) {
    super(value, name, AttributeType.NOMINAL);
  }

  public StringAttribute(StringAttribute stringAttribute) {
    super(stringAttribute);
  }

  @Override
  public int compareTo(StringAttribute stringAttribute) {
    return getValue().compareTo(stringAttribute.getValue());
  }
}
