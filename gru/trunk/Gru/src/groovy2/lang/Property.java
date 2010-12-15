package groovy2.lang;

public interface Property {
  public MetaClass getDeclaringMetaClass();
  
  public int getModifiers();
  public String getName();
  public MetaClass getType();
  
  public Closure getGetter();
  public Closure getSetter();
}
