package id.koneko096.classy.data;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;

/**
 * Class instance
 *
 * @author Afrizal Fikri
 */
@Data
public class Instance<T> implements Collection<Attribute<T>> {
  private List<String> attributeNames;
  private Map<String, Attribute<T>> attributeMap;

  private List<Attribute<T>> attributeList;
  private String label;

  private Set<String> disabledNames;

  /**
   * Constructor
   *
   * @param attributes
   */
  public Instance(List<Attribute<T>> attributes) {
    this.attributeList = new ArrayList<>(attributes);
    this.attributeNames = attributes.stream().map(Attribute::getName).collect(Collectors.toList());
    this.attributeMap =
        attributes.stream().collect(Collectors.toMap(Attribute::getName, Function.identity()));
    this.disabledNames = new HashSet<>();
    this.label = null;
  }

  /**
   * Constructor
   *
   * @param attributes
   * @param label
   */
  public Instance(List<Attribute<T>> attributes, String label) {
    this.attributeList = new ArrayList<>(attributes);
    this.attributeNames = attributes.stream().map(Attribute::getName).collect(Collectors.toList());
    this.attributeMap =
        attributes.stream().collect(Collectors.toMap(Attribute::getName, Function.identity()));
    this.disabledNames = new HashSet<>();
    this.label = label;
  }

  /**
   * Getter attribute value by attr name
   *
   * @param attributeName
   * @return attr values
   */
  public Attribute get(String attributeName) {
    return attributeMap.get(attributeName);
  }

  public double[] values() {
    return this.attributeList.stream()
        .filter(a -> !this.disabledNames.contains(a.getName()))
        .mapToDouble(Attribute::toDouble)
        .toArray();
  }

  @Override
  public int size() {
    return attributeList.size();
  }

  @Override
  public boolean isEmpty() {
    return attributeList.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return attributeList.contains(o);
  }

  @Override
  public Iterator<Attribute<T>> iterator() {
    return attributeList.iterator();
  }

  @Override
  public Object[] toArray() {
    return null;
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return null;
  }

  @Override
  public boolean add(Attribute attribute) {
    attributeMap.put(attribute.getName(), attribute);
    return attributeList.add(attribute);
  }

  @Override
  public boolean remove(Object o) {
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return attributeList.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends Attribute<T>> c) {
    List<String> additionalNames =
        c.stream()
            .map(
                a -> {
                  attributeMap.put(a.getName(), a);
                  return a.getName();
                })
            .collect(Collectors.toList());
    if (!attributeNames.addAll(additionalNames)) return false;
    return attributeList.addAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return false;
  }

  @Override
  public void clear() {
    attributeMap.clear();
    attributeNames.clear();
    attributeList.clear();
    disabledNames.clear();
  }

  @Override
  public Stream<Attribute<T>> stream() {
    return attributeList.stream();
  }

  /*
   * Implemented using soft delete
   * Rationale: most of attributes removal is less than half of total attributes
   */
  void dropAttributes(List<String> attributeNames) {
    List<String> filteredNames =
        attributeNames.stream().filter(s -> s != null && !s.isEmpty()).collect(Collectors.toList());
    disabledNames.addAll(filteredNames);
  }
}
