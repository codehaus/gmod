/*
 * Copyright 2007-2008 the original author or authors.
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

    TransformationGroup transformations
    TransformationGroup globalTransformations

    def shape

    ClipGraphicsOperation() {
        super( "clip" )
        setTransformations( new TransformationGroup() )
        setGlobalTransformations( new TransformationGroup() )
    }

    public void setTransformations( TransformationGroup transformations ){
       if( transformations ) {
          if( this.transformations ){
             this.transformations.removePropertyChangeListener( this )
          }
          this.transformations = transformations
          this.transformations.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getTransformations() {
       transformations
    }
    
    public TransformationGroup getTxs() {
       transformations
    }

    public void setGlobalTransformations( TransformationGroup globalTransformations ){
       if( globalTransformations ) {
          if( this.globalTransformations ){
             this.globalTransformations.removePropertyChangeListener( this )
          }
          this.globalTransformations = globalTransformations
          this.globalTransformations.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getGlobalTransformations() {
       globalTransformations
    }

    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == transformations ||
           event.source == globalTransformations ){
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

    protected void doExecute( GraphicsContext context ){
        if( !shape ) return
        def s = shape
        if( shape instanceof ShapeProvider ){
           s = shape.runtime(context).locallyTransformedShape
        }
        if( transformations && !transformations.isEmpty() ){
           s = transformations.apply( s )
        }

        if( globalTransformations && !globalTransformations.isEmpty() ){
           s = globalTransformations.apply( s )
        }

        context.g.clip = s
    }
}
