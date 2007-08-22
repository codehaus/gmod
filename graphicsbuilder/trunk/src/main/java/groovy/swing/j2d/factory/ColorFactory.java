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

package groovy.swing.j2d.factory;

import groovy.swing.j2d.GraphicsBuilder;
import groovy.swing.j2d.operations.ColorGraphicsOperation;
import groovy.util.FactoryBuilderSupport;

import java.awt.Color;
import java.util.Map;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ColorFactory extends AbstractGraphicsOperationFactory {
   public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
         Map properties ) throws InstantiationException, IllegalAccessException {
      ColorGraphicsOperation go = new ColorGraphicsOperation();
      if( value != null ){
         if( Color.class.isAssignableFrom( value.getClass() ) ){
            go.setProperty( "color", value );
         }else{
            value = GraphicsBuilder.getColor( value );
            go.setProperty( "color", value );
         }
      }else if( properties.containsKey( "red" ) && properties.containsKey( "green" )
            && properties.containsKey( "blue" ) ){
         Number red = (Number) properties.remove( "red" );
         Number green = (Number) properties.remove( "green" );
         Number blue = (Number) properties.remove( "blue" );
         Number alpha = (Number) properties.remove( "alpha" );

         if( red.intValue() > 1 || green.intValue() > 1 || blue.intValue() > 1 ){
            if( alpha != null ){
               value = new Color( red.intValue(), green.intValue(), blue.intValue(),
                     alpha.intValue() );
            }else{
               value = new Color( red.intValue(), green.intValue(), blue.intValue() );
            }
         }else{
            if( alpha != null ){
               value = new Color( red.floatValue(), green.floatValue(), blue.floatValue(),
                     alpha.floatValue() );
            }else{
               value = new Color( red.floatValue(), green.floatValue(), blue.floatValue() );
            }
         }
         go.setProperty( "color", value );
      }
      return go;
   }
}