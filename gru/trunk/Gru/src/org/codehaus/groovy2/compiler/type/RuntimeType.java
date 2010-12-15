package org.codehaus.groovy2.compiler.type;

public class RuntimeType implements Type {
  private final Class<?> type;
  
  public RuntimeType(Class<?> type) {
    this.type = type;
  }
  
  public Class<?> getType() {
    return type;
  }
  
  @Override
  public String getName() {
    return type.getName().toString();
  }
  
  @Override
  public String toString() {
    return type.getName().toString();
  }
  
  @Override
  public <R, P> R accept(TypeVisitor<? extends R,? super P> visitor, P param) {
    return visitor.visitRuntimeType(this, param);
  }
}