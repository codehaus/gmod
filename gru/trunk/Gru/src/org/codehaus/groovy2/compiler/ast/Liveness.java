package org.codehaus.groovy2.compiler.ast;

import org.codehaus.groovy2.compiler.type.Type;
import org.codehaus.groovy2.compiler.type.TypeVisitor;

public enum Liveness implements Type {
  ALIVE,
  DEAD;
  
  @Override
  public String getName() {
    return name();
  }
  
  @Override
  public <R, P> R accept(TypeVisitor<? extends R,? super P> visitor, P param) {
    throw new AssertionError("liveness is a fake Type");
  }
  
  //see TypeChecker.visitReturnStatement
  public static Liveness asLiveness(Type type) {
    return (type == ALIVE)? ALIVE: DEAD;
  }
}
