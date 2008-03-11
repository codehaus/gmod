/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package groovy.swing.j2d.event

import java.util.EventObject
import groovy.swing.j2d.GraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GraphicsInputEvent extends EventObject {
    private EventObject event
    private GraphicsOperation target

    public GraphicsInputEvent( Object source, EventObject event, GraphicsOperation target ) {
        super( source )
        this.event = event
        this.target = target
    }

    public EventObject getEvent() {
        return event
    }

    public GraphicsOperation getTarget() {
        return target
    }
}
