package id.koneko096.classy.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class SoftDeleteTest {

  @Test
  void testHardDeleteUpdatesAllMetadata() throws UndefinedFieldException {
    // 1. Setup a sample InstanceSet with 3 attributes
    List<String> names = Arrays.asList("a1", "a2", "a3");
    Header header =
        Header.builder()
            .attributeNames(new ArrayList<>(names))
            .attributeNameSet(new HashSet<>(names))
            .attributeCandidates(new HashMap<>())
            .attributeTypes(names.stream().map(n -> Double.class).collect(Collectors.toList()))
            .build();

    List<Attribute<Double>> attrs =
        Arrays.asList(
            new Attribute<>(1.0, "a1", AttributeType.NUMERIC),
            new Attribute<>(2.0, "a2", AttributeType.NUMERIC),
            new Attribute<>(3.0, "a3", AttributeType.NUMERIC));
    Instance instance = new Instance(new double[] {1.0, 2.0, 3.0});
    InstanceSet set = new InstanceSet(Arrays.asList(instance), header, "test set");

    // 2. Initial checks
    assertEquals(3, set.getAttributeNames().size());
    assertEquals(3, instance.values().length);

    // 3. Drop "a2"
    set.dropFields(Arrays.asList("a2"));

    // 4. Verify hard delete
    assertEquals(2, set.getAttributeNames().size());
    assertEquals(Arrays.asList("a1", "a3"), set.getAttributeNames());

    assertEquals(2, instance.values().length);
    assertEquals(1.0, instance.values()[0]);
    assertEquals(3.0, instance.values()[1]);
  }
}
