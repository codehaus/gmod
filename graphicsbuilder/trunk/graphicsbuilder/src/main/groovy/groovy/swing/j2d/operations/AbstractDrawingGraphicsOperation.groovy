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
import groovy.swing.j2d.operations.Filterable
import groovy.swing.j2d.operations.FilterProvider
import groovy.swing.j2d.operations.FilterGroup
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Paint
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Transparency
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractDrawingGraphicsOperation extends AbstractNestingGraphicsOperation implements Transformable, Filterable {
    public static required = []
    public static optional = ['borderColor','borderPaint','borderWidth','fill','asShape','opacity','composite','asImage']

    private def gcopy
    private def strokeBounds
    private def shapeBounds
    private BufferedImage image
    protected Shape locallyTransformedShape
    protected Shape globallyTransformedShape

    TransformationGroup transformationGroup
    TransformationGroup globalTransformationGroup
    FilterGroup filterGroup

    // properties
    def borderColor
    def borderPaint
    def borderWidth
    def fill
    def asShape
    def asImage
    def opacity
    def composite

    AbstractDrawingGraphicsOperation( String name ) {
        super( name )
    }

    public abstract Shape getShape( GraphicsContext context )
    
    public BufferedImage getImage() {
    	return image
    }

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

    public FilterGroup getFilterGroup() {
       filterGroup
    }
    
    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == transformationGroup ||
           event.source == globalTransformationGroup || 
           event.source == filterGroup ){
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

    /*
    public BufferedImage asImage( GraphicsContext context ) {
       if( !image ){
          image = calculateImage(context)
       }
       image
    }
    */

    protected void executeBeforeAll( GraphicsContext context ) {
       gcopy = context.g
       
       Shape shape = getGloballyTransformedShape(context)
       // honor the clip
       if( !withinClipBounds( context, shape ) ) {
           return
       }
          
       if( asImage || hasFilterGroup() || composite ){
           def stroke = getStroke(context)
           Shape strokedShape = stroke.createStrokedShape(shape)
           strokeBounds = strokedShape.bounds
           shapeBounds = shape.bounds
           
           int filterOffset = hasFilterGroup() ? filterGroup.offset : 0
           int swidth = strokeBounds.width + (filterOffset*2)
           int sheight = strokeBounds.height + (filterOffset*2)
             
           /*
           shape = AffineTransform.getTranslateInstance(
              (shapeBounds.x * -1) + filterOffset,
              (shapeBounds.x * -1) + filterOffset
           ).createTransformedShape(shape)
           */
    	   
    	   image = gcopy.deviceConfiguration.createCompatibleImage(
    	              swidth, sheight, Transparency.BITMASK )
    	              
    	   def imageBounds = new Rectangle(0,0,swidth,sheight)
    	             	              
    	   def gi = image.createGraphics()
    	   gi.color = context.g.color
    	   gi.background = context.g.background
    	   gi.translate( filterOffset - strokeBounds.x, filterOffset - strokeBounds.y )
    	   gi.clip = strokeBounds
    	   context.g = gi
       }else{
    	   context.g = context.g.create()
       }
       
       //applyOpacity( context )
    }

    protected void executeAfterAll( GraphicsContext context ) {
    	boolean drawImage = false
    	def previousComposite = null
    	
        if( hasFilterGroup() ){
      	   image = filterGroup.apply( image, strokeBounds )
      	   drawImage = true
        }
    	if( composite ){
    		previousComposite = gcopy.composite
    		gcopy.composite = composite
    		drawImage = true
    	}    	
    	
        if( !asImage && drawImage ){
           int filterOffset = hasFilterGroup() ? filterGroup.offset : 0	
     	   gcopy.drawImage( image, 
     			            (strokeBounds.x - filterOffset) as int, 
     			            (strokeBounds.y - filterOffset) as int, 
     			            null )	   
        }
        
        if( composite ){
        	gcopy.composite = previousComposite
        }
        
        context.g.dispose()
        context.g = gcopy
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
        go.execute( context )
    }

    protected void executeOperation( GraphicsContext context ) {
        if( asImage ){
           image = calculateImage( context )
        }else if( !asShape ){
            def shape = getGloballyTransformedShape(context)
            fill( context, shape )
            draw( context, shape )
        }
    }

    protected void fill( GraphicsContext context, Shape shape ) {
       def g = context.g

       def f = getFill( context )

       // short-circuit
       // don't fill the shape if fill == false
       if( f instanceof Boolean && !f ){
           return
       }

       /*
       // honor the clip
       if( !withinClipBounds( context, shape ) ) {
           return
       }
       */

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
                def previousColor = null
                def bc = getBorderColor( context )
                if( bc ){
                   previousColor = g.color
                   if( bc instanceof String ){
                       g.color = ColorCache.getInstance().getColor( bc )
                   }else if( bc instanceof Color ){
                       g.color = bc
                   }
                }
                applyFill( context, shape )
                if( previousColor ) g.color = previousColor
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

    public def getStroke(){
       def stroke = null
       operations.each { o ->
          if( o instanceof StrokeProvider ) stroke = o
       }
       return stroke
    }

    protected void applyFill( GraphicsContext context, Shape shape ) {
        context.g.fill( shape )
    }

    protected void draw( GraphicsContext context, Shape shape ) {
       def previousColor = null
       def previousStroke = null

       def g = context.g

       def bc = getBorderColor( context )
       def bw = getBorderWidth( context )
       def bp = getBorderPaint()

       // short-circuit
       // don't draw the shape if borderColor == false
       if( bc instanceof Boolean && !bc && !bp ){
           return
       }

       /*
       // honor the clip
       if( !withinClipBounds( context, shape ) ) {
           return
       }
       */

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
           }else{
              g.stroke = new BasicStroke( bw as float )
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
       return context.g.clipBounds ? shape.intersects( context.g.clipBounds ) : false
    }

    protected void calculateLocallyTransformedShape( GraphicsContext context ) {
       if( transformationGroup && !transformationGroup.empty){
          this.@locallyTransformedShape = transformationGroup.apply( getShape(context) )
       }else{
          this.@locallyTransformedShape = getShape(context)
       }
    }

    protected void calculateGloballyTransformedShape( GraphicsContext context ) {
       if( globalTransformationGroup && !globalTransformationGroup.empty ){
          this.@globallyTransformedShape = globalTransformationGroup.apply( getLocallyTransformedShape(context) )
       }else{
          this.@globallyTransformedShape = getLocallyTransformedShape(context)
       }
    }

    /*
    protected def calculateImage( GraphicsContext context ) {
       Shape shape = getLocallyTransformedShape(context)
       def bounds = shape.bounds
       BufferedImage image = context.g.deviceConfiguration.createCompatibleImage(
             bounds.width as int, bounds.height as int, Transparency.BITMASK )
       shape = AffineTransform.getTranslateInstance( (bounds.x as double)*(-1), (bounds.y as double)*(-1) )
                              .createTransformedShape(shape)
       def graphics = image.createGraphics()
       def contextCopy = context.copy()
       graphics.setClip( shape.bounds )
       graphics.renderingHints = context.g.renderingHints
       //graphics.color = context.g.color
       //if( borderColor != null && !(borderColor instanceof Boolean) ){
       //   graphics.color = ColorCache.getInstance().getColor(borderColor)
       //}

       contextCopy.g = graphics
       applyOpacity( contextCopy )

       if( operations ){
          operations.each { op -> executeNestedOperation(contextCopy,op) }
       }

       fill( contextCopy, shape )
       draw( contextCopy, shape )
       graphics.dispose()

       return image
    }
    */

    protected def getFill( GraphicsContext context ){
       def f = fill
       if( context.groupContext.fill != null ){
          f = context.groupContext.fill
       }
       if( fill != null ){
          f = fill
       }
       return f
    }

    protected def getBorderColor( GraphicsContext context ){
       def bc = borderColor
       if( context.groupContext.borderColor != null ){
          bc = context.groupContext.borderColor
       }
       if( borderColor != null ){
          bc = borderColor
       }
       return bc
    }

    protected def getBorderWidth( GraphicsContext context ){
       def bw = borderWidth
       if( context.groupContext.borderWidth ){
          bw = context.groupContext.borderWidth
       }
       if( borderWidth != null ){
          bw = borderWidth
       }
       return bw
    }

    protected def getStroke( GraphicsContext context ){
       def s = getStroke()
       def bw = getBorderWidth(context)
       if( bw ){
          def ps = context.g.stroke
          if( ps instanceof BasicStroke ){
             return ps.derive(width:bw)
          }else{
             return new BasicStroke( bw as float )
          }
       }else if( s ){
          return s.stroke
       }else{
          return context.g.stroke
       }
    }

    protected def getOpacity( GraphicsContext context ){
       /*
       def o = opacity
       if( context.groupContext?.opacity != null ){
          o = context.groupContext?.opacity
          // group already applied opacity settings
          return null
       }
       if( opacity != null ){
          o = opacity
       }
       */
       return opacity
    }

    protected void applyOpacity( GraphicsContext context ){
       def o = getOpacity( context )
       if( o != null ){
          context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, o as float)
       }/*else{
          context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
       }*/
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
    
    private boolean hasFilterGroup(){
    	return filterGroup && !filterGroup.empty
    }
}