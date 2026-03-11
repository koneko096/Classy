package id.koneko096.classy.data;

import id.koneko096.classy.util.Constants;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InstanceParser {

  private InstanceParser() {}

  public static List<Instance> parse(List<String> lines, Header header) {
    List<Class> attrTypes = header.getAttributeTypes();
    List<String> attrNames = header.getAttributeNames();

    return lines.stream()
        .map(
            line -> {
              String[] attrs = line.split(Constants.COMMA);

              String label = attrs[attrs.length - 1];
              List<String> attrList = Arrays.asList(attrs).subList(0, attrs.length - 1);

              double[] values =
                  IntStream.range(0, attrList.size())
                      .mapToDouble(
                          i -> {
                            String name = attrNames.get(i);
                            List<String> candidates = header.getAttributeCandidates().get(name);
                            return AttributeFactory.make(attrTypes.get(i), attrList.get(i), name)
                                .obtainNumericValues(candidates);
                          })
                      .toArray();

              return new Instance(values, label);
            })
        .collect(Collectors.toList());
  }
}
