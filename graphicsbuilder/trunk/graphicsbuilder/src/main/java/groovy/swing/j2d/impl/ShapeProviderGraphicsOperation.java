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

package groovy.swing.j2d.impl;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.swing.j2d.GraphicsContext;
import groovy.swing.j2d.GraphicsInputEvent;
import groovy.swing.j2d.GraphicsInputListener;
import groovy.swing.j2d.GraphicsOperation;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Decorator that adds 'asShape' and input event handling.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ShapeProviderGraphicsOperation extends DelegatingGraphicsOperation implements
        GraphicsInputListener {
    private boolean asShape;
    private Closure keyPressed;
    private Closure keyReleased;
    private Closure keyTyped;
    private Closure mouseClicked;
    private Closure mouseDragged;
    private Closure mouseEntered;
    private Closure mouseExited;
    private Closure mouseMoved;
    private Closure mousePressed;
    private Closure mouseReleased;
    private Closure mouseWheelMoved;

    public ShapeProviderGraphicsOperation( GraphicsOperation delegate ) {
        super( delegate );
        addParameter( "asShape", false );
        addParameter( "keyPressed", false );
        addParameter( "keyReleased", false );
        addParameter( "keyTyped", false );
        addParameter( "mouseClicked", false );
        addParameter( "mouseDragged", false );
        addParameter( "mouseEntered", false );
        addParameter( "mouseExited", false );
        addParameter( "mouseMoved", false );
        addParameter( "mousePressed", false );
        addParameter( "mouseReleased", false );
        addParameter( "mouseWheelMoved", false );
    }

    public Closure getKeyPressed() {
        return keyPressed;
    }

    public Closure getKeyReleased() {
        return keyReleased;
    }

    public Closure getKeyTyped() {
        return keyTyped;
    }

    public Closure getMouseClicked() {
        return mouseClicked;
    }

    public Closure getMouseDragged() {
        return mouseDragged;
    }

    public Closure getMouseEntered() {
        return mouseEntered;
    }

    public Closure getMouseExited() {
        return mouseExited;
    }

    public Closure getMouseMoved() {
        return mouseMoved;
    }

    public Closure getMousePressed() {
        return mousePressed;
    }

    public Closure getMouseReleased() {
        return mouseReleased;
    }

    public Closure getMouseWheelMoved() {
        return mouseWheelMoved;
    }

    public String[] getOptionalParameters() {
        String[] optionals = getDelegate().getOptionalParameters();
        String[] other = new String[optionals.length + 12];
        System.arraycopy( optionals, 0, other, 0, optionals.length );
        other[optionals.length] = "asShape";
        other[optionals.length + 1] = "keyPressed";
        other[optionals.length + 2] = "keyReleased";
        other[optionals.length + 3] = "keyTyped";
        other[optionals.length + 4] = "mouseClicked";
        other[optionals.length + 5] = "mouseDragged";
        other[optionals.length + 6] = "mouseEntered";
        other[optionals.length + 7] = "mouseExited";
        other[optionals.length + 8] = "mouseMoved";
        other[optionals.length + 9] = "mousePressed";
        other[optionals.length + 10] = "mouseReleased";
        other[optionals.length + 11] = "mouseWheelMoved";
        return other;
    }

    public boolean isAsShape() {
        return asShape;
    }

    public void keyPressed( GraphicsInputEvent e ) {
        if( keyPressed != null ){
            keyPressed.call( e );
        }
    }

    public void keyReleased( GraphicsInputEvent e ) {
        if( keyReleased != null ){
            keyReleased.call( e );
        }
    }

    public void keyTyped( GraphicsInputEvent e ) {
        if( keyTyped != null ){
            keyTyped.call( e );
        }
    }

    public void mouseClicked( GraphicsInputEvent e ) {
        if( mouseClicked != null ){
            mouseClicked.call( e );
        }
    }

    public void mouseDragged( GraphicsInputEvent e ) {
        if( mouseDragged != null ){
            mouseDragged.call( e );
        }
    }

    public void mouseEntered( GraphicsInputEvent e ) {
        if( mouseEntered != null ){
            mouseEntered.call( e );
        }
    }

    public void mouseExited( GraphicsInputEvent e ) {
        if( mouseExited != null ){
            mouseExited.call( e );
        }
    }

    public void mouseMoved( GraphicsInputEvent e ) {
        if( mouseMoved != null ){
            mouseMoved.call( e );
        }
    }

    public void mousePressed( GraphicsInputEvent e ) {
        if( mousePressed != null ){
            mousePressed.call( e );
        }
    }

    public void mouseReleased( GraphicsInputEvent e ) {
        if( mouseReleased != null ){
            mouseReleased.call( e );
        }
    }

    public void mouseWheelMoved( GraphicsInputEvent e ) {
        if( mouseWheelMoved != null ){
            mouseWheelMoved.call( e );
        }
    }

    public void setAsShape( boolean asShape ) {
        this.asShape = asShape;
    }

    public void setKeyPressed( Closure keyPressed ) {
        this.keyPressed = keyPressed;
    }

    public void setKeyReleased( Closure keyReleased ) {
        this.keyReleased = keyReleased;
    }

    public void setKeyTyped( Closure keyTyped ) {
        this.keyTyped = keyTyped;
    }

    public void setMouseClicked( Closure mouseClicked ) {
        this.mouseClicked = mouseClicked;
    }

    public void setMouseDragged( Closure mouseDragged ) {
        this.mouseDragged = mouseDragged;
    }

    public void setMouseEntered( Closure mouseEntered ) {
        this.mouseEntered = mouseEntered;
    }

    public void setMouseExited( Closure mouseExited ) {
        this.mouseExited = mouseExited;
    }

    public void setMouseMoved( Closure mouseMoved ) {
        this.mouseMoved = mouseMoved;
    }

    public void setMousePressed( Closure mousePressed ) {
        this.mousePressed = mousePressed;
    }

    public void setMouseReleased( Closure mouseReleased ) {
        this.mouseReleased = mouseReleased;
    }

    public void setMouseWheelMoved( Closure mouseWheelMoved ) {
        this.mouseWheelMoved = mouseWheelMoved;
    }

    protected void executeDelegate( GraphicsContext context ) {
        if( !asShape ){
            super.executeDelegate( context );
            /*
            context.addShape( this );
            Object target = context.getTarget();
            MetaClass targetMetaClass = InvokerHelper.getMetaClass( target );
            if( !targetMetaClass.respondsTo( target, "addGraphicsInputListener" )
                    .isEmpty() ){
                InvokerHelper.invokeMethod( target, "addGraphicsInputListener", this );
            }
            */
        }
    }
}