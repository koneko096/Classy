package id.koneko096.classy.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CrossSplit {
  private final List<InstanceSet> trainSets;
  private final List<InstanceSet> testSets;

  public int size() {
    return this.trainSets.size();
  }
}
