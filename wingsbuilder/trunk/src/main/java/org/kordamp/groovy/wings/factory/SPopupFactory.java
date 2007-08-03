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
import org.wings.SPopup;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SPopupFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingSBuilder.checkValueIsNull( value, name );
      SPopup popup = null;
      SComponent owner = (SComponent) properties.remove( "owner" );
      SComponent contents = (SComponent) properties.remove( "contents" );
      Integer x = (Integer) properties.remove( "x" );
      Integer y = (Integer) properties.remove( "y" );

      popup = new SPopup( owner, contents, x == null ? 0 : x.intValue(), y == null ? 0
            : y.intValue() );

      return popup;
   }
}