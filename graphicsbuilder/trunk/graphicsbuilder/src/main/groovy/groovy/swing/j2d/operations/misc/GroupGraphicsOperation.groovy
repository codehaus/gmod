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
import groovy.swing.j2d.operations.ViewBox
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.awt.Rectangle
import java.awt.Shape
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GroupGraphicsOperation extends AbstractNestingGraphicsOperation implements Grouping {
    private def previousGroupContext
    private BufferedImage image
    private Rectangle bounds
    
    ViewBox viewBox

    public GroupGraphicsOperation() {
        super( "group" )
    }

    public void setViewBox( ViewBox viewBox ){
       if( viewBox ) {
          if( this.viewBox ){
             this.viewBox.removePropertyChangeListener( this )
          }
          this.viewBox = viewBox
          this.viewBox.addPropertyChangeListener( this )
       }
    }

    public ViewBox getViewBox() {
       viewBox
    }
    
    public BufferedImage getImage() {
       image
    }
    
    public Rectangle getBounds() {
       bounds
    }
    
    public boolean hasCenter() {
       false
    }
    
    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == viewBox ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }
    
    protected void localPropertyChange( PropertyChangeEvent event ) {
        super.localPropertyChange( event )
        image = null
        bounds = null
     }

    protected void executeBeforeAll( GraphicsContext context ) {
       previousGroupContext = [:]
       previousGroupContext.putAll(context.groupContext)
       
       gcopy = context.g
       
       def boundingShape = getBoundingShape(context)
       if( asImage || hasFilters() || composite ){
          def filterOffset = hasFilters() ? filters.offset : 0
          def bounds = boundingShape.bounds
          int swidth = bounds.width + (filterOffset * 2)
          int sheight = bounds.height + (filterOffset * 2)
          
          image = gcopy.deviceConfiguration.createCompatibleImage(
                     swidth, sheight, Transparency.BITMASK )
                     
          def gi = image.createGraphics()
          gi.color = context.g.color
          gi.background = context.g.background
          gi.translate( filterOffset - bounds.x, filterOffset - bounds.y )
          gi.clip = bounds 
          context.g = gi
       }else{
          context.g = context.g.create()
          if( viewBox ){
             context.g.setClip( boundingShape )
          }
       }

       applyOpacity( context )

       if( borderColor != null ) context.groupContext.borderColor = borderColor
       if( borderWidth != null ) context.groupContext.borderWidth = borderWidth
       if( opacity != null ) context.groupContext.opacity = opacity
       if( fill != null ) context.groupContext.fill = fill
    }

    protected void executeAfterAll( GraphicsContext context ) {
       boolean drawImage = false
       def previousComposite = null
    	
       def cbounds = context.g.clipBounds       
       if( hasFilters() ){
           image = filters.apply( image, cbounds )
           drawImage = true
       }
       if( composite ){
           previousComposite = gcopy.composite
           gcopy.composite = composite
           drawImage = true
       }   	
    	
       if( !asImage || drawImage ){
          def filterOffset = hasFilters() ? filters.offset : 0 
          gcopy.drawImage( image, 
                           (cbounds.x - filterOffset) as int, 
                           (cbounds.y - filterOffset) as int, 
                           null )      
       }
       
       if( composite ){
           gcopy.composite = previousComposite
       }
       
       context.g.dispose()
       context.g = gcopy
       context.groupContext = previousGroupContext

       addAsEventTarget(context)
       
       bounds = new Rectangle(getBoundingShape(context).bounds)
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
       if( go instanceof Transformable /*&& !(go instanceof PaintProvider)*/ ){
          if( transformations ){
             def gtg = go.globalTransformations
             if( !gtg ){
                gtg = new TransformationGroup()
                go.globalTransformations = gtg
             }
             gtg.addTransformation( transformations )
          }
          if( globalTransformations ){
             def gtg = go.globalTransformations
             if( !gtg ){
                gtg = new TransformationGroup()
                go.globalTransformations = gtg
             }
             gtg.addTransformation( globalTransformations )
          }
       }
       go.execute( context )
    }
      
    public Shape getBoundingShape( GraphicsContext context ) {
        def bounds = [0,0,0,0] as Rectangle
        if( viewBox ){
            bounds = viewBox.getLocallyTransformedShape(context)
            if( !viewBox.pinned ){
               if( transformations ) bounds = transformations.apply(bounds)
               if( globalTransformations ) bounds = globalTransformations.apply(bounds)
            }
        }else if( context.g.clipBounds ){
            bounds = new Rectangle(context.g.clipBounds)
        }else{
            bounds.width = context.component?.bounds?.width
            bounds.height = context.component?.bounds?.height         
        }
        return bounds
    }

    protected void addAsEventTarget( GraphicsContext context ){
        if( viewBox ){
           super.addAsEventTarget(context)
        }
    }
}
