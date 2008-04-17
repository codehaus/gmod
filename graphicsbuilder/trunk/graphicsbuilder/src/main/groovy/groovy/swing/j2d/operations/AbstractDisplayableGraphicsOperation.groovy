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
import groovy.swing.j2d.event.GraphicsInputEvent
import groovy.swing.j2d.event.GraphicsInputListener
import groovy.swing.j2d.operations.Filterable
import groovy.swing.j2d.operations.FilterProvider
import groovy.swing.j2d.operations.FilterGroup
import groovy.swing.j2d.operations.transformations.TranslateTransformation
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Paint
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Transparency
import java.awt.geom.Area
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractDisplayableGraphicsOperation extends AbstractGraphicsOperation implements Transformable, Filterable, GraphicsInputListener {
    public static optional = ['opacity','composite','asImage','passThrough','autoDrag']

    TransformationGroup transformations
    TransformationGroup globalTransformations
    FilterGroup filters

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

    def asImage = false
    def opacity
    def composite
    def passThrough = false
    def autoDrag = false
    
    private drag = new ObservableMap()

    AbstractDisplayableGraphicsOperation( String name ) {
        super( name )
        setTransformations( new TransformationGroup() )
        setGlobalTransformations( new TransformationGroup() )
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
    
    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == transformations ||
           event.source == globalTransformations || 
           event.source == filters ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }
    
    public Map getDrag(){
    	Collections.unmodifiableMap(drag)
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
       if( autoDrag ) this.trackDrag(e)
       if( mouseDragged ) this.@mouseDragged(e)
    }

    public void mouseEntered( GraphicsInputEvent e ) {
       if( mouseEntered ) this.@mouseEntered(e)
    }

    public void mouseExited( GraphicsInputEvent e ) {
       if( autoDrag ) this.endDrag(e)
       if( mouseExited ) this.@mouseExited(e)
    }

    public void mouseMoved( GraphicsInputEvent e ) {
       if( mouseMoved ) this.@mouseMoved(e)
    }

    public void mousePressed( GraphicsInputEvent e ) {
       if( autoDrag ) this.startDrag(e)
       if( mousePressed ) this.@mousePressed(e)
    }

    public void mouseReleased( GraphicsInputEvent e ) {
       if( autoDrag ) this.endDrag(e)
       if( mouseReleased ) this.@mouseReleased(e)
    }

    public void mouseWheelMoved( GraphicsInputEvent e ) {
       if( mouseWheelMoved ) this.@mouseWheelMoved(e)
    }
    
    public boolean hasCenter() {
       false
    }
    
    public boolean hasXY() {
       false
    }
    
    public boolean hasFilters(){
       return filters && filters.enabled && !filters.empty
    }

    protected void applyOpacity( GraphicsContext context ){
       if( opacity != null ){
          context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity as float)
       }
    }

    protected void addAsEventTarget( GraphicsContext context ){
        if( keyPressed || keyReleased || keyTyped || mouseClicked ||
            mouseDragged || mouseEntered || mouseExited ||
            mouseMoved || mousePressed || mouseReleased ||
            mouseWheelMoved || autoDrag ){
           context.eventTargets << this
        }
    }
    
    private void startDrag( e ){
       def bounds = runtime.boundingShape.bounds
       def dragMap = this.@drag
       if( !dragMap.anchor ){ 
          dragMap.anchor = [x:bounds.x,y:bounds.y]
       }
       if( !dragMap.location ){
    	  dragMap.location = [x:0,y:0]
       }else{
    	  dragMap.location = [
             x: bounds.x - dragMap.anchor.x,
             y: bounds.y - dragMap.anchor.y
          ]
       }
       dragMap.dragPoint = [x:e.event.x,y:e.event.y]
    }
    
    private void endDrag( e ){
    	this.@drag.dragging = false
    }
    
    private void trackDrag( e ){
       def bounds = runtime.boundingShape.bounds
       def dragMap = this.@drag
       def location = dragMap.location
       if( !dragMap.dragPoint ) return
       def dx = e.event.x - dragMap.dragPoint.x
       def dy = e.event.y - dragMap.dragPoint.y

       if( !dragMap.dragging ){
          if( !txs['location'] ){
             txs << new TranslateTransformation( name: 'location',
                                  x: location.x, y: location.y )
          }else{
             txs['location'].x = location.x
             txs['location'].y = location.y
          }
          dragMap.dragging = true
       }

       if( !txs['drag'] ){
          txs << new TranslateTransformation( name: 'drag', x: dx, y: dy )
       }else{
          txs['drag'].x = dx
          txs['drag'].y = dy
       }    	
    }
}
