package org.codehaus.groovy2.compiler.ast;

import org.codehaus.groovy2.compiler.type.Type;
import org.codehaus.groovy2.compiler.type.TypeScope;

public class TypeCheckEnv {
  private final Type returnType;
  private final TypeScope typeScope;
  private final Type expectedType;
  
  public TypeCheckEnv(Type returnType, TypeScope typeScope, Type expectedType) {
    this.returnType = returnType;
    this.typeScope = typeScope;
    this.expectedType = expectedType;
  }
  
  public Type getReturnType() {
    return returnType;
  }
  
  public TypeScope getTypeScope() {
    return typeScope;
  }
  
  public Type getExpectedType() {
    return expectedType;
  }
  
  public TypeCheckEnv expectedType(Type expectedType) {
    return new TypeCheckEnv(returnType, typeScope, expectedType);
  }
}
