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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.impl.AbstractGraphicsOperation
import groovy.swing.j2d.impl.PaintSupportGraphicsOperation

import java.awt.Color
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.Rectangle
import java.awt.image.ImageObserver

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GradientPaintGraphicsOperation extends AbstractGraphicsOperation implements
   PaintSupportGraphicsOperation {
    private Paint paint
    def x1
    def y1
    def x2
    def y2
    def color1
    def color2
    def cycle

    static supportsFill = true

    GradientPaintGraphicsOperation() {
        super( "gradientPaint", ["x1", "y1", "color1", "x2", "y2", "color2"] as String[],
               ["cycle"] as String[] )
    }

    public Paint adjustPaintToBounds( Rectangle bounds ){
        return getPaint()
    }

    public Paint getPaint() {
        float x1 = getParameterValue( "x1" )
        float x2 = getParameterValue( "x2" )
        float y1 = getParameterValue( "y1" )
        float y2 = getParameterValue( "y2" )
        Object c1 = getParameterValue( "color1" )
        Object c2 = getParameterValue( "color2" )
        Color color1 = null
        Color color2 = null
        if( c1 instanceof String ){
            color1 = ColorCache.getInstance().getColor( c1 )
        }else if( c1 instanceof Color ){
            color1 = (Color) c1
        }
        if( c2 instanceof String ){
            color2 = ColorCache.getInstance().getColor( c2 )
        }else if( c2 instanceof Color ){
            color2 = (Color) c2
        }

        boolean cycle = false
        if( parameterHasValue( "cycle" ) ){
            Object c = getParameterValue( "cycle" )
            if( c instanceof Boolean ){
                cycle = c.booleanValue()
            }
        }

        paint = new GradientPaint( x1, y1, color1, x2, y2, color2, cycle )
        return paint
    }

    public void verify() {
        Map parameters = getParameterMap()
        parameters.each { k, v ->
            if( !k.equals( "cycle" ) ){
                if( !v ){
                    throw new IllegalStateException( "Property '${k}' for 'gradientPaint' has no value" );
                }
            }
        }
    }

    protected void doExecute( Graphics2D g, ImageObserver observer ){
        g.paint = getPaint()
    }
}