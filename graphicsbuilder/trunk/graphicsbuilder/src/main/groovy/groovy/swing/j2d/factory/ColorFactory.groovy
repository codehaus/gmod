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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.operations.ColorGraphicsOperation
import groovy.util.FactoryBuilderSupport

import java.awt.Color

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ColorFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
            Map properties ) throws InstantiationException, IllegalAccessException {
        ColorGraphicsOperation go = new ColorGraphicsOperation()
        if( value != null ){
            if( Color.class.isAssignableFrom( value.getClass() ) ){
                go.color = value
            }else{
                go.color = ColorCache.getInstance().getColor( value )
            }
        }else if( properties.containsKey( "red" ) && properties.containsKey( "green" )
                && properties.containsKey( "blue" ) ){
            Number red = properties.remove( "red" )
            Number green = properties.remove( "green" )
            Number blue = properties.remove( "blue" )
            Number alpha = properties.remove( "alpha" )

            if( red > 1 || green > 1 || blue > 1 ){
                if( alpha != null ){
                    value = new Color( red as int, green as int, blue as int, alpha as int )
                }else{
                    value = new Color( red as int, green as int, blue as int )
                }
            }else{
                if( alpha != null ){
                    value = new Color( red as float, green as float, blue as float, alpha as float )
                }else{
                    value = new Color( red as float, green as float, blue as float )
                }
            }
            go.color = value
        }
        return go
    }

    public boolean isLeaf(){
        return true
    }
}