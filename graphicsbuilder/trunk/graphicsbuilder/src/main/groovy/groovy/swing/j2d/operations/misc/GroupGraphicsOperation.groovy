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
import groovy.swing.j2d.event.GraphicsInputEvent
import groovy.swing.j2d.event.GraphicsInputListener
import groovy.swing.j2d.operations.Grouping
import groovy.swing.j2d.operations.PaintProvider
import groovy.swing.j2d.operations.Transformable
import groovy.swing.j2d.operations.TransformationGroup
import groovy.swing.j2d.operations.AbstractNestingGraphicsOperation
import groovy.swing.j2d.operations.ViewBox
import groovy.swing.j2d.operations.Filterable
import groovy.swing.j2d.operations.FilterProvider
import groovy.swing.j2d.operations.FilterGroup
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.awt.AlphaComposite
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GroupGraphicsOperation extends AbstractNestingGraphicsOperation implements Transformable, Grouping, Filterable, GraphicsInputListener {
    public static optional = ['borderColor','borderWidth','fill','opacity','asImage','composite','passThrough']

    private def previousGroupContext
    private def gcopy
    private BufferedImage image
    
    TransformationGroup transformations
    TransformationGroup globalTransformations
    FilterGroup filters
    ViewBox viewBox

    Closure keyPressed
    Closure keyReleased
    Closure keyTyped
    Closure mouseClicked
    Closure mouseDragged
    Closure mouseEntered
    Closure mouseExited
    Closure mouseMoved
    Closure mousePressed
    Closure mouseReleased
    Closure mouseWheelMoved

    // properties
    def borderColor
    def borderWidth
    def fill
    def opacity
    def asImage
    def composite
    def passThrough

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

    public void setTransformations( TransformationGroup transformations ){
       if( transformations ) {
          if( this.transformations ){
             this.transformations.removePropertyChangeListener( this )
          }
          this.transformations = transformations
          this.transformations.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getTransformations() {
       transformations
    }
    
    public TransformationGroup getTxs() {
       transformations
    }

    public void setGlobalTransformations( TransformationGroup globalTransformations ){
       if( globalTransformations ) {
          if( this.globalTransformations ){
             this.globalTransformations.removePropertyChangeListener( this )
          }
          this.globalTransformations = globalTransformations
          this.globalTransformations.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getGlobalTransformations() {
       globalTransformations
    }

    public void setFilters( FilterGroup filters ){
       if( filters ) {
          if( this.filters ){
             this.filters.removePropertyChangeListener( this )
          }
          this.filters = filters
          this.filters.addPropertyChangeListener( this )
       }
    }
 
    public FilterGroup getFilters() {
       filters
    }

    public void keyPressed( GraphicsInputEvent e ) {
       if( keyPressed ) this.@keyPressed(e)
    }

    public void keyReleased( GraphicsInputEvent e ) {
       if( keyReleased ) this.@keyReleased(e)
    }

    public void keyTyped( GraphicsInputEvent e ) {
       if( keyTyped ) this.@keyTyped(e)
    }

    public void mouseClicked( GraphicsInputEvent e ) {
       if( mouseClicked ) this.@mouseClicked(e)
    }

    public void mouseDragged( GraphicsInputEvent e ) {
       if( mouseDragged ) this.@mouseDragged(e)
    }

    public void mouseEntered( GraphicsInputEvent e ) {
       if( mouseEntered ) this.@mouseEntered(e)
    }

    public void mouseExited( GraphicsInputEvent e ) {
       if( mouseExited ) this.@mouseExited(e)
    }

    public void mouseMoved( GraphicsInputEvent e ) {
       if( mouseMoved ) this.@mouseMoved(e)
    }

    public void mousePressed( GraphicsInputEvent e ) {
       if( mousePressed ) this.@mousePressed(e)
    }

    public void mouseReleased( GraphicsInputEvent e ) {
       if( mouseReleased ) this.@mouseReleased(e)
    }

    public void mouseWheelMoved( GraphicsInputEvent e ) {
       if( mouseWheelMoved ) this.@mouseWheelMoved(e)
    }
    
    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == transformations ||
           event.source == globalTransformations ||
           event.source == filters ||
           event.source == viewBox ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }
    
    protected void localPropertyChange( PropertyChangeEvent event ) {
        super.localPropertyChange( event )
        image = null
     }

    protected void executeBeforeAll( GraphicsContext context ) {
       previousGroupContext = [:]
       previousGroupContext.putAll(context.groupContext)
       
       gcopy = context.g
       
       def boundingShape = getBoundingShape(context)
       if( asImage || hasfilters() || composite ){
          def filterOffset = hasfilters() ? filters.offset : 0
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
       def bounds = context.g.clipBounds
       def filterOffset = hasfilters() ? filters.offset : 0
       if( hasfilters() ){
           image = filters.apply( image, bounds )   
       }
       if( !asImage || composite ){
          gcopy.drawImage( image, 
                           (bounds.x - filterOffset) as int, 
                           (bounds.y - filterOffset) as int, 
                           null )      
       }
       
       context.g.dispose()
       context.g = gcopy
       context.groupContext = previousGroupContext

       addAsEventTarget(context)
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

    protected def getOpacity( GraphicsContext context ){
       /*def o = opacity
       if( context.groupContext?.opacity ){
          o = context.groupContext?.opacity
       }
       if( opacity != null ){
          o = opacity
       }*/
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
    
    private boolean hasfilters(){
       return filters && !filters.empty
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

    private void addAsEventTarget( GraphicsContext context ){
        if( viewBox && (keyPressed ||
            keyReleased || keyTyped || mouseClicked ||
            mouseDragged || mouseEntered || mouseExited ||
            mouseMoved || mousePressed || mouseReleased ||
            mouseWheelMoved) ){
           context.eventTargets << this
        }
    }
}
