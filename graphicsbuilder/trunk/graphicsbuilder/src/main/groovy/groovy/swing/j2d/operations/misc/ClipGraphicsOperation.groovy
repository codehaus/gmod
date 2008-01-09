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

package groovy.swing.j2d.operations.misc

import java.awt.Shape

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.ShapeProvider
import groovy.swing.j2d.operations.Transformable
import groovy.swing.j2d.operations.AbstractGraphicsOperation
import groovy.swing.j2d.operations.TransformationGroup
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ClipGraphicsOperation extends AbstractGraphicsOperation implements Transformable {
    public static required = ['shape']

    TransformationGroup transformationGroup
    TransformationGroup globalTransformationGroup

    def shape

    ClipGraphicsOperation() {
        super( "clip" )
    }

    public void setTransformationGroup( TransformationGroup transformationGroup ){
       if( transformationGroup ) {
          if( this.transformationGroup ){
             this.transformationGroup.removePropertyChangeListener( this )
          }
          this.transformationGroup = transformationGroup
          this.transformationGroup.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getTransformationGroup() {
       transformationGroup
    }

    public void setGlobalTransformationGroup( TransformationGroup globalTransformationGroup ){
       if( globalTransformationGroup ) {
          if( this.globalTransformationGroup ){
             this.globalTransformationGroup.removePropertyChangeListener( this )
          }
          this.globalTransformationGroup = globalTransformationGroup
          this.globalTransformationGroup.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getGlobalTransformationGroup() {
       globalTransformationGroup
    }

    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == transformationGroup ||
           event.source == globalTransformationGroup ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    void setProperty( String property, Object value ) {
       if( property == "shape" ){
          if( value instanceof ShapeProvider ){
             if( value != shape && shape instanceof ShapeProvider ){
                shape.removePropertyChangeListener( this )
             }
             value.addPropertyChangeListener( this )
          }
       }
       super.setProperty( property, value )
    }

    public void execute( GraphicsContext context ){
        if( !shape ) return
        def s = shape
        if( shape instanceof ShapeProvider ){
           s = shape.getLocallyTransformedShape(context)
        }
        if( transformationGroup && !transformationGroup.isEmpty() ){
           s = transformationGroup.apply( s )
        }

        if( globalTransformationGroup && !globalTransformationGroup.isEmpty() ){
           s = globalTransformationGroup.apply( s )
        }

        context.g.clip = s
    }
}