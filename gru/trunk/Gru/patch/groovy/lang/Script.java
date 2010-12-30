package groovy.lang;

/** Drop-in replacement to groovy.lang.Script.
 *  Remove dependency to GroovyObject
 */
public abstract class Script {
    protected Script() {
    }
    
    public void setBinding(Binding binding) {
      
    }
    
    public void setProperty(String property, Object newValue) {
      
    }
    
    public abstract Object run();
}
