package groovy2.lang;

import java.dyn.MethodHandle;

public interface Closure {
  //do(...)
  
  public MethodHandle asMethodHandle();
  
  public boolean isVarargs();
  
  public int getParameterCount();
  public String getParameterName(int index);
  public FunctionType getFunctionType();
  
  // magic !!
  //public Object invoke(Object... args) throws Throwable;
  
  public Closure curry(int index, Object... args);
  
  public Closure memoize(ClosureMemoizer closureMemoizer);
  
  public interface ClosureMemoizer extends Closure {
    // just a closure
  }
}
