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
import groovy.swing.j2d.TransformationGroup

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

    protected Shape transformedShape
    private def g
    TransformationGroup transformationGroup

    // properties
    def borderColor
    def borderWidth
    def fill
    def asShape

    AbstractDrawingGraphicsOperation( String name ) {
        super( name )
    }

    public abstract Shape getShape( GraphicsContext context )

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

    public Shape getTransformedShape() {
       transformedShape
    }

    public void propertyChange( PropertyChangeEvent event ) {
       if( event.source == transformationGroup ){
          super.firePropertyChange( event )
       }
    }

    protected void executeBeforeAll( GraphicsContext context ) {
       if( transformationGroup || operations ){
          g = context.g
          context.g = context.g.create()
          if( transformationGroup ){
             context.g.transform( transformationGroup.getTransform() )
          }
       }
       if( !context.g.getTransform().isIdentity() ){
          transformedShape = context.g.transform.createTransformedShape(getShape(context))
       }
    }

    protected void executeAfterAll( GraphicsContext context ) {
       if( transformationGroup || operations ){
          context.g.dispose()
          context.g = g
       }
    }

    protected boolean executeBeforeNestedOperations( GraphicsContext context ) {
        return withinClipBounds( context )
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
        // go.execute( context )
    }

    protected void executeOperation( GraphicsContext context ) {
        if( !asShape ){
            fill( context )
            draw( context )
        }
    }

    protected void fill( GraphicsContext context ) {
       def g = context.g

       // short-circuit
       // don't fill the shape if fill == false
       if( fill instanceof Boolean && !fill ){
           return
       }

       // honor the clip
       if( !withinClipBounds( context ) ) {
           return
       }

       if( fill ){
          if( fill instanceof Color ){
              def previousValue = g.color
              g.color = fill
              applyFill( context )
              g.color = previousValue
          }else if( fill instanceof Paint ){
              def previousValue = g.paint
              g.paint = fill
              applyFill( context )
              g.paint = previousValue
          }else if( fill instanceof String ){
              def previousValue = g.color
              g.color = ColorCache.getInstance().getColor( fill )
              applyFill( context )
              g.color = previousValue
          }else if( fill instanceof PaintProvider ){
              Paint paint = context.g.getPaint()
              context.g.setPaint( fill.getPaint(context, getActualShape(context).bounds2D) )
              applyFill( context )
              context.g.setPaint( paint )
          }else {
             // look for a nested paintProvider
             def pp = operations.reverse().find{ it instanceof PaintProvider }
             if( pp ){
                Paint paint = context.g.getPaint()
                context.g.setPaint( pp.getPaint(context, getActualShape(context).bounds2D) )
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
             context.g.setPaint( pp.getPaint(context, getActualShape(context).bounds2D) )
             applyFill( context )
             context.g.setPaint( paint )
          }
       }
    }

    protected void applyFill( GraphicsContext context ) {
        context.g.fill( getShape(context) )
    }

    protected void draw( GraphicsContext context ) {
       def previousColor = null
       def previousStroke = null

       def g = context.g

       // short-circuit
       // don't draw the shape if borderColor == false
       if( borderColor instanceof Boolean && !borderColor ){
           return
       }

       // honor the clip
       if( !withinClipBounds( context ) ) {
           return
       }

       // apply color & stroke
       if( borderColor ){
           previousColor = g.color
           if( borderColor instanceof String ){
               g.color = ColorCache.getInstance().getColor( borderColor )
           }else if( borderColor instanceof Color ){
               g.color = borderColor
           }
       }
       if( borderWidth ){
           previousStroke = g.stroke
           g.stroke = new BasicStroke( borderWidth )
       }

       // draw the shape
       g.draw( getShape(context) )

       // restore color & stroke
       if( previousColor ) g.color = previousColor
       if( previousStroke ) g.stroke = previousStroke
    }

    protected boolean withinClipBounds( GraphicsContext context ) {
       if( transformedShape ){
          return transformedShape.intersects(context.g.clipBounds)
       }else{
          return getShape(context).intersects(context.g.clipBounds)
       }
    }

    protected Shape getActualShape( GraphicsContext context ){
       if( transformedShape ) return transformedShape
       return getShape(context)
    }
}