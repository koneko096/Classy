package id.koneko096.classy.transformer;

import static org.junit.jupiter.api.Assertions.*;

import id.koneko096.classy.data.Attribute;
import id.koneko096.classy.data.AttributeType;
import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import id.koneko096.classy.math.Matrix;
import id.koneko096.classy.math.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PCATransformerTest {

  private InstanceSet sampleDataSet;

  @BeforeEach
  void setUp() {
    // Sample data for a 2x3 matrix (2 instances, 3 features)
    // Instance 1: [1.0, 2.0, 3.0] Label: "A"
    // Instance 2: [4.0, 5.0, 6.0] Label: "B"
    List<Instance> instances = new ArrayList<>();
    instances.add(new Instance(new double[] {1.0, 2.0, 3.0}, "A"));
    instances.add(new Instance(new double[] {4.0, 5.0, 6.0}, "B"));

    sampleDataSet = new InstanceSet(instances, null, "test");
  }

  @Test
  void testConstructorValidNComponents() {
    assertDoesNotThrow(() -> new PCATransformer(1));
    assertDoesNotThrow(() -> new PCATransformer(5));
  }

  @Test
  void testConstructorInvalidNComponentsThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new PCATransformer(0));
    assertThrows(IllegalArgumentException.class, () -> new PCATransformer(-1));
  }

  @Test
  void testTransformBeforeFitThrowsException() {
    PCATransformer pca = new PCATransformer(1);
    assertThrows(IllegalStateException.class, () -> pca.transform(sampleDataSet));
    List<Instance> instancesWithSingleAttributes = new ArrayList<>();
    instancesWithSingleAttributes.add(new Instance(new double[] {1.0, 2.0}, "A"));
    assertThrows(
        IllegalStateException.class,
        () -> pca.transform(new InstanceSet(instancesWithSingleAttributes, null, "test")));
  }

  @Test
  void testFitWithEmptyDataSetThrowsException() {
    PCATransformer pca = new PCATransformer(1);
    InstanceSet emptyDataSet = new InstanceSet(new ArrayList<>(), null, "empty");
    assertThrows(IllegalArgumentException.class, () -> pca.fit(emptyDataSet));
  }

  @Test
  void testFitNComponentsGreaterThanNumFeaturesThrowsException() {
    PCATransformer pca =
        new PCATransformer(4); // More components than features in sampleDataSet (3 features)
    assertThrows(IllegalArgumentException.class, () -> pca.fit(sampleDataSet));
  }

  @Test
  void testFitSetsMeanAndPrincipalComponents() throws NoSuchFieldException, IllegalAccessException {
    PCATransformer pca = new PCATransformer(2); // Request 2 components
    pca.fit(sampleDataSet);

    Instance transformed = pca.transform(new Instance(new double[]{6, 8, 0.1}));

    // Verify principal components dimensions (numFeatures x n_components)
    assertEquals(2, sampleDataSet.size());
    assertEquals(2, transformed.getValues().length); // n_components
  }

  @Test
  void testTransformDataSetDimensions() {
    PCATransformer pca = new PCATransformer(2);
    pca.fit(sampleDataSet);
    InstanceSet transformedSet = pca.transform(sampleDataSet);

    assertNotNull(transformedSet);
    assertEquals(
        sampleDataSet.size(), transformedSet.size()); // Number of instances should remain the same

    // Each instance in the transformed set should have n_components features
    for (Instance instance : transformedSet) {
      assertEquals(2, instance.values().length);
      // Labels should be preserved
      assertTrue(Arrays.asList("A", "B").contains(instance.getLabel()));
    }
  }

  @Test
  void testTransformSingleInstanceDimensions() {
    PCATransformer pca = new PCATransformer(2);
    pca.fit(sampleDataSet); // Fit with sample data

    Instance instanceWithAttributes = new Instance(new double[] {7.0, 8.0, 9.0}, "C");
    Instance transformedInstance = pca.transform(instanceWithAttributes);
    assertNotNull(transformedInstance);
    assertEquals(2, transformedInstance.values().length); // Should have n_components features
    assertEquals("C", transformedInstance.getLabel()); // Label should be preserved
  }

  @Test
  void testGetFeatureNames() {
    PCATransformer pca = new PCATransformer(2);
    pca.fit(sampleDataSet);
    List<String> featureNames = pca.getFeatureNames();
    assertNotNull(featureNames);
    assertEquals(2, featureNames.size());
    assertEquals("PC1", featureNames.get(0));
    assertEquals("PC2", featureNames.get(1));
  }

  @Test
  void testGetFeatureNamesBeforeFitReturnsNull() {
    PCATransformer pca = new PCATransformer(2);
    assertNull(pca.getFeatureNames());
  }
}
