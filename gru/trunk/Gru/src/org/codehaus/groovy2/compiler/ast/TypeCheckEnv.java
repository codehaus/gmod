package org.codehaus.groovy2.compiler.ast;

import org.codehaus.groovy2.compiler.type.Type;

public class TypeCheckEnv {
  private final Type returnType;
  private final Type expectedType;
  
  public TypeCheckEnv(Type returnType, Type expectedType) {
    this.returnType = returnType;
    this.expectedType = expectedType;
  }
  
  public Type getReturnType() {
    return returnType;
  }
  
  public Type getExpectedType() {
    return expectedType;
  }
  
  public TypeCheckEnv expectedType(Type expectedType) {
    return new TypeCheckEnv(returnType, expectedType);
  }
}
