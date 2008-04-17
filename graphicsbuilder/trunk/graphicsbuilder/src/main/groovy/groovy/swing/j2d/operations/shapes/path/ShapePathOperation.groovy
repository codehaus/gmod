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

package groovy.swing.j2d.operations.shapes.path

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.OutlineProvider
import groovy.swing.j2d.operations.ShapeProvider
import java.awt.geom.GeneralPath
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShapePathOperation extends AbstractPathOperation {
    public static required = ['shape']
    public static optional = ['connect']

    def shape
    def connect = false

    ShapePathOperation(){
       super("shapeTo")
    }

    void setProperty( String property, Object value ) {
       if( property == "shape" && value instanceof ShapeProvider || value instanceof OutlineProvider ){
          value.addPropertyChangeListener( this )
       }
       super.setProperty( property, value )
    }

    public void propertyChange( PropertyChangeEvent event ){
       if( shape == event.source ){
          firePropertyChange( event )
       }
    }

    public void apply( GeneralPath path, GraphicsContext context ) {
       def s = shape
       if( shape instanceof ShapeProvider || shape instanceof OutlineProvider ){
          s = shape.runtime(context).locallyTransformedShape
       }
       path.append( s, connect as boolean )
    }
}