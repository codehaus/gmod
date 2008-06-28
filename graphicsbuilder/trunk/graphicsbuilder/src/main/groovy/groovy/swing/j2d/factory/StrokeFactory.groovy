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

import groovy.swing.j2d.operations.Grouping
import groovy.swing.j2d.operations.OutlineProvider
import groovy.swing.j2d.operations.ShapeProvider
import groovy.swing.j2d.operations.StrokeProvider
import groovy.swing.j2d.operations.strokes.ComposableStroke
import groovy.swing.j2d.operations.strokes.StrokeGraphicsOperation

import java.awt.Stroke

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StrokeFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
          Map properties ) throws InstantiationException, IllegalAccessException {
       StrokeGraphicsOperation go = new StrokeGraphicsOperation()
       if( value != null && value instanceof StrokeProvider || value instanceof Stroke ) {
           go.stroke = value
       }
       return go
    }

    public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
       if( parent instanceof Grouping || parent instanceof OutlineProvider ||
             parent instanceof ShapeProvider ) {
          parent.addOperation( child )
       }else if( parent instanceof ComposableStroke ){
          parent.addStroke( child )
       }else{
          throw new IllegalArgumentException("$child can not be nested inside $parent")
       }
    }
}