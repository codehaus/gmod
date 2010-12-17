package groovy2.lang.mop;

import groovy2.lang.FunctionType;

import java.dyn.MethodHandle;

public class MOPNewInstanceEvent extends MOPEvent {
  private final FunctionType signature;
  
  public MOPNewInstanceEvent(Class<?> callerClass, MethodHandle fallback, MethodHandle reset, FunctionType signature) {
    super(callerClass, fallback, reset);
    this.signature = signature;
  }
  
  public FunctionType getSignature() {
    return signature;
  }
}
