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
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GroupingGraphicsOperation extends AbstractGraphicsOperation {
    private GraphicsContext context;
    private List operations = new ArrayList();
    private TransformationsGraphicsOperation transformations;

    public GroupingGraphicsOperation() {
        this( null );
    }

    public GroupingGraphicsOperation( List operations ) {
        super( "group", null );
        if( operations != null ){
            this.operations.addAll( operations );
        }
        this.context = new GraphicsContext();
    }

    public void addOperation( GraphicsOperation go ) {
        operations.add( go );
    }

    public GraphicsContext getContext() {
        return context;
    }

    public final List getOperations() {
        return Collections.unmodifiableList( operations );
    }

    public void setTransformations( TransformationsGraphicsOperation transformations ) {
        this.transformations = transformations;
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

    protected void doExecute( Graphics2D g, ImageObserver observer ) {
        saveContext( g, observer );
        if( operations.size() > 0 ){
            for( Iterator i = operations.iterator(); i.hasNext(); ){
                GraphicsOperation go = (GraphicsOperation) i.next();
                go.execute( g, observer );
            }
        }
        if( transformations != null ){
            transformations.execute( g, observer );
        }
        restoreClip( g );
        restoreContext( g );
    }

    private void restoreClip( Graphics2D g ) {
        context.restoreClip( g );
    }

    private void restoreContext( Graphics2D g ) {
        context.restore( g );
    }

    private void saveContext( Graphics2D g, ImageObserver observer ) {
        context.save( g, getClip( g, observer ) );
    }
}