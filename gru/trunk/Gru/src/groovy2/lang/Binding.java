package groovy2.lang;

import java.lang.reflect.Type;

public interface Binding {
  public String getName();
  public Type getType();
  
  public Object getValue();
  public void setValue(Object value);
}
