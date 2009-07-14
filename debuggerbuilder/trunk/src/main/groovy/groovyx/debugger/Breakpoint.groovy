package groovyx.debugger

class Breakpoint {
    def className
    def method
    def line
    def handler
    def suspendPolicy

    public Breakpoint(className) {
        this.className = className
    }

    static stopIn(Object className, Object method, Closure handler) {
        def breakpoint = new Breakpoint(className)
        breakpoint.method = method
        breakpoint.handler = handler
        return breakpoint
    }

    static stopAt(Object className, int line, Closure handler) {
        def breakpoint = new Breakpoint(className)
        breakpoint.line = line
        breakpoint.handler = handler
        return breakpoint
    }
}
