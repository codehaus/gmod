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

import groovy.swing.j2d.operations.StrokeProvider

import java.awt.Stroke
import com.jhlabs.awt.CompositeStroke

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class CompositeStrokeGraphicsOperation extends AbstractStrokeGraphicsOperation implements ComposableStroke {
	public static optional = AbstractStrokeGraphicsOperation.optional + ['stroke1','stroke2']

	def stroke1
	def stroke2

    public CompositeStrokeGraphicsOperation() {
        super( "compositeStroke" )
    }

	public void addStroke( Stroke stroke ){
	   if( !stroke1 ){
	      stroke1 = stroke
	   }else if( !stroke2 ){
	      stroke2 = stroke
	   }
	}

	public void addStroke( StrokeProvider stroke ){
	   if( !stroke1 ){
	      stroke1 = stroke
	   }else if( !stroke2 ){
	      stroke2 = stroke
	   }
	}

    protected Stroke createStroke() {
        if( !stroke1 || !stroke2 ){
           throw new IllegalArgumentException("${this} must have two strokes.")
        }

        def s1 = stroke1 instanceof StrokeProvider ? stroke1.stroke : stroke1
        def s2 = stroke1 instanceof StrokeProvider ? stroke2.stroke : stroke2
        return new CompositeStroke( s1, s2 )
    }
}