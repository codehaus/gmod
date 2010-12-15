package org.codehaus.groovy2.compiler.type;

public interface Type {
  public String getName();
  
  public <R,P> R accept(TypeVisitor<? extends R, ? super P> visitor, P param);
}
