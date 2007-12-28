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

import java.awt.Color

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ColorFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
          Map properties ) throws InstantiationException, IllegalAccessException {
        if( value != null ){
            if( Color.class.isAssignableFrom( value.getClass() ) ){
                return value
            }else{
                return ColorCache.getInstance().getColor( value )
            }
        }

        def red = properties.remove( "red" )
        def green = properties.remove( "green" )
        def blue = properties.remove( "blue" )
        def alpha = properties.remove( "alpha" )

        red = red != null ? red : 0
        green = green != null ? green : 0
        blue = blue != null ? blue : 0
        alpha = alpha != null ? alpha : 255

        if( red > 1 || green > 1 || blue > 1 ){
           return new Color( red as int, green as int, blue as int, alpha as int )
        }else{
           if( alpha > 1 ) alpha = 1
           return new Color( red as float, green as float, blue as float, alpha as float )
        }
    }

    public void setParent( FactoryBuilderSupport builder, Object parent, Object child ){
       // empty
    }

    public boolean isLeaf(){
        return true
    }
}