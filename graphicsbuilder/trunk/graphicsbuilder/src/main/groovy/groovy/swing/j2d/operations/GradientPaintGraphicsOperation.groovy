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

import java.awt.Color
import java.awt.Paint
import java.awt.Shape
import java.awt.Rectangle
import java.awt.GradientPaint
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.AbstractPaintingGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class GradientPaintGraphicsOperation extends AbstractPaintingGraphicsOperation {
    protected static required = ['x1','y1','x2','y2','color1','color2']
    //protected static optional = super.optional + ['cyclic','stretch']

    def x1 = 0
    def x2 = 100
    def y1 = 0
    def y2 = 0
    def color1 = 'black'
    def color2 = 'white'
    def cyclic = false
    def stretch = false
    
    public GradientPaintGraphicsOperation() {
        super( "gradientPaint" )
    }

    public Paint getPaint( GraphicsContext context, Rectangle2D bounds ) {
       def myBounds = [x1,y1,x2-x1,y2-y1] as Rectangle
       println "$myBounds $bounds"
       def transform = new AffineTransform()
       def translate = AffineTransform.getTranslateInstance( x1 - bounds.x, y1 - bounds.y )
       transform.concatenate( translate )
       if( stretch ){
          // scale it as a square
          def w = bounds.width / myBounds.width
          def h = bounds.height / myBounds.height
          if( w > h ){
             transform.concatenate( AffineTransform.getScaleInstance( w, w ) )
          }else{
             transform.concatenate( AffineTransform.getScaleInstance( h, h ) )
          }
       }
       def translated = transform.createTransformedShape( myBounds ).bounds2D 
       def gp =  new GradientPaint( new Point2D.Double(translated.x, 
                                                    translated.y),
                                 getColor(color1),
                                 new Point2D.Double(translated.x + translated.width, 
                                                    translated.x + translated.width),
                                 getColor(color2),
                                 cyclic as boolean )
       println "${gp.point1} ${gp.point2} ${gp.color1} ${gp.color2}"
       return gp
    }
    
    private Color getColor( value ) {
       if( value instanceof String ){
          return ColorCache.getInstance().getColor( value )
       }else if( value instanceof Color ){
          return value
       }
    }
}