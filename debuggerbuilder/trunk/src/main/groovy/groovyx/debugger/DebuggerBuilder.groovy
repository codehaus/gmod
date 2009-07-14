package groovyx.debugger

import groovy.lang.Closure
import groovy.lang.GroovyObjectSupport

class DebuggerBuilder extends GroovyObjectSupport {
    private debugger

    def debugger(Closure closure) {
        debugger = new Debugger()
        try {
            closure.setDelegate(this)
            closure.call()

            return debugger
        } finally {
            debugger = null
        }
    }

    void breakpoint(Map attr, Closure handler) {
        assert debugger != null

        assert !attr.empty
        assert attr.class
        assert handler != null

        def className = attr['class']

        def breakpointDefinition
        if (attr.containsKey("method")) {
            breakpointDefinition = Breakpoint.stopIn(
                className, attr.method, handler)
        } else if (attr.containsKey("line")) {
            breakpointDefinition = Breakpoint.stopAt(
                className, attr.line, handler)
        }

        assert breakpointDefinition

        debugger.addBreakpointDefinition(breakpointDefinition)
    }
}
