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

package groovy.swing.j2d.operations

import java.awt.Component
import java.awt.Container
import java.awt.Graphics2D
import java.awt.image.ImageObserver
import javax.swing.SwingUtilities as SU
import javax.swing.CellRendererPane

import groovy.swing.j2d.impl.AbstractGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SwingGraphicsOperation extends AbstractGraphicsOperation {
    private Container container

    SwingGraphicsOperation( Container container ) {
        super( "swing", [] as String[] )
        this.container = container
    }

    protected void doExecute( Graphics2D g, ImageObserver observer ){
        Component[] components = container.components
        def p = new CellRendererPane()
        components.each { component ->
           def bounds = component.bounds
           SU.paintComponent( g, component, p, bounds.x as int,
                   bounds.y as int, bounds.width as int, bounds.height as int)
        }
    }
}