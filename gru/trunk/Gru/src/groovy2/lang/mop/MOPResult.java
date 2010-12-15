package groovy2.lang.mop;

import java.dyn.MethodHandle;

public final class MOPResult {
  private final MethodHandle target;
  private final Throwable failure;
  
  //TODO revisit: the error is a throwable to ease debugging
  public MOPResult(Throwable failure) {
    target = null;
    this.failure = failure;
  }
  
  public MOPResult(MethodHandle target) {
    this.target = target;
    this.failure = null;
  }
  
  public MethodHandle getTarget() {
    return target;
  }
  public Throwable getFailure() {
    return failure;
  }
}