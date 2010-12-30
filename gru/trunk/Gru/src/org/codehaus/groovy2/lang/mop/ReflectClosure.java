package org.codehaus.groovy2.lang.mop;

import groovy2.lang.Closure;
import groovy2.lang.FunctionType;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;

import org.codehaus.groovy2.lang.RT;

public class ReflectClosure implements Closure {
  private final boolean varargs;
  private final MethodHandle methodHandle;
  private FunctionType functionType;  // lazy created
  
  public ReflectClosure(boolean varargs, MethodHandle methodHandle) {
    this.methodHandle = methodHandle;
    this.varargs = varargs;
  }

  @Override
  public String toString() {
    return methodHandle.toString();
  }
  
  @Override
  public boolean isVarargs() {
    return varargs;
  }
  @Override
  public MethodHandle asMethodHandle() {
    return methodHandle;
  }

  @Override
  public int getParameterCount() {
    return methodHandle.type().parameterCount();
  }

  @Override
  public String getParameterName(int index) {
    return "args"+index;
  }

  @Override
  public FunctionType getFunctionType() {
    if (functionType == null) {
      functionType = RT.asFunctionType(methodHandle.type());
    }
    return functionType;
  }

  /*@Override
  public Object invoke(Object... args) throws Throwable {
    assert !varargs; //TODO
    return asMethodHandle().invokeVarargs(args);
  }*/

  @Override
  public Closure curry(int index, Object... args) {
    assert !varargs; //TODO
    return new ReflectClosure(
        false,
        MethodHandles.insertArguments(asMethodHandle(), index, args));
  }

  @Override
  public Closure memoize(ClosureMemoizer closureMemoizer) {
    // TODO Auto-generated method stub
    return this;
  }

}
