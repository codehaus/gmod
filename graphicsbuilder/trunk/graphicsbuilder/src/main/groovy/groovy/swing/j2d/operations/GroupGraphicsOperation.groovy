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
import groovy.swing.j2d.Grouping
import groovy.swing.j2d.Transformable
import groovy.swing.j2d.TransformationGroup
import groovy.swing.j2d.impl.AbstractNestingGraphicsOperation

import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GroupGraphicsOperation extends AbstractNestingGraphicsOperation implements Transformable, Grouping {
    protected static optional = ['borderColor','borderWidth','fill']

    private def g
    TransformationGroup transformationGroup

    // properties
    def borderColor
    def borderWidth
    def fill

    public GroupGraphicsOperation() {
        super( "group" )
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

    /*
    public void propertyChange( PropertyChangeEvent event ) {
       if( event.source == transformationGroup ){
          super.firePropertyChange( event )
       }
    }
    */

    protected void executeBeforeAll( GraphicsContext context ) {
       if( transformationGroup ){
          g = context.g
          context.g = context.g.create()
          context.g.transform( transformationGroup.getTransform() )
       }
    }

    protected void executeAfterAll( GraphicsContext context ) {
       if( transformationGroup ){
          context.g.dispose()
          context.g = g
       }
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
       setPropertyOnNestedOperation( go, "borderColor" )
       setPropertyOnNestedOperation( go, "borderWidth" )
       setPropertyOnNestedOperation( go, "fill" )
       go.execute( context )
    }
}