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

package groovy.swing.j2d.operations.paints

import java.awt.Color
import java.awt.Paint
import java.awt.GradientPaint
import java.awt.geom.Point2D
import groovy.swing.j2d.ColorCache

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class GradientPaintGraphicsOperation extends AbstractLinearGradientPaintGraphicsOperation {
    public static required = super.required + ['color1','color2']

    def color1 = Color.BLACK
    def color2 = Color.WHITE

    GradientPaintGraphicsOperation(){
       super( "gradientPaint" )
       cycle = DEFAULT_CYCLE_VALUE
    }

    protected Paint makePaint( x1, y1, x2, y2 ){
       return new GradientPaint( new Point2D.Double(x1,y1),
                                 color1,
                                 new Point2D.Double(x2,y2),
                                 color2,
                                 cycle as boolean )
    }

    void setProperty( String property, Object value ) {
       if( property == "color1" || property == "color2" && value instanceof String ){
          super.setProperty( property, ColorCache.getInstance().getColor( value ) )
       }else{
          super.setProperty( property, value )
       }
    }

    private Color getColor( value ) {
       if( value instanceof String ){
          return ColorCache.getInstance().getColor( value )
       }else if( value instanceof Color ){
          return value
       }
    }
}