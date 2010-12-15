package groovy2.lang;

public interface Method extends Closure {
  // do(...)
  
  public MetaClass getDeclaringMetaClass();
  
  public int getModifiers();
  public String getName();  
  
  public FunctionType getMethodType();
}
