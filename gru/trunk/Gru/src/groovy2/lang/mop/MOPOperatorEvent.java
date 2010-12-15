package groovy2.lang.mop;

import java.dyn.MethodHandle;

import groovy2.lang.FunctionType;

public class MOPOperatorEvent extends MOPEvent {
  private final String name;
  private final FunctionType signature;
  
  public MOPOperatorEvent(Class<?> callerClass, MethodHandle reset, String name, FunctionType signature) {
    super(callerClass, reset);
    this.name = name;
    this.signature = signature;
  }
  
  public String getName() {
    return name;
  }
  public FunctionType getSignature() {
    return signature;
  }
}
