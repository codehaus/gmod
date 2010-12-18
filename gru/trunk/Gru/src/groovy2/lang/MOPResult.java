package groovy2.lang;

import java.util.Arrays;
import java.util.List;

import org.codehaus.groovy2.dyn.Switcher;

public class MOPResult {
  private final Closure target;
  private final List<Switcher> conditions;
  
  public MOPResult(Closure target, Switcher switcher) {
    this(target, Arrays.asList(switcher));
  }
  
  public MOPResult(Closure target, List<Switcher> conditions) {
    this.target = target;
    this.conditions = conditions;
  }
  
  public Closure getTarget() {
    return target;
  }
  public List<Switcher> getConditions() {
    return conditions;
  }
}
