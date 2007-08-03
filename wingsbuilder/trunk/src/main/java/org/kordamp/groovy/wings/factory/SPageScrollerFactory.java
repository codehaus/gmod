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

import java.awt.Adjustable;
import java.util.Map;

import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.SPageScroller;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SPageScrollerFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingSBuilder.checkValueIsNull( value, name );
      SPageScroller scroller = null;
      Integer orientation = (Integer) properties.remove( "orientation" );
      Integer svalue = (Integer) properties.remove( "value" );
      Integer extent = (Integer) properties.remove( "extent" );
      Integer min = (Integer) properties.remove( "min" );
      Integer max = (Integer) properties.remove( "max" );

      scroller = new SPageScroller( orientation == null ? Adjustable.HORIZONTAL
            : orientation.intValue(), svalue == null ? 0 : svalue.intValue(), extent == null ? 10
            : extent.intValue(), min == null ? 0 : min.intValue(), max == null ? 100
            : max.intValue() );

      return scroller;
   }
}