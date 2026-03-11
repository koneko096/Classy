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
  private Object value;
  private String name;
  private AttributeType type;

  public static List<Attribute> ofMap(Map<String, Object> map, AttributeType type) {
    return map.entrySet().parallelStream()
        .map(e -> new Attribute(e.getValue(), e.getKey(), type))
        .collect(Collectors.toList());
  }

  public double obtainNumericValues() {
    return obtainNumericValues(null);
  }

  public double obtainNumericValues(List<String> candidates) {
    if (type == AttributeType.NOMINAL && candidates != null) {
      int index = candidates.indexOf(value.toString());
      if (index != -1) {
        return (double) index;
      }
    }

    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    } else {
      try {
        return Double.parseDouble(value.toString());
      } catch (NumberFormatException e) {
        return 0.0;
      }
    }
  }
}
