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

import groovy.swing.j2d.StrokeProvider
import groovy.swing.j2d.impl.ComposableStroke
import groovy.swing.j2d.impl.AbstractStrokeGraphicsOperation

import java.awt.Stroke
import com.jhlabs.awt.ZigzagStroke

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ZigzagStrokeGraphicsOperation extends AbstractStrokeGraphicsOperation implements ComposableStroke {
    public static required = ['stroke']
	public static optional = super.optional + ['amplitude','wavelength']

	def stroke
	def amplitude = 10
	def wavelength = 10

	ZigzagStrokeGraphicsOperation() {
       super( "zigzagStroke" )
    }

    public void addStroke( Stroke stroke ){
	   setStroke( stroke )
	}

    public void addStroke( StrokeProvider stroke ){
	   setStroke( stroke )
	}

    protected Stroke createStroke() {
       if( !stroke ){
          throw new IllegalArgumentException("${this}.stroke is null.")
       }

       def s = stroke instanceof StrokeProvider ? stroke.stroke : stroke
       return new ZigzagStroke( s, amplitude as float, wavelength as float )
    }
}