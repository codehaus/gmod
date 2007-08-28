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

import groovy.swing.j2d.GraphicsOperation;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ContextualGraphicsOperation extends DelegatingGraphicsOperation {
    private GraphicsContext context;
    private List operations = new ArrayList();

    public ContextualGraphicsOperation( GraphicsOperation delegate ) {
        super( delegate );
        this.context = new GraphicsContext();
    }

    public void addOperation( GraphicsOperation go ) {
        operations.add( go );
    }

    public void verify() {
        super.verify();
        for( Iterator i = operations.iterator(); i.hasNext(); ){
            GraphicsOperation go = (GraphicsOperation) i.next();
            go.verify();
        }
    }

    protected void afterDelegateExecutes( Graphics2D g, ImageObserver observer ) {
        if( operations.size() > 0 ){
            restoreContext( g, observer );
        }
    }

    protected void beforeDelegateExecutes( Graphics2D g, ImageObserver observer ) {
        if( operations.size() > 0 ){
            saveContext( g, observer );
            for( Iterator i = operations.iterator(); i.hasNext(); ){
                GraphicsOperation go = (GraphicsOperation) i.next();
                executeChildOperation( g, observer, go );
            }
            restoreClip( g, observer );
        }
    }

    protected void executeChildOperation( Graphics2D g, ImageObserver observer, GraphicsOperation go ) {
        go.execute( g, observer );
    }

    protected List getOperations() {
        return Collections.unmodifiableList( operations );
    }

    private void restoreClip( Graphics2D g, ImageObserver observer ) {
        Shape clip = context.getClip();
        if( clip != null ){
            g.translate( context.getX() * -1, context.getY() * -1 );
            g.setClip( clip );
        }
    }

    private void restoreContext( Graphics2D g, ImageObserver observer ) {
        if( context.getBackground() != null ){
            g.setBackground( context.getBackground() );
        }
        if( context.getColor() != null ){
            g.setColor( context.getColor() );
        }
        if( context.getFont() != null ){
            g.setFont( context.getFont() );
        }
        if( context.getPaint() != null ){
            g.setPaint( context.getPaint() );
        }
        if( context.getStroke() != null ){
            g.setStroke( context.getStroke() );
        }
    }

    private void saveContext( Graphics2D g, ImageObserver observer ) {
        context.setBackground( g.getBackground() );
        context.setColor( g.getColor() );
        context.setFont( g.getFont() );
        context.setPaint( g.getPaint() );
        context.setStroke( g.getStroke() );
        context.setClip( g.getClip() );
        Shape newclip = getClip( g, observer );
        if( newclip != null ){
            g.setClip( newclip );
            Rectangle bounds = newclip.getBounds();
            context.setX( bounds.getX() );
            context.setY( bounds.getY() );
            g.translate( bounds.getX(), bounds.getY() );
        }
    }
}