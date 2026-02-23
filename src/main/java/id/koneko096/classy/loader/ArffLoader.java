package id.koneko096.classy.loader;

import static id.koneko096.classy.util.Constants.*;

import id.koneko096.classy.data.AttributeType;
import id.koneko096.classy.data.Header;
import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceParser;
import id.koneko096.classy.loader.IO.InputReader;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArffLoader implements BaseLoader {
  private InputReader input;

  @Override
  public void loadInput(InputReader input) {
    writeLog(log, input.getName());
    this.input = input;
  }

  @Override
  public Header parseHeader() {

    List<String> attrNames = new ArrayList<>();
    Map<String, List<String>> attributes = new HashMap<>();

    String line = input.next();
    while (line != null && !line.startsWith(ATTRIBUTE_SEGMENT)) {
      line = input.next();
    }

    while (line != null && line.startsWith(ATTRIBUTE_SEGMENT)) {
      String attribute =
          line.substring(line.indexOf(ATTRIBUTE_SEGMENT) + ATTRIBUTE_SEGMENT.length() + 1);

      int firstSpacePos = attribute.indexOf(' ');
      String attrName = attribute.substring(0, firstSpacePos);

      String attrCandidatesStr = attribute.substring(firstSpacePos + 1);
      List<String> attrCandidates = parseAttrCandidates(attrCandidatesStr);

      if (!attrName.equals("class")) {
        attributes.put(attrName, attrCandidates);
        attrNames.add(attrName);
      }

      line = input.next();
    }

    List<String> attrTypesStr =
        new ArrayList<>(
            Collections.nCopies(attributes.size(), "NOMINAL")); //  TODO: READ FROM FILE/CONFIG
    return Header.builder()
        .attributeNames(attrNames)
        .attributeNameSet(new HashSet<>(attrNames))
        .attributeCandidates(attributes)
        .attributeTypes(
            attrTypesStr.stream()
                .map(AttributeType::valueOf)
                .map(AttributeType::getType)
                .collect(Collectors.toList()))
        .build();
  }

  private List<String> parseAttrCandidates(String attrCandidatesStr) {
    String trimmed = attrCandidatesStr.trim();
    String cleaned = trimmed.substring(1, trimmed.length() - 1);
    String[] splitted = cleaned.split(DELIMITERS);
    return Arrays.stream(splitted)
        .filter(s -> s != null && !s.isEmpty())
        .collect(Collectors.toList());
  }

  @Override
  public List<Instance> parseInstances(Header header) {
    String line = input.next();
    while (line != null && !line.startsWith(DATA_SEGMENT)) {
      line = input.next();
    }
    line = input.next();

    if (line == null) {
      return Collections.emptyList();
    }

    List<String> lines = new ArrayList<>();
    do {
      lines.add(line);
      line = input.next();
    } while (line != null);

    return InstanceParser.parse(lines, header);
  }
}
