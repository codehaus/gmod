package groovy2.lang;

public interface MetaClassMutator extends AutoCloseable {
  public void addMixin(MetaClass mixin);
  public Attribute addAttribute(String name, MetaClass type);
  public Property addProperty(String name, MetaClass type);
  public Method addMethod(int modifiers, String name, Closure closure);
  public Method addConstructor(int modifiers, Closure closure);
  
  public void removeMixin(MetaClass mixin);
  public void removeAttribute(String name);
  public void removeProperty(String name);
  public void removeMethod(String name, MetaClass... types);
  public void removeConstructor(MetaClass... types);
  
  @Override
  public void close();
}
