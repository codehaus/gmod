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
import groovy.swing.j2d.operations.StrokeProvider
import groovy.swing.j2d.operations.AbstractGraphicsOperation

import java.awt.BasicStroke
import java.awt.Paint
import java.awt.Stroke
import java.awt.Shape
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractStrokeGraphicsOperation extends AbstractGraphicsOperation implements StrokeProvider {
    public static optional = ['asStroke']

	private Stroke stroke
	def asStroke

    public AbstractStrokeGraphicsOperation( String name ) {
        super( name )
    }

    protected void doExecute( GraphicsContext context ) {
       if( asStroke ) return
       context.g.stroke = getStroke()
    }

    public Stroke getStroke(){
       if( stroke == null ){
          stroke = createStroke()
       }
       return stroke
    }

    public Shape createStrokedShape( Shape shape ){
    	createStroke().createStrokedShape(shape)
    }
    
    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       stroke = null
    }

    protected abstract Stroke createStroke()
}
