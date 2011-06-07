/*
* Copyright 2011 the original author or authors.
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

package groovyx.javafx.input

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
/**
 *
 * @author jimclarke
 */
class GroovyMouseHandler implements EventHandler<MouseEvent> {

    String type;
    Closure closure;

    public GroovyMouseHandler(String type) {
        this.type = type;
    }

    public void setClosure(closure) {
         this.closure = closure;
    }

    public String getType() { return type; }

    public void handle(MouseEvent event) {
        closure.call(event);
    }

    public String toString() {
        "type = ${type}, closure = ${closure}"
    }
}

