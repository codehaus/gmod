package org.codehaus.groovy2.compiler.type;

import org.codehaus.groovy.ast.ClassNode;

public class PrimaryType implements Type {
  private final ClassNode primaryNode;

  public PrimaryType(ClassNode primaryNode) {
    this.primaryNode = primaryNode;
  }
  
  public ClassNode getPrimaryNode() {
    return primaryNode;
  }
  
  @Override
  public String toString() {
    return primaryNode.toString();
  }
  
  @Override
  public String getName() {
    return primaryNode.getName();
  }
  
  @Override
  public <R, P> R accept(org.codehaus.groovy2.compiler.type.TypeVisitor<? extends R,? super P> visitor, P param) {
    return visitor.visitPrimaryType(this, param);
  }
}
