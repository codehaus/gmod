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

import java.awt.Shape
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.ShapeProvider
import groovy.swing.j2d.OutlineProvider
import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class DrawGraphicsOperation extends AbstractShapeGraphicsOperation {
    protected static required = ['shape']
    protected static optional = super.optional - ['asShape']

    def shape

    public DrawGraphicsOperation() {
        super( "draw" )
    }

    public Shape getShape( GraphicsContext context) {
        if( shape instanceof ShapeProvider || shape instanceof OutlineProvider ){
           return shape.getLocallyTransformedShape(context)
        }else if( shape instanceof Shape ){
           return shape
        }
        throw new IllegalArgumentException("draw.shape must be one of [java.awt.Shape,OutlineProvider,ShapeProvider]")
    }

    protected void fill( GraphicsContext context ) {
        if( shape instanceof OutlineProvider ) return
        super.fill( context )
    }
}