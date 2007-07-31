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
 * limitations under the License.
 */

package groovy.wings.factory;

import groovy.wings.WingXBuilder;

import java.awt.Color;
import java.util.Map;

import org.wingx.XColorPicker;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class XColorPickerFactory extends AbstractWingXFactory {
   public Object doNewInstance( WingXBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingXBuilder.checkValueIsNull( value, name );
      XColorPicker colorPicker = null;
      Number red = (Number) properties.remove( "red" );
      Number green = (Number) properties.remove( "green" );
      Number blue = (Number) properties.remove( "blue" );
      Color color = (Color) properties.remove( "color" );

      if( red != null && green != null && blue != null ){
         colorPicker = new XColorPicker( red.intValue(), green.intValue(), blue.intValue() );
      }else if( color != null ){
         colorPicker = new XColorPicker( color.getRed(), color.getGreen(), color.getBlue() );
      }else{
         colorPicker = new XColorPicker();
      }

      return colorPicker;
   }
}