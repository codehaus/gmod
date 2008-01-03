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
import java.awt.font.FontRenderContext
import java.awt.font.TextLayout
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.beans.PropertyChangeEvent

import groovy.swing.j2d.GraphicsContext

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TextGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = ['text','x','y']

    private Shape outline

    def text = "Groovy"
    def x = 0
    def y = 0

    TextGraphicsOperation() {
        super( "text" )
    }

    public Shape getShape( GraphicsContext context ){
        if( outline == null ){
           calculateOutline(context)
        }
        outline
    }

    public void propertyChange( PropertyChangeEvent event ) {
       if( required.contains(event.propertyName) ){
          outline = null
       }
    }

    private void calculateOutline( GraphicsContext context ) {
        def g = context.g
        /*
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
        */

        FontRenderContext frc = g.getFontRenderContext()
        TextLayout layout = new TextLayout( text, g.font, frc )
        Rectangle2D bounds = layout.getBounds()
        outline = layout.getOutline( AffineTransform.getTranslateInstance( x, y + bounds.height ) )
    }
}