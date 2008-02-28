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
import java.awt.font.TextLayout
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Rectangle2D
import java.beans.PropertyChangeEvent

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.misc.FontGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TextGraphicsOperation extends AbstractShapeGraphicsOperation {
    static required = ['text','x','y']
    static optional = super.optional + ['spacing']

    private Shape outline

    def text = "Groovy"
    def x = 0
    def y = 0
    def spacing = 0

    TextGraphicsOperation() {
        super( "text" )
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

        def frc = g.getFontRenderContext()
        def fm = g.fontMetrics

        def t = text.split(/\n/)
        def layout = new TextLayout( t[0], g.font, frc )
        def dy = fm.ascent*2 - fm.height
        outline = new Area(layout.getOutline( AffineTransform.getTranslateInstance( x, y + dy ) ))

        if( t.size() > 1 ){
           t[1..-1].inject(y+fm.ascent) { ny, txt ->
              layout = new TextLayout( txt, g.font, frc )
              def py = ny + spacing + dy
              def s = new Area(layout.getOutline(AffineTransform.getTranslateInstance(x,py)))
              outline.add( s )
              return ny + spacing + fm.ascent
           }
        }
    }
}