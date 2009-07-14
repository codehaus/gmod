package groovyx.debugger

abstract class Script extends groovy.lang.Script {
    private builder

    protected Script() {
        super()
        init()
    }

    protected Script(Binding binding) {
        super(binding)
        init()
    }

    private void init() {
        builder = new DebuggerBuilder()
    }

    def getProperty(String property) {
        builder.getProperty(property)
    }

    void setProperty(String property, Object newValue) {
        builder.setProperty(property, newValue)
    }

    def invokeMethod(String name, Object args) {
        builder.invokeMethod(name, args)
    }
}
