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
import java.awt.Transparency
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractDrawingGraphicsOperation extends AbstractNestingGraphicsOperation implements Transformable {
    protected static optional = ['borderColor','borderWidth','fill','asShape']

    private BufferedImage image
    protected Shape locallyTransformedShape
    protected Shape globallyTransformedShape
    private def gcopy
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
       image = null
    }

    public BufferedImage asImage( GraphicsContext context ) {
       if( !image ){
          calculateImage(context)
       }
       image
    }

    protected void executeBeforeAll( GraphicsContext context ) {
       if( operations ){
          gcopy = context.g
          context.g = context.g.create()
       }
    }

    protected void executeAfterAll( GraphicsContext context ) {
       if( operations ){
          context.g.dispose()
          context.g = gcopy
       }
    }

    /*
    protected boolean executeAfterNestedOperations( GraphicsContext context ) {
       return withinClipBounds( context )
    }
    */

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
        go.execute( context )
    }

    protected void executeOperation( GraphicsContext context ) {
        if( !asShape ){
            def shape = getGloballyTransformedShape(context)
            fill( context, shape )
            draw( context, shape )
        }
    }

    protected void fill( GraphicsContext context, Shape shape ) {
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
       if( !withinClipBounds( context, shape ) ) {
           return
       }

       if( f ){
          if( f instanceof Color ){
              def previousValue = g.color
              g.color = f
              applyFill( context, shape )
              g.color = previousValue
          }else if( f instanceof Paint ){
              def previousValue = g.paint
              g.paint = f
              applyFill( context, shape )
              g.paint = previousValue
          }else if( f instanceof String ){
              def previousValue = g.color
              g.color = ColorCache.getInstance().getColor( f )
              applyFill( context, shape )
              g.color = previousValue
          }else if( f instanceof PaintProvider ){
              Paint paint = g.getPaint()
              g.setPaint( f.getPaint(context, shape.bounds2D) )
              applyFill( context, shape )
              g.setPaint( paint )
          }else {
             // look for a nested paintProvider
             def pp = operations.reverse().find{ it instanceof PaintProvider }
             if( pp ){
                Paint paint = g.getPaint()
                g.setPaint( pp.getPaint(context, shape.bounds2D) )
                applyFill( context, shape )
                g.setPaint( paint )
             }else{
                // use current settings on context
                applyFill( context, shape )
             }
          }
       }else{
          // look for a nested paintProvider
          def pp = operations.reverse().find{ it instanceof PaintProvider }
          if( pp ){
             Paint paint = g.getPaint()
             g.setPaint( pp.getPaint(context, shape.bounds2D) )
             applyFill( context, shape )
             g.setPaint( paint )
          }
       }
    }

    protected void applyFill( GraphicsContext context, Shape shape ) {
        context.g.fill( shape )
    }

    protected void draw( GraphicsContext context, Shape shape ) {
       def previousColor = null
       def previousStroke = null

       def g = context.g

       def bc = borderColor
       if( context.groupContext.borderColor ){
          bc = context.groupContext.borderColor
       }
       if( borderColor != null ){
          bc = borderColor
       }
       def bw = borderWidth
       if( context.groupContext.borderWidth ){
          bw = context.groupContext.borderWidth
       }
       if( borderWidth != null ){
          bw = borderWidth
       }

       // short-circuit
       // don't draw the shape if borderColor == false
       if( bc instanceof Boolean && !bc ){
           return
       }

       // honor the clip
       if( !withinClipBounds( context, shape ) ) {
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
       g.draw( shape )

       // restore color & stroke
       if( previousColor ) g.color = previousColor
       if( previousStroke ) g.stroke = previousStroke
    }

    protected boolean withinClipBounds( GraphicsContext context, Shape shape ) {
       return shape.intersects( context.g.clipBounds )
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

    private void calculateImage( GraphicsContext context ) {
       Shape shape = getLocallyTransformedShape(context)
       def bounds = shape.bounds
       image = context.g.deviceConfiguration.createCompatibleImage(
             bounds.width as int, bounds.height as int, Transparency.BITMASK )
       shape = AffineTransform.getTranslateInstance( (bounds.x as double)*(-1), (bounds.y as double)*(-1) )
                              .createTransformedShape(shape)
       def graphics = image.createGraphics()
       def contextCopy = context.copy()
       contextCopy.g = graphics
       graphics.setClip( shape.bounds )
       graphics.color = context.g.color
       if( borderColor != null && !(borderColor instanceof Boolean) ){
          graphics.color = ColorCache.getInstance().getColor(borderColor)
       }
       fill( contextCopy, shape )
       draw( contextCopy, shape )
       graphics.dispose()
    }
}