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

import java.awt.Component;
import java.awt.Graphics2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Decorator that adds a GraphicsContext to its delegate.<br>
 * A GraphicsContext takes care of restoring the state of the Graphics2D object
 * after this operation's delegate has been executed.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ContextualGraphicsOperation extends DelegatingGraphicsOperation {
    private GraphicsContext context;
    private List operations;
    private TransformationsGraphicsOperation transformations;

    public ContextualGraphicsOperation( GraphicsOperation delegate ) {
        super( delegate );
        this.context = new GraphicsContext();
        this.operations = new ArrayList();
    }

    public void addOperation( GraphicsOperation go ) {
        operations.add( go );
    }

    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        super.addPropertyChangeListener( listener );
        for( Iterator i = operations.iterator(); i.hasNext(); ){
            GraphicsOperation go = (GraphicsOperation) i.next();
            go.addPropertyChangeListener( listener );
        }
    }

    public GraphicsContext getContext() {
        return context;
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        super.removePropertyChangeListener( listener );
        for( Iterator i = operations.iterator(); i.hasNext(); ){
            GraphicsOperation go = (GraphicsOperation) i.next();
            go.removePropertyChangeListener( listener );
        }
    }

    public void setTransformations( TransformationsGraphicsOperation transformations ) {
        this.transformations = transformations;
    }

    public void verify() {
        super.verify();
        for( Iterator i = operations.iterator(); i.hasNext(); ){
            GraphicsOperation go = (GraphicsOperation) i.next();
            go.verify();
        }
        if( transformations != null ){
            transformations.verify();
        }
    }

    protected void afterDelegateExecutes( Graphics2D g, Component target ) {
        if( operations.size() > 0 || transformations != null ){
            restoreContext( g );
        }
    }

    protected void beforeDelegateExecutes( Graphics2D g, Component target ) {
        if( operations.size() > 0 || transformations != null ){
            saveContext( g, target );
        }
        if( operations.size() > 0 ){
            for( Iterator i = operations.iterator(); i.hasNext(); ){
                GraphicsOperation go = (GraphicsOperation) i.next();
                executeChildOperation( g, target, go );
            }
        }
        if( transformations != null ){
            transformations.execute( g, target );
        }
        if( operations.size() > 0 || transformations != null ){
            restoreClip( g );
        }
    }

    protected void executeChildOperation( Graphics2D g, Component target, GraphicsOperation go ) {
        go.execute( g, target );
    }

    protected List getOperations() {
        return Collections.unmodifiableList( operations );
    }

    private void restoreClip( Graphics2D g ) {
        context.restoreClip( g );
    }

    private void restoreContext( Graphics2D g ) {
        context.restore( g );
    }

    private void saveContext( Graphics2D g, Component target ) {
        context.save( g, getClip( g, target ) );
    }
}