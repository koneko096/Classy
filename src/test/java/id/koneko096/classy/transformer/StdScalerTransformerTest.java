package id.koneko096.classy.transformer;

import static org.junit.jupiter.api.Assertions.*;

import id.koneko096.classy.data.Instance;
import id.koneko096.classy.data.InstanceSet;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StdScalerTransformerTest {

  private InstanceSet sampleDataSet;

  @BeforeEach
  void setUp() {
    // [1.0, 10.0], [2.0, 20.0], [3.0, 30.0]
    // Means: [2.0, 20.0]
    // Variances: [(1-2)^2+(2-2)^2+(3-2)^2]/3 = (1+0+1)/3 = 2/3
    // Standard Deviations: sqrt(2/3) ≈ 0.81649658

    List<Instance> instances = new ArrayList<>();
    instances.add(new Instance(new double[] {1.0, 10.0}, "A"));
    instances.add(new Instance(new double[] {2.0, 20.0}, "B"));
    instances.add(new Instance(new double[] {3.0, 30.0}, "C"));

    sampleDataSet = new InstanceSet(instances, null, "test");
  }

  @Test
  void testFitAndTransform() {
    StdScalerTransformer scaler = new StdScalerTransformer();
    scaler.fit(sampleDataSet);

    InstanceSet transformed = scaler.transform(sampleDataSet);

    assertEquals(3, transformed.size());

    // First instance: [1.0, 10.0] -> [(1-2)/sqrt(2/3), (10-20)/sqrt(200/3)]
    // Actually simpler: 10*x has 10*stddev.
    // For "a1": mean=2, stddev=sqrt(2/3)
    // Value 1.0 -> (1-2)/sqrt(2/3) ≈ -1.0 / 0.81649658 ≈ -1.22474487

    double firstVal = transformed.get(0).values()[0];
    assertTrue(Math.abs(firstVal + 1.224744871391589) < 1e-10);

    // Mean of transformed data should be 0
    double sum0 = 0, sum1 = 0;
    for (Instance i : transformed) {
      sum0 += i.values()[0];
      sum1 += i.values()[1];
    }
    assertTrue(Math.abs(sum0) < 1e-10);
    assertTrue(Math.abs(sum1) < 1e-10);
  }

  @Test
  void testZeroVariance() {
    List<Instance> instances = new ArrayList<>();
    instances.add(new Instance(new double[] {5.0}, "A"));
    instances.add(new Instance(new double[] {5.0}, "B"));
    InstanceSet dataset = new InstanceSet(instances, null, "test");

    StdScalerTransformer scaler = new StdScalerTransformer();
    scaler.fit(dataset);
    InstanceSet transformed = scaler.transform(dataset);

    // Value - mean = 5.0 - 5.0 = 0. Variance is 0. Scale should be 0.
    assertEquals(0.0, transformed.get(0).values()[0]);
  }
}
