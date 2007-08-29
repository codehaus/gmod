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

import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.impl.ContextualGraphicsOperation
import groovy.swing.j2d.impl.GroupingGraphicsOperation
import groovy.swing.j2d.impl.StrokingGraphicsOperation
import groovy.swing.j2d.impl.StrokingAndFillingGraphicsOperation
import groovy.swing.j2d.impl.TransformationsGraphicsOperation
import groovy.swing.j2d.impl.TransformSupportGraphicsOperation
import groovy.util.AbstractFactory
import groovy.util.FactoryBuilderSupport

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractGraphicsOperationFactory extends AbstractFactory {
    public void onNodeCompleted( FactoryBuilderSupport builder, Object parent, Object node ) {
        if( parent && safePropertyGet(parent, "fillable") && safePropertyGet(node, "supportsFill") ){
           if( parent.fill instanceof Boolean && !parent.fill ){
		       // do not override fill
           }else{
               parent.fill = true
           }
        }

        if( parent instanceof TransformationsGraphicsOperation &&
            node instanceof TransformSupportGraphicsOperation ){
            parent.addOperation( node )
        }else if( safePropertyGet(parent, "contextual") ){
            if( node instanceof TransformationsGraphicsOperation ){
                parent.transformations = node
            }else{
                parent.addOperation( node )
            }
        }else if( parent instanceof GroupingGraphicsOperation &&
                  node instanceof TransformationsGraphicsOperation ){
            parent.transformations = node
        }else if( parent instanceof GroupingGraphicsOperation ){
            parent.addOperation( node )
        }
        else{
            List operations = builder.getOperations()
            if( operations != null && node instanceof GraphicsOperation ){
                operations.add( node )
            }
        }
    }

    protected boolean safePropertyGet( Object bean, String name ){
        try{
            return bean?."${name}"
        }catch( MissingPropertyException mpe ){
            // ignore
        }
        return null
    }

    protected GraphicsOperation wrap( GraphicsOperation go ){
        if( safePropertyGet(go, "fillable") ){
            go = new StrokingAndFillingGraphicsOperation( go )
        }else if( safePropertyGet(go, "strokable") ){
            go = new StrokingGraphicsOperation( go )
        }
        if( safePropertyGet(go, "contextual") ){
            go = new ContextualGraphicsOperation( go )
        }
        return go
    }
}