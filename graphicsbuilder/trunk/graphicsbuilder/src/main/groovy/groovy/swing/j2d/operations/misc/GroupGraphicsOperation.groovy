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

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.Grouping
import groovy.swing.j2d.operations.PaintProvider
import groovy.swing.j2d.operations.Transformable
import groovy.swing.j2d.operations.TransformationGroup
import groovy.swing.j2d.operations.AbstractNestingGraphicsOperation
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.awt.AlphaComposite
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GroupGraphicsOperation extends AbstractNestingGraphicsOperation implements Transformable, Grouping {
    public static optional = ['borderColor','borderWidth','fill','opacity']

    private def previousGroupContext
    private def gcopy
    TransformationGroup transformationGroup
    TransformationGroup globalTransformationGroup

    // properties
    def borderColor
    def borderWidth
    def fill
    def opacity

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

    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == transformationGroup ||
           event.source == globalTransformationGroup ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    protected void executeBeforeAll( GraphicsContext context ) {
       previousGroupContext = [:]
       previousGroupContext.putAll(context.groupContext)

       def o = opacity
       if( context.groupContext.opacity ){
          o = context.groupContext.opacity
       }
       if( opacity != null ){
          o = opacity
       }

       //if( operations || o != null ){
          gcopy = context.g
          context.g = context.g.create()
          if( o != null ){
             context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, o as float)
          }else{
             context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
          }
       //}

       if( borderColor != null ) context.groupContext.borderColor = borderColor
       if( borderWidth != null ) context.groupContext.borderWidth = borderWidth
       if( opacity != null ) context.groupContext.opacity = opacity
       if( fill != null ) context.groupContext.fill = fill
    }

    protected void executeAfterAll( GraphicsContext context ) {
       //if( operations ){
          context.g.dispose()
          context.g = gcopy
       //}
       context.groupContext = previousGroupContext
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
       if( go instanceof Transformable && !(go instanceof PaintProvider) ){
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