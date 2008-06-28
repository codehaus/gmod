/*
 * Copyright 2007-2008 the original author or authors.
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

package groovy.swing.j2d.factory

import groovy.swing.j2d.operations.ShapeProvider
import groovy.swing.j2d.operations.misc.ShapeGraphicsOperation
import groovy.swing.j2d.operations.shapes.AreaGraphicsOperation
import groovy.swing.j2d.operations.strokes.ShapeStrokeGraphicsOperation

import java.awt.Shape

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ShapeFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
            Map properties ) throws InstantiationException, IllegalAccessException {
        ShapeGraphicsOperation go = new ShapeGraphicsOperation()
        if( value != null && (Shape.class.isAssignableFrom( value.class ) ||
             value instanceof ShapeProvider ) ) {
            go.shape = value
        }
        return go
    }

    public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
       if( parent instanceof AreaGraphicsOperation ) {
          parent.addOperation( child )
       }else if( parent instanceof ShapeStrokeGraphicsOperation ) {
          parent.addShape( child )
       }else{
          throw new IllegalArgumentException("shape() can only be nested in any of [add, subtract, intersect, xor, shapeStroke]")
       }
    }
}