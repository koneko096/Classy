package id.koneko096.classy.data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Class instance-set
 *
 * @author Afrizal Fikri
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class InstanceSet implements List<Instance> {
  private List<Instance> instanceList;
  @Getter private Header header;
  @Getter private String name;

  /**
   * Copy cnstructor
   *
   * @param is instance set
   */
  public InstanceSet(InstanceSet is) {
    this.instanceList = new ArrayList<>(is.instanceList);
    this.header = new Header(is.header);
    this.name = is.name;
  }

  /**
   * Split instances into number of folds
   *
   * @return split wrapper
   */
  public CrossSplit split(int fold) {
    List<Instance> shuffled = new ArrayList<>(this.instanceList);
    Collections.shuffle(shuffled);

    Map<Integer, List<Instance>> groupedId =
        IntStream.range(0, shuffled.size())
            .boxed()
            .collect(
                Collectors.groupingBy(
                    i -> i % fold, Collectors.mapping(shuffled::get, Collectors.toList())));

    List<InstanceSet> testSets =
        IntStream.range(0, fold)
            .boxed()
            .map(groupedId::get)
            .map(
                i ->
                    InstanceSet.builder()
                        .header(this.header)
                        .instanceList(groupedId.get(i))
                        .name(this.name + " - fold " + i)
                        .build())
            .collect(Collectors.toList());
    List<InstanceSet> trainSets =
        IntStream.range(0, fold)
            .boxed()
            .map(
                i -> {
                  List<Instance> l =
                      IntStream.range(0, fold)
                          .boxed()
                          .filter(j -> !j.equals(i))
                          .map(testSets::get)
                          .flatMap(InstanceSet::stream)
                          .collect(Collectors.toList());
                  return InstanceSet.builder()
                      .header(this.header)
                      .instanceList(l)
                      .name(this.name + " - fold " + i)
                      .build();
                })
            .collect(Collectors.toList());

    return new CrossSplit(trainSets, testSets);
  }

  /** Drop fields inside aan instance set header and instances */
  public void dropFields(List<String> fieldNames) throws UndefinedFieldException {
    Optional<String> unpresentFields =
        fieldNames.stream().filter(f -> !this.header.getAttributeNameSet().contains(f)).findFirst();

    if (unpresentFields.isPresent()) {
      throw new UndefinedFieldException(
          String.format("Field %s is undefined", unpresentFields.get()));
    }

    this.header.dropFields(fieldNames);
    this.instanceList.forEach(i -> i.dropAttributes(fieldNames));
  }

  public List<String> getAttributeNames() {
    return header.getAttributeNames();
  }

  public Map<String, List<String>> getAttributeCandidates() {
    return header.getAttributeCandidates();
  }

  public List<Class> getAttributeTypes() {
    return header.getAttributeTypes();
  }

  @Override
  public int size() {
    return instanceList.size();
  }

  @Override
  public boolean isEmpty() {
    return instanceList.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return instanceList.contains(o);
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public boolean add(Instance o) {
    return instanceList.add(o);
  }

  @Override
  public boolean remove(Object o) {
    return instanceList.remove(o);
  }

  @Override
  public boolean addAll(Collection<? extends Instance> collection) {
    return instanceList.addAll(collection);
  }

  @Override
  public boolean addAll(int index, Collection<? extends Instance> collection) {
    return instanceList.addAll(index, collection);
  }

  @Override
  public void clear() {
    instanceList.clear();
  }

  @Override
  public Instance get(int index) {
    return instanceList.get(index);
  }

  @Override
  public Instance set(int index, Instance element) {
    return instanceList.set(index, element);
  }

  @Override
  public void add(int index, Instance element) {
    instanceList.add(index, element);
  }

  @Override
  public Instance remove(int index) {
    return instanceList.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return instanceList.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return instanceList.lastIndexOf(o);
  }

  @Override
  public ListIterator<Instance> listIterator() {
    return instanceList.listIterator();
  }

  @Override
  public ListIterator<Instance> listIterator(int index) {
    return instanceList.listIterator(index);
  }

  @Override
  public List<Instance> subList(int fromIndex, int toIndex) {
    return instanceList.subList(fromIndex, toIndex);
  }

  @Override
  public boolean retainAll(Collection collection) {
    return instanceList.retainAll(collection);
  }

  @Override
  public boolean removeAll(Collection collection) {
    return instanceList.removeAll(collection);
  }

  @Override
  public boolean containsAll(Collection collection) {
    return instanceList.containsAll(collection);
  }

  @Override
  public <T> T[] toArray(T[] objects) {
    return instanceList.toArray(objects);
  }

  @Override
  public Iterator<Instance> iterator() {
    return instanceList.iterator();
  }

  @Override
  public Stream<Instance> stream() {
    return instanceList.stream();
  }

  @Override
  public Stream<Instance> parallelStream() {
    return instanceList.parallelStream();
  }
}
