package id.koneko096.classy.data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class InstanceSetTest {

  @Test
  void testConstructorAndGetters() {
    List<Instance> instances = new ArrayList<>();
    Header header = Header.builder().attributeNames(Collections.singletonList("a1")).build();
    InstanceSet set = new InstanceSet(instances, header, "testSet");

    assertEquals(0, set.size());
    assertEquals(header, set.getHeader());
    assertEquals("testSet", set.getName());
    assertEquals(Collections.singletonList("a1"), set.getAttributeNames());
  }

  @Test
  void testAddAndRemove() {
    InstanceSet set = new InstanceSet(new ArrayList<>(), null, "test");
    Instance instance = new Instance(new double[] {});

    set.add(instance);
    assertEquals(1, set.size());
    assertTrue(set.contains(instance));

    set.remove(instance);
    assertEquals(0, set.size());
  }

  @Test
  void testSplitCorrectly() {
    List<Instance> instances = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      instances.add(new Instance(new double[] {}, "Label" + (i % 2)));
    }
    InstanceSet set = new InstanceSet(instances, null, "test");

    CrossSplit split = set.split(5);

    assertEquals(5, split.getTrainSets().size());
    assertEquals(5, split.getTestSets().size());

    // Each test set should have 2 instances (10/5)
    for (InstanceSet testSet : split.getTestSets()) {
      assertEquals(2, testSet.size());
      assertNotNull(testSet.getInstanceList());
    }

    // Each train set should have 8 instances (10-2)
    for (InstanceSet trainSet : split.getTrainSets()) {
      assertEquals(8, trainSet.size());
      assertNotNull(trainSet.getInstanceList());
    }
  }
}
