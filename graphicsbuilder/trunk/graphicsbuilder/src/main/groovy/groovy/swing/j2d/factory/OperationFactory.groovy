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

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class OperationFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
            Map properties ) throws InstantiationException, IllegalAccessException {
        if( FactoryBuilderSupport.checkValueIsType( value, name, GraphicsOperation ) ){
            return value
        }else if( properties.get( name ) instanceof GraphicsOperation ){
            return properties.remove( name )
        }else{
            throw new RuntimeException( "'${name}' must have a value argument of "
                    + "${GraphicsOperation.class.name} or a property named '${name}'"
                    + " of type ${GraphicsOperation.class.name}" )
        }
    }

    public boolean isLeaf() {
       true
    }
}