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

import groovy.swing.j2d.operations.RectGraphicsOperation
import groovy.swing.j2d.operations.Rect3DGraphicsOperation
import groovy.swing.j2d.operations.RoundRectGraphicsOperation
import groovy.util.FactoryBuilderSupport

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class RectFactory extends AbstractGraphicsOperationFactory {
     public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
             Map properties ) throws InstantiationException, IllegalAccessException {
         if( properties.containsKey("arcWidth") && properties.containsKey("arcHeight") ){
             return wrap( new RoundRectGraphicsOperation() )
         }else if( properties.containsKey("raised") ){
             return wrap( new Rect3DGraphicsOperation() )
         }else{
             return wrap( new RectGraphicsOperation() )
         }
     }
}