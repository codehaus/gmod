package groovy2.lang.mop;

import java.dyn.MethodHandle;

import groovy2.lang.FunctionType;

public class MOPDoCallEvent extends MOPEvent {
  private final FunctionType signature;
  
  public MOPDoCallEvent(Class<?> callerClass, MethodHandle reset, FunctionType signature) {
    super(callerClass, reset);
    this.signature = signature;
  }
  
  public FunctionType getSignature() {
    return signature;
  }
}
