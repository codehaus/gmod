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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.awt.AlphaComposite
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
    public static required = []
    public static optional = ['borderColor','borderPaint','borderWidth','fill','asShape','opacity','alphaComposite']

    private BufferedImage image
    protected Shape locallyTransformedShape
    protected Shape globallyTransformedShape
    private def gcopy
    TransformationGroup transformationGroup
    TransformationGroup globalTransformationGroup

    // properties
    def borderColor
    def borderPaint
    def borderWidth
    def fill
    def asShape
    def asImage
    def opacity
    def alphaComposite

    AbstractDrawingGraphicsOperation( String name ) {
        super( name )
    }

    public abstract Shape getShape( GraphicsContext context )

    public Shape getLocallyTransformedShape( GraphicsContext context ){
       if( !this.@locallyTransformedShape ){
          calculateLocallyTransformedShape( context )
       }
       return this.@locallyTransformedShape
    }

    public Shape getGloballyTransformedShape( GraphicsContext context ){
       if( !this.@globallyTransformedShape ){
          calculateGloballyTransformedShape( context )
       }
       return this.@globallyTransformedShape
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
           event.source == globalTransformationGroup  ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    public void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       this.@locallyTransformedShape = null
       this.@globallyTransformedShape = null
       image = null
    }

    public BufferedImage asImage( GraphicsContext context ) {
       if( !image ){
          calculateImage(context)
       }
       image
    }

    protected void executeBeforeAll( GraphicsContext context ) {
       def o = opacity
       if( context.groupContext?.opacity ){
          o = context.groupContext?.opacity
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
    }

    protected void executeAfterAll( GraphicsContext context ) {
       //if( operations ){
          context.g.dispose()
          context.g = gcopy
       //}
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
        go.execute( context )
    }

    protected void executeOperation( GraphicsContext context ) {
        if( !asShape && !asImage ){
            def shape = getGloballyTransformedShape(context)
            /*
            if( alphaComposite ){
               context.g.composite = alphaComposite
            }
            */
            fill( context, shape )
            draw( context, shape )
        }
    }

    protected void fill( GraphicsContext context, Shape shape ) {
       def g = context.g

       def f = fill
       if( context.groupContext.fill != null ){
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
             def pp = getPaint()
             if( pp ){
                applyPaint( context, shape, pp )
             }else{
                // use current settings on context
                applyFill( context, shape )
             }
          }
       }else{
          // look for a nested paintProvider
          //def pp = operations.reverse().find{ it instanceof PaintProvider }
          def pp = getPaint()
          if( pp ){
             applyPaint( context, shape, pp )
          }
       }
    }

    public def getPaint(){
       def paint = null
       operations.each { o ->
          if( o instanceof PaintProvider || o instanceof MultiPaintProvider ) paint = o
       }
       return paint
    }

    public def getBorderPaint(){
       def paint = null
       operations.each { o ->
          if( o instanceof BorderPaintProvider ) paint = o
       }
       return paint
    }

    private void applyPaint( GraphicsContext context, Shape shape, paint ){
       if( paint instanceof PaintProvider ){
          Paint oldpaint = context.g.getPaint()
          context.g.paint = paint.getPaint(context, shape.bounds2D)
          applyFill( context, shape )
          context.g.paint = oldpaint
       }else if( paint instanceof MultiPaintProvider ){
          paint.apply( context, shape )
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
       if( context.groupContext.borderColor != null ){
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

       def bp = getBorderPaint()

       // short-circuit
       // don't draw the shape if borderColor == false
       if( bc instanceof Boolean && !bc && !bp ){
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
       if( bw != null ){
           previousStroke = g.stroke
           if( previousStroke instanceof BasicStroke ){
              g.stroke = previousStroke.derive(width:bw)
           }
       }

       if( bp && bp.paint ){
          def ss = g.stroke.createStrokedShape(shape)
          if( bp.paint instanceof MultiPaintProvider ){
             bp.paint.apply(context,ss)
          }else{
             def p = g.paint
             g.paint = bp.getPaint(context,ss.bounds2D)
             g.fill( ss )
             g.paint = p
          }
       }else{
          // draw the shape
          g.draw( shape )
       }

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
       graphics.setClip( shape.bounds )
       graphics.renderingHints = context.g.renderingHints
       /*
       graphics.color = context.g.color
       if( borderColor != null && !(borderColor instanceof Boolean) ){
          graphics.color = ColorCache.getInstance().getColor(borderColor)
       }
       */

       contextCopy.g = graphics

       def o = opacity
       if( contextCopy.groupContext?.opacity ){
          o = contextCopy.groupContext?.opacity
       }
       if( opacity != null ){
          o = opacity
       }

       if( o != null ){
          graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, o as float)
       }else{
          graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
       }

       if( operations ){
          operations.each { op -> executeNestedOperation(contextCopy,op) }
       }

       fill( contextCopy, shape )
       draw( contextCopy, shape )
       graphics.dispose()
    }
}