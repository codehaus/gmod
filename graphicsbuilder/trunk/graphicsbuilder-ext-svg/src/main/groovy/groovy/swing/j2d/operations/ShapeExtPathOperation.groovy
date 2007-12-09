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

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.OutlineProvider
import groovy.swing.j2d.ShapeProvider
import groovy.swing.j2d.impl.AbstractExtPathOperation
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShapeExtPathOperation extends AbstractExtPathOperation {
    def shape
    boolean connect

    public void propertyChange( PropertyChangeEvent event ){
       if( shape == event.source && event.source.required.contains(event.propertyName) ){
           // TODO signal change
       }
    }

    public void apply( ExtendedGeneralPath path, GraphicsContext context ) {
       if( (shape instanceof ShapeProvider || shape instanceof OutlineProvider )
           && shape.hasShape != null && shape.hasShape ){
          shape = shape.getShape(context)
       }
       path.append( shape, connect )
    }
}