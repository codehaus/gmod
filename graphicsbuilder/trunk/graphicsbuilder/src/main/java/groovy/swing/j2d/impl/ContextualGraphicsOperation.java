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

import groovy.swing.j2d.GraphicsContext;
import groovy.swing.j2d.GraphicsOperation;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Decorator that adds a GraphicsScope to its delegate.<br>
 * A GraphicsScope takes care of restoring the state of the Graphics2D object
 * after this operation's delegate has been executed.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ContextualGraphicsOperation extends DelegatingGraphicsOperation {
    private List operations;
    private GraphicsScope scope;
    private TransformationsGraphicsOperation transformations;

    public ContextualGraphicsOperation( GraphicsOperation delegate ) {
        super( delegate );
        this.scope = new GraphicsScope();
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

    public GraphicsScope getScope() {
        return scope;
    }

    public TransformationsGraphicsOperation getTransformations() {
        return transformations;
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

    protected void afterDelegateExecutes( GraphicsContext context ) {
        if( operations.size() > 0 || transformations != null ){
            restoreScope( context );
        }
    }

    protected void beforeDelegateExecutes( GraphicsContext context ) {
        if( operations.size() > 0 || transformations != null ){
            saveScope( context );
        }
        if( operations.size() > 0 ){
            for( Iterator i = operations.iterator(); i.hasNext(); ){
                GraphicsOperation go = (GraphicsOperation) i.next();
                executeChildOperation( context, go );
            }
        }
        if( transformations != null ){
            transformations.execute( context );
        }
        if( operations.size() > 0 || transformations != null ){
            restoreClip( context );
        }
    }

    protected void executeChildOperation( GraphicsContext context, GraphicsOperation go ) {
        go.execute( context );
    }

    protected List getOperations() {
        return Collections.unmodifiableList( operations );
    }

    private void restoreClip( GraphicsContext context ) {
        scope.restoreClip( context );
    }

    private void restoreScope( GraphicsContext context ) {
        scope.restore( context );
    }

    private void saveScope( GraphicsContext context ) {
        scope.save( context, getClip( context ) );
    }
}