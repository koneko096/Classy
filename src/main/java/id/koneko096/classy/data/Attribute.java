package id.koneko096.classy.data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class attribute
 *
 * @author Afrizal Fikri
 */
@Data
@AllArgsConstructor
public class Attribute<T> {
  private T value;
  private String name;
  private AttributeType type;

  public Attribute(Attribute<T> attr) {
    value = attr.value;
    type = attr.type;
    name = attr.name;
  }

  public static <T> List<Attribute<T>> ofMap(Map<String, T> map, AttributeType type) {
    return map.entrySet().parallelStream()
        .map(e -> new Attribute<>(e.getValue(), e.getKey(), type))
        .collect(Collectors.toList());
  }

  public double toDouble() {
    String s = value.toString();
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
