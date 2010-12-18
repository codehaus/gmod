package groovy2.lang.mop;

import groovy2.lang.MetaClass;

import java.dyn.MethodHandle;

public class MOPConvertEvent extends MOPEvent {
  private final MetaClass type;
  
  public MOPConvertEvent(Class<?> callerClass, boolean lazyAllowed, MethodHandle fallback, MethodHandle reset, MetaClass type) {
    super(callerClass, lazyAllowed, fallback, reset);
    this.type = type;
  }
  
  public MetaClass getType() {
    return type;
  }
}
