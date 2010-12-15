package groovy2.lang;

import java.lang.reflect.Type;

public interface MetaClassMutator extends AutoCloseable {
  public void addMixin(MetaClass mixin);
  public Attribute addAttribute(int modifiers, String name, Type type);
  public Property addProperty(int modifiers, String name, Type type);
  public Method addMethod(int modifiers, String name, Closure closure);
  public Method addConstructor(int modifiers, Closure closure);
  
  public void removeMixin(MetaClass mixin);
  public void removeAttribute(String name);
  public void removeProperty(String name);
  public void removeMethod(String name, Type... types);
  public void removeConstructor(Type... types);
  
  @Override
  public void close();
}
