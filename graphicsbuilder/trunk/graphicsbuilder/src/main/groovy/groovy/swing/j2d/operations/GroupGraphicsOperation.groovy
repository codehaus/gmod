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
import groovy.swing.j2d.impl.TransformationGroup
import groovy.swing.j2d.impl.AbstractNestingGraphicsOperation

import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GroupGraphicsOperation extends AbstractNestingGraphicsOperation implements Transformable, Grouping {
    protected static optional = ['borderColor','borderWidth','fill']

    private def previousGroupContext
    private def g
    TransformationGroup transformationGroup
    TransformationGroup globalTransformationGroup

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

    public void propertyChange( PropertyChangeEvent event ) {
       if( event.source == transformationGroup ){
          super.firePropertyChange( event )
       }
       if( event.source == globalTransformationGroup ){
          super.firePropertyChange( event )
       }
    }

    protected void executeBeforeAll( GraphicsContext context ) {
       if( operations ){
          g = context.g
          context.g = context.g.create()
       }
       previousGroupContext = context.groupContext
       if( borderColor != null ) context.groupContext.borderColor = borderColor
       if( borderWidth != null ) context.groupContext.borderWidth = borderWidth
       if( fill != null ) context.groupContext.fill = fill
    }

    protected void executeAfterAll( GraphicsContext context ) {
       if( operations ){
          context.g.dispose()
          context.g = g
       }
       context.groupContext = previousGroupContext
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
       if( go instanceof Transformable ){
          if( transformationGroup ){
             def gtg = go.globalTransformationGroup
             if( !gtg ){
                gtg = new TransformationGroup()
                go.globalTransformationGroup = gtg
             }
             gtg.addTransformation( transformationGroup )
          }
          if( globalTransformationGroup ){
             def gtg = go.globalTransformationGroup
             if( !gtg ){
                gtg = new TransformationGroup()
                go.globalTransformationGroup = gtg
             }
             gtg.addTransformation( globalTransformationGroup )
          }
       }
       go.execute( context )
    }
}