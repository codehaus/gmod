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
    public static optional = ['opacity','composite','asImage','passThrough']

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
    
    public boolean hasCenter() {
       false
    }
    
    public boolean hasXY() {
       false
    }

    public abstract Shape getBoundingShape( GraphicsContext context )
    
    public abstract Rectangle getBounds()

    protected void applyOpacity( GraphicsContext context ){
       if( opacity != null ){
          context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, o as float)
       }
    }
    
    protected boolean hasfilters(){
    	return filters && !filters.empty
    }

    protected void addAsEventTarget( GraphicsContext context ){
        if( keyPressed || keyReleased || keyTyped || mouseClicked ||
            mouseDragged || mouseEntered || mouseExited ||
            mouseMoved || mousePressed || mouseReleased ||
            mouseWheelMoved ){
           context.eventTargets << this
        }
    }
}
