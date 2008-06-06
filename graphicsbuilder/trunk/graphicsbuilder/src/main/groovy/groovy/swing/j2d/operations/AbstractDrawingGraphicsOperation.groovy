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
import groovy.swing.j2d.GraphicsBuilderHelper
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Paint
import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractDrawingGraphicsOperation extends AbstractNestingGraphicsOperation {
    public static required = []
    public static optional = AbstractNestingGraphicsOperation.optional + ['asShape']

    private def strokeBounds
    private def shapeBounds
    private BufferedImage image

    // properties
    def asShape

    AbstractDrawingGraphicsOperation( String name ) {
        super( name )
    }

    //public abstract Shape getShape( GraphicsContext context )
    
    public BufferedImage getImage() {
    	return image
    }

    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == filters ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    public void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       this.@image = null
    }

    protected void executeBeforeAll( GraphicsContext context ) {
       if( shouldSkip(context) ) return

       gcopy = context.g

       def boundingShape = runtime.boundingShape
       if( asImage || hasFilters() || composite ){
           strokeBounds = boundingShape.bounds
           shapeBounds = runtime.globallyTransformedShape.bounds
           
           int filterOffset = hasFilters() ? filters.offset : 0
           int swidth = strokeBounds.width + (filterOffset*2)
           int sheight = strokeBounds.height + (filterOffset*2)
             
    	   //image = gcopy.deviceConfiguration.createCompatibleImage(
    	   //           swidth, sheight, Transparency.BITMASK )
    	   image = GraphicsBuilderHelper.createCompatibleImage( swidth, sheight )
    	              
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
       
       applyOpacity( context )
    }

    protected void executeAfterAll( GraphicsContext context ) {
        if( shouldSkip(context) ) return

    	boolean drawImage = false
    	def previousComposite = null
    	
        if( hasFilters() ){
      	   image = filters.apply( image, strokeBounds )
      	   drawImage = true
        }
    	if( composite ){
    	   previousComposite = gcopy.composite
    	   gcopy.composite = composite
    	   drawImage = true
    	}    	
    	
        if( !asImage && drawImage ){
           int filterOffset = hasFilters() ? filters.offset : 0	
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
        
        addAsEventTarget(context)
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
        //if( shouldSkip(context) ) return
        //go.execute( context )
    }

    protected void executeOperation( GraphicsContext context ) {
        if( shouldSkip(context) ) return

        def shape = runtime.globallyTransformedShape
        context.bounds = shape?.bounds
        fill( context, shape )
        draw( context, shape )
    }

    protected void fill( GraphicsContext context, Shape shape ){
       def f = runtime.fill
       def g = context.g
       
       switch( f ){
          case Color:
             def color = g.color
             g.color = f
             applyFill( context, shape )
             g.color = color
             break
          case Paint:
             def paint = g.paint
             g.paint = f
             applyFill( context, shape )
             g.paint = paint
             break
          case MultiPaintProvider:
             f.apply( context, shape )
             break
          default:
             // no fill
             break
       }
    }

    protected void applyFill( GraphicsContext context, Shape shape ) {
        context.g.fill( shape )
    }

    protected void draw( GraphicsContext context, Shape shape ) {
       def g = context.g
       def bc = runtime.borderColor
       def st = runtime.stroke
       def bp = findLast { it instanceof BorderPaintProvider }
       
       if( bp && bp.paint ){
          def ss = st.createStrokedShape(shape)
          if( bp.paint instanceof MultiPaintProvider ){
             bp.apply(context,ss)
          }else{
             def p = g.paint
             g.paint = bp.getPaint(context,ss.bounds2D)
             g.fill( ss )
             g.paint = p
          }
       }else if( bc ){
          def pc = g.color
          def ps = g.stroke
          g.color = bc
          if( st ) g.stroke = st
          g.draw( shape )
          g.color = pc
          if( st ) g.stroke = ps
       }else{
          // don't draw the shape
       }
    }

    protected GraphicsRuntime createRuntime( GraphicsContext context ){
       return new DrawingGraphicsRuntime(this,context)
    }
    
    protected boolean withinClipBounds( GraphicsContext context, Shape shape ) {
       return context.g.clipBounds ? shape.intersects( context.g.clipBounds ) : false
    }

    protected void addAsEventTarget( GraphicsContext context ){
       if( !asShape && !asImage ){
          super.addAsEventTarget(context)
       }
    }

    private boolean shouldSkip( GraphicsContext context ){
       Shape shape = runtime.globallyTransformedShape
       // honor the clip
       if( asShape || !withinClipBounds( context, shape ) ){
           return true
       }
       return false
    }
}
