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

import org.jvnet.substance.api.SubstanceColorScheme
import org.jvnet.substance.colorscheme.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ColorSchemeFactory extends AbstractGraphicsOperationFactory {    
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
            Map properties ) throws InstantiationException, IllegalAccessException {
        if( value instanceof SubstanceColorScheme ) return value
        if( value instanceof String ) return parseColorScheme(value)
        return new LightGrayColorScheme()
    }

    public boolean isLeaf() {
        true
    }

    private SubstanceColorScheme parseColorScheme( value ) {
         value = camelCaseConverter(value)
         value = value[0].toUpperCase() + value[1..-1]
         def clazz = (value + "ColorScheme") as Class
         clazz.newInstance()
    }

    def camelCaseConverter( orig ) {
        if( !(orig =~ /[_\s]/) ) return orig
        orig.toLowerCase().replaceAll(/[_\s](\w)?/) { wholeMatch, firstLetter ->
            firstLetter?.toUpperCase() ?: ""
        }
    }
}