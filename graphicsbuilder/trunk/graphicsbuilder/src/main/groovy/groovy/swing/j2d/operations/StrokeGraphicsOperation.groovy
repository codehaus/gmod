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

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.AbstractGraphicsOperation

import java.awt.BasicStroke
import java.awt.Paint
import java.awt.Stroke

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StrokeGraphicsOperation extends AbstractGraphicsOperation {
	protected static required = ['width']
	protected static optional = ['cap','join','mitterlimit','dash','dashphase']

	private Stroke stroke

	def width = 1
	def cap
	def join
	def mitterlimit
	def dash
	def dashphase

    public StrokeGraphicsOperation() {
        super( "stroke" )
    }

    public void execute( GraphicsContext context ) {
        if( stroke == null ){
            createStroke()
        }

        context.g.stroke = stroke
    }

    private void createStroke() {
        def w = width
        def c = cap
        def j = join
        def m = mitterlimit
        def d = dash

        if( c == null ){
            c = BasicStroke.CAP_SQUARE
        }else{
           c = getCapValue()
        }
        if( j == null ){
           j = BasicStroke.JOIN_MITER
        }else{
           j = getJoinValue()
        }
        if( m == null ) m = 10

        if( dash != null && dashphase != null ){
           stroke = new BasicStroke( w as float, c as int, j as int, m as float, dash as float[], dashphase as float )
        }else{
           stroke = new BasicStroke( w as float, c as int, j as int, m as float)
        }
    }

    private int getCapValue() {
        if( cap instanceof Number ){
            return cap
        }else if( cap instanceof String ){
            if( "butt".compareToIgnoreCase( cap ) == 0 ){
                return BasicStroke.CAP_BUTT
            }else if( "round".compareToIgnoreCase( cap ) == 0 ){
               return BasicStroke.CAP_ROUND
            }else if( "square".compareToIgnoreCase( cap ) == 0 ){
               return BasicStroke.CAP_SQUARE
            }
            throw new IllegalArgumentException( "'cap=$cap' is not one of [butt,round,square]" )
        }
        throw new IllegalArgumentException( "'cap' value is not a String nor an int" )
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
                throw new IllegalArgumentException( "dash[${index}] is not a Number" );
            }
        }
        return array
    }

    private int getJoinValue() {
        if( join instanceof Number ){
            return join
        }else if( join instanceof String ){
            if( "bevel".compareToIgnoreCase( join ) == 0 ){
                return BasicStroke.JOIN_BEVEL
            }else if( "round".compareToIgnoreCase( join ) == 0 ){
               return BasicStroke.JOIN_ROUND
            }else if( "miter".compareToIgnoreCase( join ) == 0 ){
               return BasicStroke.JOIN_MITER
            }
            throw new IllegalArgumentException( "'join=$join' is not one of [bevel,miter,round]" )
        }
        throw new IllegalArgumentException( "'join' value is not a String nor an int" )
    }
}