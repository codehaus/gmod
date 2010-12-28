package groovy2.lang;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;

import org.codehaus.groovy2.lang.RT;

public class Failures {
  static class Failure implements Closure {
    private static final FunctionType VOID =
        new FunctionType(RT.getMetaClass(void.class));
    
    private final Throwable throwable;
    
    public Failure(Throwable throwable) {
      this.throwable = throwable;
    }
    
    @Override
    public String toString() {
      return "failure "+throwable.toString();
    }
    
    @Override
    public MethodHandle asMethodHandle() {
      return MethodHandles.throwException(void.class, throwable.getClass()).bindTo(throwable);
    }
    
    @Override
    public boolean isVarargs() {
      return false;
    }

    @Override
    public int getParameterCount() {
      return 0;
    }

    @Override
    public String getParameterName(int index) {
      throw new IllegalStateException("no parameter");
    }

    @Override
    public FunctionType getFunctionType() {
      return VOID;
    }

    @Override
    public Closure curry(int index, Object... args) {
      throw new IllegalStateException("no parameter");
    }

    @Override
    public Closure memoize(ClosureMemoizer closureMemoizer) {
      throw new IllegalStateException("no parameter");
    }
  }
  
  public static boolean isFailure(Closure closure) {
    return closure instanceof Failure; 
  }
  
  public static Closure fail(String message) {
    return new Failure(new Throwable(message));
  }
  
  public static Closure fail(Throwable throwable) {
    return new Failure(throwable);
  }
}
