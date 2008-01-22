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

package groovy.swing.j2d.operations.shapes

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.event.GraphicsInputEvent
import groovy.swing.j2d.event.GraphicsInputListener
import groovy.swing.j2d.operations.ShapeProvider
import groovy.swing.j2d.operations.Filterable
import groovy.swing.j2d.operations.FilterProvider
import groovy.swing.j2d.operations.FilterGroup
import groovy.swing.j2d.operations.AbstractDrawingGraphicsOperation
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.awt.AlphaComposite
import java.awt.Shape
import java.awt.Transparency
import java.awt.geom.Area
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractShapeGraphicsOperation extends AbstractDrawingGraphicsOperation implements
   ShapeProvider, GraphicsInputListener, Filterable {
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

    private BufferedImage filteredImage
    FilterGroup filterGroup

    public AbstractShapeGraphicsOperation( String name ) {
        super( name )
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
       if( event.source == filterGroup ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    protected void localPropertyChange( PropertyChangeEvent event ) {
       super.localPropertyChange( event )
       filteredImage = null
    }

    public void setFilterGroup( FilterGroup filterGroup ){
       if( filterGroup ) {
          if( this.filterGroup ){
             this.filterGroup.removePropertyChangeListener( this )
          }
          this.filterGroup = filterGroup
          this.filterGroup.addPropertyChangeListener( this )
       }
    }

    public FilterGroup getFilterGroup() {
       filterGroup
    }

    /* ===== OPERATOR OVERLOADING ===== */

    public Shape plus( ShapeProvider shape ){
       return plus( shape.getLocallyTransformedShape(null) )
    }
    public Shape plus( Shape shape ){
       def area = new Area(getLocallyTransformedShape(null))
       area.add( new Area(shape) )
       return area
    }

    public Shape minus( ShapeProvider shape ){
       return minus( shape.getLocallyTransformedShape(null) )
    }
    public Shape minus( Shape shape ){
       def area = new Area(getLocallyTransformedShape(null))
       area.subtract( new Area(shape) )
       return area
    }

    public Shape and( ShapeProvider shape ){
       return and( shape.getLocallyTransformedShape(null) )
    }
    public Shape and( Shape shape ){
       def area = new Area(getLocallyTransformedShape(null))
       area.intersect( new Area(shape) )
       return area
    }

    public Shape xor( ShapeProvider shape ){
       return xor( shape.getLocallyTransformedShape(null) )
    }
    public Shape xor( Shape shape ){
       def area = new Area(getLocallyTransformedShape(null))
       area.exclusiveOr( new Area(shape) )
       return area
    }

    /* ===== PROTECTED ===== */

    protected void executeOperation( GraphicsContext context ) {
       def hasPaint = getPaint()
       def hasFill = getFill( context )

       // normal execution if
       // - no filters
       // - filters but no fill nor paint
       // - marked asShape = true
       // TODO review asImage !!!
       if( !filterGroup || filterGroup.isEmpty() || asShape || asImage ||
           (!filterGroup.isEmpty() && !hasPaint && !hasFill) ){
          super.executeOperation( context )
          return
       }
       if( filteredImage == null ){
          calculateFilteredImage( context )
       }
       Shape shape = getGloballyTransformedShape(context)
       def stroke = getStroke(context)
       Shape strokedShape = stroke.createStrokedShape(shape)

       def bounds = strokedShape.bounds
       int sx = bounds.x - filterGroup.offset
       int sy = bounds.y - filterGroup.offset
       applyOpacity( context )
       context.g.drawImage( filteredImage, sx, sy, null )
    }

    protected void executeAfterAll( GraphicsContext context ) {
       if( !asShape ){
           context.shapes << this
       }
       super.executeAfterAll(context)
    }

    /* ===== PRIVATE ===== */

    private void calculateFilteredImage( GraphicsContext context ){
       Shape shape = getGloballyTransformedShape(context)
       def stroke = getStroke(context)
       Shape strokedShape = stroke.createStrokedShape(shape)

       def strokeBounds = strokedShape.bounds
       def shapeBounds = shape.bounds
       int swidth = strokeBounds.width + (filterGroup.offset*2)
       int sheight = strokeBounds.height + (filterGroup.offset*2)
       BufferedImage src = context.g.deviceConfiguration.createCompatibleImage(
             swidth, sheight, Transparency.BITMASK )
       shape = AffineTransform.getTranslateInstance(
          (shapeBounds.x * -1) + filterGroup.offset,
          (shapeBounds.x * -1) + filterGroup.offset
       ).createTransformedShape(shape)
       def graphics = src.createGraphics()
       def contextCopy = context.copy()
       graphics.setClip( 0, 0, swidth, sheight )
       //graphics.setClip( shape.bounds )
       graphics.renderingHints.putAll( context.g.renderingHints )
       contextCopy.g = graphics

       applyOpacity( contextCopy )

       if( operations ){
          operations.each { op -> executeNestedOperation(contextCopy,op) }
       }

       fill( contextCopy, shape )
       draw( contextCopy, shape )
       graphics.dispose()

       // apply filters
       filteredImage = filterGroup.apply( src, shape )
    }
}