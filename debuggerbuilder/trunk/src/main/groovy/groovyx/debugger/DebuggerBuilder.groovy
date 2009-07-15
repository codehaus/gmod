/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
