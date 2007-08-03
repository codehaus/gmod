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

package org.kordamp.groovy.wings.factory;

import java.util.Map;

import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.SComponent;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SWidgetFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      if( WingSBuilder.checkValueIsType( value, name, SComponent.class ) ){
         return value;
      }else if( properties.get( name ) instanceof SComponent ){
         return properties.remove( name );
      }else{
         throw new RuntimeException( name
               + " must have a value argument of type org.wings.SComponent or a property named "
               + name + " of type java.awt.Component" );
      }
   }
}