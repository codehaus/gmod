package groovy2.lang.mop;

import java.dyn.MethodHandle;

public class MOPEvent {
  private final Class<?> callerClass;
  private final MethodHandle fallback;
  private final MethodHandle reset;
  
  public MOPEvent(Class<?> callerClass, MethodHandle fallback, MethodHandle reset) {
    this.callerClass = callerClass;
    this.fallback = fallback;
    this.reset = reset;
  }
  
  /** Returns the caller class or null if there is no caller class.
   * @return the caller class or null otherwise.
   */
  public Class<?> getCallerClass() {
    return callerClass;
  }
  
  public MethodHandle getFallback() {
    return fallback;
  }
  
  public MethodHandle getReset() {
    return reset;
  }
}
