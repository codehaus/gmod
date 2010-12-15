package org.codehaus.groovy2.lang.java;

import java.dyn.MethodHandle;

import org.codehaus.groovy2.lang.RT;

import groovy2.lang.FunctionType;
import groovy2.lang.MetaClass;
import groovy2.lang.Method;

public class JVMMethod extends JVMClosure implements Method {
  private final MetaClass declaringMetaClass;
  private final int modifiers;
  private final String name;
  private FunctionType methodType; // lazy
  
  private static final int VARARGS   = 0x00000080;
  
  public JVMMethod(MetaClass declaringMetaClass, int modifiers, String name, MethodHandle mh) {
    super((modifiers & VARARGS) != 0, mh);
    this.declaringMetaClass = declaringMetaClass;
    this.modifiers = modifiers;
    this.name = name;
  }

  @Override
  public MetaClass getDeclaringMetaClass() {
    return declaringMetaClass;
  }

  @Override
  public int getModifiers() {
    return modifiers;
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public FunctionType getMethodType() {
    if (methodType == null) {
      methodType = RT.asMethodType(asMethodHandle().type());
    }
    return methodType;
  }
  
  @Override
  public String toString() {
    return declaringMetaClass.toString()+'.'+name+getMethodType();
  }
}
