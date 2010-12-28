package groovy2.lang.mop;

import groovy2.lang.Closure;

import java.util.Collections;
import java.util.Set;

import org.codehaus.groovy2.dyn.Switcher;

public class MOPResult {
  private final Closure target;
  private final Set<Switcher> conditions;
  
  public MOPResult(Closure target, Switcher switcher) {
    this(target, Collections.singleton(switcher));
  }
  
  public MOPResult(Closure target, Set<Switcher> conditions) {
    this.target = target;
    this.conditions = conditions;
  }
  
  public Closure getTarget() {
    return target;
  }
  public Set<Switcher> getConditions() {
    return conditions;
  }
}
