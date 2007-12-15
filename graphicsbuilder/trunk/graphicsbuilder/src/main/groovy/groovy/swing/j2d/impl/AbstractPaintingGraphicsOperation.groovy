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

package groovy.swing.j2d.impl

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.PaintProvider
import java.beans.PropertyChangeListener

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractPaintingGraphicsOperation extends AbstractGraphicsOperation implements PaintProvider {
    protected static optional = ['asPaint']
    
    // properties
    def asPaint

    AbstractPaintingGraphicsOperation( String name ) {
        super( name )
    }

    protected void executeOperation( GraphicsContext context ) {
        if( !asPaint ) {
           context.g.paint = getPaint(context, context.g.clipBounds)
        }
    }
}