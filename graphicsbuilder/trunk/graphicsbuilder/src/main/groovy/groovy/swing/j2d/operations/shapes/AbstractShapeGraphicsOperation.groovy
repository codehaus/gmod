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
import groovy.swing.j2d.operations.AbstractDrawingGraphicsOperation

import java.awt.Shape
import java.awt.geom.Area

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractShapeGraphicsOperation extends AbstractDrawingGraphicsOperation implements
   ShapeProvider, GraphicsInputListener {
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
    
    protected void executeAfterAll( GraphicsContext context ) {
       if( !asShape ){
           context.shapes << this
       }
       super.executeAfterAll(context)
    }
}