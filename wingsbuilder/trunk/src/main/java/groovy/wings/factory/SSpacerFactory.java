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

import groovy.wings.WingSBuilder;

import java.util.Map;



import org.wings.SSpacer;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SSpacerFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingSBuilder.checkValueIsNull( value, name );
      Object width = properties.remove( "width" );
      Object height = properties.remove( "height" );

      SSpacer spacer = null;
      if( width instanceof Number && height instanceof Number ){
         spacer = new SSpacer( ((Number) width).intValue(), ((Number) height).intValue() );
      }else if( width instanceof String && height instanceof String ){
         spacer = new SSpacer( (String) width, (String) height );
      }

      return spacer;
   }
}