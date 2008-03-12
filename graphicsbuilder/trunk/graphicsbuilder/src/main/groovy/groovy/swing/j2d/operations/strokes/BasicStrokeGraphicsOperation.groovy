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

package groovy.swing.j2d.operations.strokes

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsBuilderHelper

import java.awt.BasicStroke
import java.awt.Stroke

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class BasicStrokeGraphicsOperation extends AbstractStrokeGraphicsOperation {
	public static required = ['width']
	public static optional = AbstractStrokeGraphicsOperation.optional + ['cap','join','miterlimit','dash','dashphase','color','opacity']

	def width = 1
	def cap
	def join
	def miterlimit
	def dash
	def dashphase = 0
	def color
	def opacity

    public BasicStrokeGraphicsOperation() {
        super( "basicStroke" )
    }

    protected void doExecute( GraphicsContext context ) {
        if( asStroke ) return
        def c = ColorCache.getInstance().getColor(color)
        if( opacity != null ){
           c = c.derive(alpha:opacity)
        }
        context.g.color = c
        context.g.stroke = getStroke()
    }

    protected Stroke createStroke() {
        def w = width
        def c = cap
        def j = join
        def m = miterlimit
        def d = dash

        c = GraphicsBuilderHelper.getCapValue(c)
        j = GraphicsBuilderHelper.getJoinValue(j)
        d = GraphicsBuilderHelper.getDashValue(d)
        if( m == null ) m = 10

        if( dash != null && dashphase != null ){
           return new BasicStroke( w as float, c as int, j as int, m as float, dash as float[], dashphase as float )
        }else{
           return new BasicStroke( w as float, c as int, j as int, m as float)
        }
    }
}