package id.koneko096.classy.data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Header {
  private List<String> attributeNames;
  private Set<String> attributeNameSet;
  private Map<String, List<String>> attributeCandidates;
  private List<Class> attributeTypes;

  public Header(Header header) {
    this.attributeNames = new ArrayList<>(header.attributeNames);
    this.attributeCandidates = new HashMap<>(header.attributeCandidates);
    this.attributeTypes = new ArrayList<>(header.attributeTypes);
    this.attributeNameSet = new HashSet<>(attributeNames);
  }

  public void dropFields(List<String> fieldNames) {
    // Remove from set first
    attributeNameSet.removeAll(fieldNames);

    List<Integer> droppedIndexes =
        IntStream.range(0, attributeNames.size())
            .filter(i -> !attributeNameSet.contains(attributeNames.get(i)))
            .boxed()
            .collect(Collectors.toList());
    List<Integer> notDroppedIndexes =
        IntStream.range(0, attributeNames.size())
            .filter(i -> attributeNameSet.contains(attributeNames.get(i)))
            .boxed()
            .collect(Collectors.toList());

    // Remove from map
    droppedIndexes.forEach(i -> attributeCandidates.remove(attributeNames.get(i)));

    // Remove from name list
    attributeNames =
        notDroppedIndexes.stream().map(attributeNames::get).collect(Collectors.toList());

    // Remove from type list
    attributeTypes =
        notDroppedIndexes.stream().map(attributeTypes::get).collect(Collectors.toList());
  }
}
