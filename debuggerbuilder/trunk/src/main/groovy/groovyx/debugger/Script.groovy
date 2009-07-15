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
