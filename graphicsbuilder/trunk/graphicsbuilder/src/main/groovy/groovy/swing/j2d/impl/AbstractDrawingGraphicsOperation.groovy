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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.PaintProvider
import groovy.swing.j2d.Transformable
import groovy.swing.j2d.impl.TransformationGroup

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Paint
import java.awt.Shape
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractDrawingGraphicsOperation extends AbstractNestingGraphicsOperation implements Transformable {
    protected static optional = ['borderColor','borderWidth','fill','asShape']

    protected Shape locallyTransformedShape
    protected Shape globallyTransformedShape
    private def g
    TransformationGroup transformationGroup
    TransformationGroup globalTransformationGroup

    // properties
    def borderColor
    def borderWidth
    def fill
    def asShape

    AbstractDrawingGraphicsOperation( String name ) {
        super( name )
    }

    public abstract Shape getShape( GraphicsContext context )

    public Shape getLocallyTransformedShape( GraphicsContext context ){
       if( !locallyTransformedShape ){
          calculateLocallyTransformedShape( context )
       }
       return locallyTransformedShape
    }

    public Shape getGloballyTransformedShape( GraphicsContext context ){
       if( !globallyTransformedShape ){
          calculateGloballyTransformedShape( context )
       }
       return globallyTransformedShape
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
       locallyTransformedShape = null
       globallyTransformedShape = null
    }

    protected void executeBeforeAll( GraphicsContext context ) {
       if( operations ){
          g = context.g
          context.g = context.g.create()
       }
    }

    protected void executeAfterAll( GraphicsContext context ) {
       if( operations ){
          context.g.dispose()
          context.g = g
       }
    }

    protected boolean executeAfterNestedOperations( GraphicsContext context ) {
       return withinClipBounds( context )
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
        go.execute( context )
    }

    protected void executeOperation( GraphicsContext context ) {
        if( !asShape ){
            fill( context )
            draw( context )
        }
    }

    protected void fill( GraphicsContext context ) {
       def g = context.g

       def f = fill
       if( context.groupContext.fill ){
          f = context.groupContext.fill
       }
       if( fill != null ){
          f = fill
       }

       // short-circuit
       // don't fill the shape if fill == false
       if( f instanceof Boolean && !f ){
           return
       }

       // honor the clip
       if( !withinClipBounds( context ) ) {
           return
       }

       if( f ){
          if( f instanceof Color ){
              def previousValue = g.color
              g.color = f
              applyFill( context )
              g.color = previousValue
          }else if( f instanceof Paint ){
              def previousValue = g.paint
              g.paint = f
              applyFill( context )
              g.paint = previousValue
          }else if( f instanceof String ){
              def previousValue = g.color
              g.color = ColorCache.getInstance().getColor( f )
              applyFill( context )
              g.color = previousValue
          }else if( f instanceof PaintProvider ){
              Paint paint = context.g.getPaint()
              context.g.setPaint( f.getPaint(context, getGloballyTransformedShape(context).bounds2D) )
              applyFill( context )
              context.g.setPaint( paint )
          }else {
             // look for a nested paintProvider
             def pp = operations.reverse().find{ it instanceof PaintProvider }
             if( pp ){
                Paint paint = context.g.getPaint()
                context.g.setPaint( pp.getPaint(context, getGloballyTransformedShape(context).bounds2D) )
                applyFill( context )
                context.g.setPaint( paint )
             }else{
                // use current settings on context
                applyFill( context )
             }
          }
       }else{
          // look for a nested paintProvider
          def pp = operations.reverse().find{ it instanceof PaintProvider }
          if( pp ){
             Paint paint = context.g.getPaint()
             context.g.setPaint( pp.getPaint(context, getGloballyTransformedShape(context).bounds2D) )
             applyFill( context )
             context.g.setPaint( paint )
          }
       }
    }

    protected void applyFill( GraphicsContext context ) {
        context.g.fill( getGloballyTransformedShape(context) )
    }

    protected void draw( GraphicsContext context ) {
       def previousColor = null
       def previousStroke = null

       def g = context.g

       def bc = borderColor
       if( context.groupContext.borderColor ){
          bc = context.groupContext.borderColor
       }
       if( fill != null ){
          bc = borderColor
       }
       def bw = borderWidth
       if( context.groupContext.borderWidth ){
          bw = context.groupContext.borderWidth
       }
       if( fill != null ){
          bw = borderWidth
       }

       // short-circuit
       // don't draw the shape if borderColor == false
       if( bc instanceof Boolean && !bc ){
           return
       }

       // honor the clip
       if( !withinClipBounds( context ) ) {
           return
       }

       // apply color & stroke
       if( bc ){
           previousColor = g.color
           if( bc instanceof String ){
               g.color = ColorCache.getInstance().getColor( bc )
           }else if( bc instanceof Color ){
               g.color = bc
           }
       }
       if( bw ){
           previousStroke = g.stroke
           g.stroke = new BasicStroke( bw )
       }

       // draw the shape
       g.draw( getGloballyTransformedShape(context) )

       // restore color & stroke
       if( previousColor ) g.color = previousColor
       if( previousStroke ) g.stroke = previousStroke
    }

    protected boolean withinClipBounds( GraphicsContext context ) {
       return getGloballyTransformedShape( context ).intersects( context.g.clipBounds )
    }

    protected void calculateLocallyTransformedShape( GraphicsContext context ) {
       if( transformationGroup && !transformationGroup.isEmpty() ){
          this.@locallyTransformedShape = transformationGroup.apply( getShape(context) )
       }else{
          this.@locallyTransformedShape = getShape(context)
       }
    }

    protected void calculateGloballyTransformedShape( GraphicsContext context ) {
       if( globalTransformationGroup && !globalTransformationGroup.isEmpty() ){
          this.@globallyTransformedShape = globalTransformationGroup.apply( getLocallyTransformedShape(context) )
       }else{
          this.@globallyTransformedShape = getLocallyTransformedShape(context)
       }
    }
}