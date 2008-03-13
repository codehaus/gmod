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

package groovy.swing.j2d.operations.misc

import java.awt.Shape
import java.awt.geom.AffineTransform
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.ShapeProvider
import groovy.swing.j2d.operations.OutlineProvider
import groovy.swing.j2d.operations.shapes.AbstractShapeGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class DrawGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = ['shape']
    public static optional = AbstractShapeGraphicsOperation.optional - ['asShape','keepTrans']

    def shape
    def keepTrans

    public DrawGraphicsOperation() {
        super( "draw" )
    }

    public Shape getShape( GraphicsContext context) {
        def s = null
        if( shape instanceof ShapeProvider || shape instanceof OutlineProvider ){
           s = shape.getLocallyTransformedShape(context)
        }else if( shape instanceof Shape ){
           s = shape
        }else{
           throw new IllegalArgumentException("draw.shape must be one of [java.awt.Shape,OutlineProvider,ShapeProvider]")
        }

        if( keepTrans ) return s
        
        // translate to world origin
        def bounds = s.bounds
        if( bounds.x != 0 || bounds.y != 0 ){
           s = AffineTransform.getTranslateInstance( bounds.x*(-1), bounds.y*(-1)).createTransformedShape(s)
        }
        return s
    }

    protected void fill( GraphicsContext context, Shape shape ) {
        if( this.shape instanceof OutlineProvider ) return
        super.fill( context, shape )
    }
}
