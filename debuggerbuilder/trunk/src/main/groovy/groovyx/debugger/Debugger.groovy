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

import com.sun.jdi.Bootstrap
import com.sun.jdi.StackFrame
import com.sun.jdi.event.VMDeathEvent
import com.sun.jdi.event.VMDisconnectEvent

class Debugger {
    private vm
    private breakpointDefinitions = []
    private requestHandlers = [:]

    void addBreakpointDefinition(breakpointDefinition) {
        if (vm == null) {
            breakpointDefinitions << breakpointDefinition
        }
    }

    void attach(String transportName, Map transportArgs) {
        if (vm != null) {
            throw new IllegalStateException()
        }

        def connector = findConnector(transportName)

        def attachArgs = connector.defaultArguments()
        transportArgs.each { k, v ->
            def arg = attachArgs[k.toString()]
            if (arg == null) {
                throw new IllegalArgumentException(
                    "Unknown attribute ${k.toString()}")
            }
            arg.setValue(v?.toString())
        }

        vm = connector.attach(attachArgs)

        processEvents()
    }

    void printStackTrace(PrintStream out, List<StackFrame> frames, indent="") {
        doPrintStackTrace(out, frames, indent)
    }

    void printStackTrace(PrintWriter out, List<StackFrame> frames, indent="") {
        doPrintStackTrace(out, frames, indent)
    }

    private void doPrintStackTrace(out, List<StackFrame> frames, indent) {
        for (frame in frames) {
            def loc = frame.location()
            def method = loc.method()
            def locString
            if (method.isNative()) {
                locString = "Native Method"
            } else {
                locString = "${loc.sourceName()}:${loc.lineNumber()}"
            }

            out.println "${indent}at ${method.declaringType().name()}.${method.name()}(${locString})"
        }
    }

    private findConnector(String transportName) {
        def vmm = Bootstrap.virtualMachineManager()
        def connectors = vmm.attachingConnectors().grep {
            it.transport().name() == transportName
        }

        if (connectors.empty) {
            die "Unable to locate $transportName connector"
        }

        return connectors[0]
    }

    private void processEvents() {
        assert vm != null

        def eventRequests = []
        if (breakpointDefinitions) {
            def erm = vm.eventRequestManager()
            for (bd in breakpointDefinitions) {
                def classes = vm.classesByName(bd.className)
                if (classes.empty) {
                    // DEFER bd...
                    continue
                }

                for (c in classes) {
                    if (bd.method) {
                        def found = []
                        for (m in c.methods()) {
                            def subject
                            if (bd.method instanceof Closure) {
                                subject = m
                            } else {
                                subject = m.name()
                            }
                            if (bd.method.isCase(subject)) {
                                found << m
                            }
                        }

                        if (found.empty) {
                            throw new IllegalArgumentException("No such method: $bd.method")
                        }

                        for (m in found) {
                            def breakpoint = erm.createBreakpointRequest(m.locationOfCodeIndex(0))
                            if (bd.suspendPolicy) {
                                breakpoint.suspendPolicy = bd.suspendPolicy
                            }
                            requestHandlers[breakpoint] = bd.handler
                            eventRequests << breakpoint
                        }
                    } else if (bd.line) {
                    }
                }
            }
        }

        def queue = vm.eventQueue()

        eventRequests.each { it.enable() }

        while (true) {
            def events = queue.remove()
            for (event in events) {
                if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                    return
                }

                def handler = requestHandlers[event.request()]
                if (handler) {
                    try {
                        handler.delegate = this
                        handler(event)
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }
            events.resume()
        }
    }
}
