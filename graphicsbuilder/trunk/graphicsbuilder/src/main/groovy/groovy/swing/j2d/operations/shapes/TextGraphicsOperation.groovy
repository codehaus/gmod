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
    static optional = AbstractShapeGraphicsOperation.optional + ['spacing','halign','valign']

    public static final int LEFT = 0
    public static final int CENTER = 1
    public static final int RIGHT = 2
    public static final int TOP = 0
    public static final int MIDDLE = 1
    public static final int BASELINE = 2
    public static final int BOTTOM = 3

    private Shape outline

    def text = "Groovy"
    def x = 0
    def y = 0
    def spacing = 0
    def halign = 'left'
    def valign = 'bottom'

    TextGraphicsOperation() {
        super( "text" )
    }

    public Shape getShape( GraphicsContext context ){
        if( outline == null ){
           calculateOutline(context)
        }
        outline
    }

    public boolean hasXY() {
       true
    }
    
    protected void localPropertyChange( PropertyChangeEvent event ) {
       super.localPropertyChange( event )
       outline = null
    }

    protected void executeBeforeAll( GraphicsContext context ) {
       // make sure nested font nodes are executed before
       def fo = operations.reverse().find { it instanceof FontGraphicsOperation }
       if( fo ){
          fo.execute( context )
       }
       
       super.executeBeforeAll( context )
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

        def t = text.split(/\n/)
        if( t.size() == 1 ){
           // single row text
           calculateSingleRowOutline( context )
        }else{
           // multiple row text
           calculateMultipleRowOutline( context, t )
        }
    }

    private void calculateSingleRowOutline( GraphicsContext context ) {
       def g = context.g
       def frc = g.getFontRenderContext()
       def fm = g.fontMetrics

       def layout = new TextLayout( text, g.font, frc )

       def dx = 0
       def dy = 0
       def sb = fm.getStringBounds(text,g)

       switch( getHalignValue() ){
          case LEFT: dx = x; break;
          case CENTER: dx = x - sb.width/2; break;
          case RIGHT: dx = x - sb.width; break;
       }

       switch( getValignValue() ){
          case BOTTOM: dy = y + fm.ascent*2 - fm.height; break;
          case MIDDLE: dy = y + fm.height - fm.ascent; break;
          case TOP: dy = y - fm.height + fm.ascent; break;
          case BASELINE: dy = y; break;
       }

       outline = layout.getOutline( AffineTransform.getTranslateInstance( dx, dy ) )
    }

    private void calculateMultipleRowOutline( GraphicsContext context, rows ) {
       def g = context.g
       def frc = g.getFontRenderContext()
       def fm = g.fontMetrics

       def layout = new TextLayout( rows[0], g.font, frc )
       def dy = fm.ascent*2 - fm.height
       outline = new Area(layout.getOutline( AffineTransform.getTranslateInstance( x, y + dy ) ))

       if( rows.size() > 1 ){
          rows[1..-1].inject(y+fm.ascent) { ny, txt ->
             layout = new TextLayout( txt, g.font, frc )
             def py = ny + spacing + dy
             def s = new Area(layout.getOutline(AffineTransform.getTranslateInstance(x,py)))
             outline.add( s )
             return ny + spacing + fm.ascent
          }
       }

       def dx = 0
       def sb = outline.bounds2D

       switch( getHalignValue() ){
          case LEFT: dx = 0; break;
          case CENTER: dx = 0 - sb.width/2; break;
          case RIGHT: dx = 0 - sb.width; break;
       }

       switch( getValignValue() ){
          case BOTTOM: dy = 0; break;
          case BASELINE:
          case MIDDLE: dy = 0 - sb.height/2; break;
          case TOP: dy = 0 - sb.height; break;
       }

       outline = AffineTransform.getTranslateInstance(dx,dy).createTransformedShape(outline)
    }

    private def getHalignValue(){
       if( !halign ) return LEFT
       if( halign instanceof Number ){
          return halign.intValue()
       }
       switch( halign ){
          case "left": return LEFT
          case "center": return CENTER
          case "right": return RIGHT
       }
       return LEFT
    }

    private def getValignValue(){
       if( !valign ) return BOTTOM
       if( valign instanceof Number ){
          return halign.intValue()
       }
       switch( valign ){
          case "top": return TOP
          case "middle": return MIDDLE
          case "baseline": return BASELINE
          case "bottom": return BOTTOM
       }
       return BOTTOM
    }
}