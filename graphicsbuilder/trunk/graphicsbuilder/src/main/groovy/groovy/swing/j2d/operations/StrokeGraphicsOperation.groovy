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
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.AbstractGraphicsOperation

import java.awt.BasicStroke
import java.awt.Paint
import java.awt.Stroke

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StrokeGraphicsOperation extends AbstractGraphicsOperation {
	def paint
	def color
	def width = 1
	def cap
	def join
	def miterlimit
	def dash
	def dashphase
	private Stroke stroke

	static supportsFill = true

    public StrokeGraphicsOperation() {
        super( "stroke", [] as String[], ["paint", "color", "width", "cap", "join", "miterlimit",
                "dash", "dashphase", "red", "green", "blue", "alpha"] as String[] )
    }

    public void doExecute( GraphicsContext context ) {
        if( parameterHasValue( "color" ) ){
            Object colorValue = getParameterValue( "color" )
            if( colorValue instanceof String ){
                colorValue = ColorCache.getInstance().getColor( colorValue );
            }
            context.g.color = colorValue
        }
        if( parameterHasValue( "paint" ) ){
            Object paintValue = getParameterValue( "paint" );
            if( paintValue instanceof String ){
                paintValue = ColorCache.getInstance().getColor( paintValue );
            }
            context.g.paint = paintValue
        }

        if( stroke == null ){
            createStroke()
        }

        context.g.stroke = stroke
    }

    public void verify() {
        // because all values are actually optional
        return;
    }

    private void createStroke() {
        boolean hasWidth = parameterHasValue( "width" );
        boolean hasCap = parameterHasValue( "cap" );
        boolean hasJoin = parameterHasValue( "join" );
        boolean hasMiterLimit = parameterHasValue( "miterlimit" );
        boolean hasDash = parameterHasValue( "dash" );
        boolean hasDashPhase = parameterHasValue( "dashphase" );

        if( hasWidth ){
            int width = getParameterValue( "width" )
            if( hasCap && hasJoin ){
                int cap = getCapValue()
                int join = getJoinValue()
                if( hasMiterLimit ){
                    int miterlimit = getParameterValue( "miterlimit" )
                    if( hasDash && hasDashPhase ){
                        float[] dash = getDashValue()
                        float dashphase = getParameterValue( "dashphase" )
                        stroke = new BasicStroke( width, cap, join, miterlimit, dash, dashphase )
                    }else{
                        stroke = new BasicStroke( width, cap, join, miterlimit )
                    }
                }else{
                    stroke = new BasicStroke( width, cap, join )
                }
            }else{
                stroke = new BasicStroke( width )
            }
        }else{
            stroke = new BasicStroke()
        }
    }

    private int getCapValue() {
        int cap = 0;
        Object capvalue = getParameterValue( "cap" );
        if( capvalue instanceof Number ){
            cap = capvalue
        }else if( capvalue instanceof String ){
            if( "butt".compareToIgnoreCase( capvalue ) == 0 ){
                cap = BasicStroke.CAP_BUTT
            }else if( "round".compareToIgnoreCase( capvalue ) == 0 ){
                cap = BasicStroke.CAP_ROUND
            }else if( "square".compareToIgnoreCase( capvalue ) == 0 ){
                cap = BasicStroke.CAP_SQUARE
            }else{
                throw new IllegalStateException( "'cap=" + capvalue
                        + "' is not one of [butt,round,square]" )
            }
        }else{
            throw new IllegalStateException( "'cap' value is not a String nor an int" )
        }
        return cap
    }

    private float[] getDashValue() {
        List dash = (List) getParameterValue( "dash" )
        float[] array = new float[dash.size()]
        array.eachWithIndex { value, index ->
            if( value instanceof Closure ){
                value = ((Closure) value).call()
            }
            if( value instanceof Number ){
                array[index] = value
            }else{
                throw new IllegalStateException( "dash[${index}] is not a Number" );
            }
        }
        return array;
    }

    private int getJoinValue() {
        int join = 0
        Object joinvalue = getParameterValue( "join" )
        if( joinvalue instanceof Number ){
            join = joinvalue
        }else if( joinvalue instanceof String ){
            if( "bevel".compareToIgnoreCase( joinvalue ) == 0 ){
                join = BasicStroke.JOIN_BEVEL
            }else if( "round".compareToIgnoreCase( joinvalue ) == 0 ){
                join = BasicStroke.JOIN_ROUND
            }else if( "miter".compareToIgnoreCase( joinvalue ) == 0 ){
                join = BasicStroke.JOIN_MITER
            }else{
                throw new IllegalStateException( "'join=" + joinvalue
                        + "' is not one of [bevel,miter,round]" )
            }
        }else{
            throw new IllegalStateException( "'join' value is not a String nor an int" )
        }
        return join
    }
}