package groovy2.lang.mop;

import java.dyn.MethodHandle;

import groovy2.lang.FunctionType;

public class MOPDoCallEvent extends MOPEvent {
  private final FunctionType signature;
  
  public MOPDoCallEvent(Class<?> callerClass, boolean lazyAllowed, MethodHandle fallback, MethodHandle reset, FunctionType signature) {
    super(callerClass, lazyAllowed, fallback, reset);
    this.signature = signature;
  }
  
  public FunctionType getSignature() {
    return signature;
  }
}
