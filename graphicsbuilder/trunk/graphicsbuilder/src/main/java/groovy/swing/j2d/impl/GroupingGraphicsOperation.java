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

import groovy.lang.MissingPropertyException;
import groovy.swing.j2d.GraphicsContext;
import groovy.swing.j2d.GraphicsOperation;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GroupingGraphicsOperation extends AbstractGraphicsOperation {
    private Object color;
    private Object fill;
    private GraphicsScope graphicsScope;
    private List operations = new ArrayList();
    private Object strokeWidth;
    private TransformationsGraphicsOperation transformations;

    public GroupingGraphicsOperation() {
        this( null );
    }

    public GroupingGraphicsOperation( List operations ) {
        super( "group", null );
        if( operations != null ){
            this.operations.addAll( operations );
        }
        this.graphicsScope = new GraphicsScope();
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

    public Object getColor() {
        return color;
    }

    public Object getFill() {
        return fill;
    }

    public final List getOperations() {
        return Collections.unmodifiableList( operations );
    }

    public GraphicsScope getScope() {
        return graphicsScope;
    }

    public Object getStrokeWidth() {
        return strokeWidth;
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

    public void setColor( Object color ) {
        Object oldValue = this.color;
        this.color = color;
        firePropertyChange( "color", oldValue, color );
    }

    public void setFill( Object fill ) {
        Object oldValue = this.fill;
        this.fill = fill;
        firePropertyChange( "fill", oldValue, fill );
    }

    public void setStrokeWidth( Object strokeWidth ) {
        Object oldValue = this.strokeWidth;
        this.strokeWidth = strokeWidth;
        firePropertyChange( "strokeWidth", oldValue, strokeWidth );
    }

    public void setTransformations( TransformationsGraphicsOperation transformations ) {
        this.transformations = transformations;
    }

    public final int size() {
        return operations.size();
    }

    public void verify() {
        for( Iterator i = operations.iterator(); i.hasNext(); ){
            GraphicsOperation go = (GraphicsOperation) i.next();
            go.verify();
        }
        if( transformations != null ){
            transformations.verify();
        }
    }

    protected void doExecute( GraphicsContext context ) {
        saveScope( context );
        if( transformations != null ){
            transformations.execute( context );
        }
        if( operations.size() > 0 ){
            for( Iterator i = operations.iterator(); i.hasNext(); ){
                GraphicsOperation go = (GraphicsOperation) i.next();
                if( color != null ){
                    safeSetProperty( go, "color", color );
                }
                if( strokeWidth != null ){
                    safeSetProperty( go, "strokeWidth", strokeWidth );
                }
                if( fill != null ){
                    safeSetProperty( go, "fill", fill );
                }
                go.execute( context );
            }
        }
        restoreClip( context );
        restoreScope( context );
    }

    private void restoreClip( GraphicsContext context ) {
        graphicsScope.restoreClip( context );
    }

    private void restoreScope( GraphicsContext context ) {
        graphicsScope.restore( context );
    }

    private void safeSetProperty( Object obj, String name, Object value ) {
        try{
            InvokerHelper.setProperty( obj, name, value );
        }catch( MissingPropertyException mpe ){
            // ignore
        }
    }

    private void saveScope( GraphicsContext context ) {
        graphicsScope.save( context, getClip( context ) );
    }
}