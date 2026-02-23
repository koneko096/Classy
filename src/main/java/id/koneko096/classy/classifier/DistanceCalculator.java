package id.koneko096.classy.classifier;

import id.koneko096.classy.data.Instance;

public interface DistanceCalculator {
  double calculate(Instance a, Instance b);

  String getName();
}
