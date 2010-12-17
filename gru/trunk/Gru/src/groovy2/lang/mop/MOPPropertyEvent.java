package groovy2.lang.mop;

import java.dyn.MethodHandle;

import groovy2.lang.MetaClass;

public class MOPPropertyEvent extends MOPEvent {
  private final boolean isStatic;
  private final String name;
  private final MetaClass type;
  
  public MOPPropertyEvent(Class<?> callerClass, MethodHandle fallback, MethodHandle reset, boolean isStatic, String name, MetaClass type) {
    super(callerClass, fallback, reset);
    this.isStatic = isStatic;
    this.name = name;
    this.type = type;
  }
  
  public boolean isStatic() {
    return isStatic;
  }
  public String getName() {
    return name;
  }
  public MetaClass getType() {
    return type;
  }
}
