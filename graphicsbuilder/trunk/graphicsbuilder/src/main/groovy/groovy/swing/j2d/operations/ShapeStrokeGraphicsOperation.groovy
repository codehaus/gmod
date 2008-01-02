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
import groovy.swing.j2d.ShapeProvider
import groovy.swing.j2d.impl.AbstractStrokeGraphicsOperation

import java.awt.Shape
import java.awt.Stroke
import com.jhlabs.awt.ShapeStroke

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ShapeStrokeGraphicsOperation extends AbstractStrokeGraphicsOperation {
	public static optional = super.optional + ['shapes','advance']

	def shapes = []
	def advance = 10

    public ShapeStrokeGraphicsOperation() {
        super( "shapeStroke" )
    }

	public void addShape( Shape shape ){
	   shapes << shape
	}

	public void addShape( ShapeProvider shape ){
	   shapes << shape
	}

    protected Stroke createStroke() {
        if( !shapes ) throw new IllegalArgumentException("shapeStroke() requires at least 1 shape.")
        def s = []
        shapes.each { shape ->
           if( shape instanceof Shape ) s << shape
           if( shape instanceof ShapeProvider ) s << shape.getLocallyTransformedShape(null)
        }

        return new ShapeStroke( s as Shape[], advance as float )
    }
}