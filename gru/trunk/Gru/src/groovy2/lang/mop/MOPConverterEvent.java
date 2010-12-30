package groovy2.lang.mop;

import groovy2.lang.FunctionType;

import java.dyn.MethodHandle;

public class MOPConverterEvent extends MOPEvent {
  private final FunctionType signature;
  
  public MOPConverterEvent(Class<?> callerClass, boolean lazyAllowed, MethodHandle fallback, MethodHandle reset, FunctionType signature) {
    super(callerClass, lazyAllowed, fallback, reset);
    this.signature = signature;
  }
  
  public FunctionType getSignature() {
    return signature;
  }
}
