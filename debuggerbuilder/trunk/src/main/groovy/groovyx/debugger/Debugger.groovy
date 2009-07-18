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

import java.util.logging.Logger

import com.sun.jdi.AbsentInformationException
import com.sun.jdi.Bootstrap
import com.sun.jdi.StackFrame
import com.sun.jdi.event.VMDeathEvent
import com.sun.jdi.event.VMDisconnectEvent

class Debugger {
    private static final Logger log = Logger.getLogger(Debugger.class.name)

    private vm
    private breakpointDefinitions = []
    private requestHandlers = new WeakHashMap()

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

        def erm = vm.eventRequestManager()
        requestHandlers[erm.createClassPrepareRequest()] = { event ->
            processBreakpointDefinitions(event.referenceType())
        }

        processBreakpointDefinitions(vm.allClasses())

        processEvents()
    }

    void dumpVisibleVariables(out, frame) {
        def toDump
        try {
            toDump = frame.getValues(frame.visibleVariables()).collect { k, v ->
                return "${k.typeName()} ${k.name()}=$v"
            }
        } catch (AbsentInformationException e) {
            log.warning("Unable to dump local variables; use -g to add debug information")
            def k = 0
            toDump = frame.getArgumentValues().collect { v ->
                k += 1
                return "<argument $k>=$v"
            }
        }

        toDump.each { out.println(it) }
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
                try {
                    locString = "${loc.sourceName()}:${loc.lineNumber()}"
                } catch (AbsentInformationException e) {
                    locString = "Unknown Source"
                }
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

    private void processBreakpointDefinitions(classes) {
        if (!breakpointDefinitions) {
            return
        }

        def eventRequests = []
        def erm = vm.eventRequestManager()
        for (bd in breakpointDefinitions) {
            for (c in classes.grep { matchClass(bd.className, it) }) {
                if (bd.method) {
                    def matchedMethods = c.methods().grep { method ->
                        matchMethod(bd.method, method)
                    }

                    if (matchedMethods.empty) {
                        throw new IllegalArgumentException("No such matching methods for specification: $bd.method")
                    }

                    for (method in matchedMethods) {
                        def breakpoint = erm.createBreakpointRequest(method.locationOfCodeIndex(0))
                        if (bd.suspendPolicy) {
                            breakpoint.suspendPolicy = bd.suspendPolicy
                        }
                        requestHandlers[breakpoint] = bd.handler
                        eventRequests << breakpoint
                    }
                } else if (bd.line) {
                    throw new UnsupportedOperationException()
                }
            }
        }

        eventRequests.each { it.enable() }
    }

    private void processEvents() {
        assert vm != null

        def queue = vm.eventQueue()

        while (true) {
            def events = queue.remove()
            try {
                for (event in events) {
                    if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                        return
                    }

                    def handler = requestHandlers[event.request()]
                    if (handler) {
                        try {
                            handler.delegate = this
                            handler(event)
                        } catch (InterruptedException e) {
                            throw e
                        } catch (e) {
                            log.error("Error executing handler for event: $event", e)
                        }
                    }
                }
            } finally {
                events.resume()
            }
        }
    }

    private boolean matchClass(classSpec, clazz) {
        def subject
        if (classSpec instanceof Closure) {
            subject = clazz
        } else {
            subject = clazz.name()
        }
        return classSpec.isCase(subject)
    }

    private boolean matchMethod(methodSpec, method) {
        def subject
        if (methodSpec instanceof Closure) {
            subject = method
        } else {
            subject = method.name()
        }
        return methodSpec.isCase(subject)
    }
}
