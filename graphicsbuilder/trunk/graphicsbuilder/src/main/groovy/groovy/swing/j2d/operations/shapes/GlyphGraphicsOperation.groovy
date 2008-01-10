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

import java.awt.Font
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.beans.PropertyChangeEvent

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.misc.FontGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GlyphGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = ['glyph']
    public static optional = ['cx','cy']

    private Shape outline

    def glyph = "G"
    def cx
    def cy

    GlyphGraphicsOperation() {
        super( "glyph" )
    }

    public Shape getShape( GraphicsContext context ){
        if( outline == null ){
           calculateOutline(context)
        }
        outline
    }

    protected void localPropertyChange( PropertyChangeEvent event ) {
       super.localPropertyChange( event )
       outline = null
    }

    private void calculateOutline( GraphicsContext context ) {
        def g = context.g
        if( operations ){
           // apply last font() if any
           def fo = operations.reverse().find { it instanceof FontGraphicsOperation }
           if( fo ){
              def contextCopy = context.copy()
              contextCopy.g = g.clone()
              fo.execute( contextCopy )
              g = contextCopy.g
           }
        }

        def glyphVector = g.font.createGlyphVector( g.getFontRenderContext(), glyph[0] )
        outline = glyphVector.getOutline()
        def bounds = outline.bounds
        outline = AffineTransform.getTranslateInstance(
           bounds.x * -1, bounds.y * -1
        ).createTransformedShape(outline)
        if( cx != null && cy != null ){
           outline = AffineTransform.getTranslateInstance(
              (cx - (bounds.width/2)) as double,
              (cy - (bounds.height/2)) as double
           ).createTransformedShape(outline)
        }
    }
}