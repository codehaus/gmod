package groovy2.lang.mop;

import java.dyn.MethodHandle;

import groovy2.lang.FunctionType;

public class MOPInvokeEvent extends MOPEvent {
  private final boolean isStatic;
  private final String name;
  private final FunctionType signature;
  
  public MOPInvokeEvent(Class<?> callerClass, boolean lazyAllowed, MethodHandle fallback, MethodHandle reset, boolean isStatic, String name, FunctionType signature) {
    super(callerClass, lazyAllowed, fallback, reset);
    this.isStatic = isStatic;
    this.name = name;
    this.signature = signature;
  }
  
  public boolean isStatic() {
    return isStatic;
  }
  
  public String getName() {
    return name;
  }
  public FunctionType getSignature() {
    return signature;
  }
}
