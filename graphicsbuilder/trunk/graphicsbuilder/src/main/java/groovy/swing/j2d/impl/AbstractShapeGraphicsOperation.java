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

import java.awt.Shape;

/**
 * Base class for shape drawing operations
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractShapeGraphicsOperation extends AbstractGraphicsOperation {
    public static boolean contextual = true;
    public static boolean fillable = true;
    public static boolean hasShape = true;
    private Shape shape;

    public AbstractShapeGraphicsOperation( String name ) {
        super( name );
    }

    public AbstractShapeGraphicsOperation( String name, String[] parameters ) {
        super( name, parameters );
    }

    public AbstractShapeGraphicsOperation( String name, String[] parameters, String[] optional ) {
        super( name, parameters, optional );
    }

    public Shape getClip( GraphicsContext context ) {
        if( shape == null || isDirty() ){
            shape = computeShape( context );
            setDirty( false );
        }
        return shape;
    }

    protected abstract Shape computeShape( GraphicsContext context );

    protected void doExecute( GraphicsContext context ) {
        context.getG().draw( getClip( context ) );
    }

    protected Shape getShape() {
        return shape;
    }
}