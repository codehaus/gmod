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

package groovy.swing.j2d.factory

import groovy.swing.j2d.operations.ShapeProvider
import groovy.swing.j2d.operations.OutlineProvider
import groovy.swing.j2d.operations.BorderPaintProvider
import groovy.swing.j2d.operations.PaintProvider
import groovy.swing.j2d.operations.MultiPaintProvider
import groovy.swing.j2d.operations.paints.BorderPaintGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class BorderPaintFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
            Map properties ) throws InstantiationException, IllegalAccessException {
        BorderPaintGraphicsOperation go = new BorderPaintGraphicsOperation()
        if( value != null &&
              (value instanceof PaintProvider || value instanceof MultiPaintProvider) ) {
            go.paint = value
        }
        return go
    }

    public void setParent( FactoryBuilderSupport builder, Object parent, Object child ){
       if( parent instanceof ShapeProvider || parent instanceof OutlineProvider ||
           parent instanceof BorderPaintProvider ){
          parent.addOperation( child )
          return
       }
       throw new IllegalArgumentException("$child can not be nested inside $parent")
    }

    public boolean isLeaf(){
        return false
    }
}