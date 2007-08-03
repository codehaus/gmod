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

import groovy.swing.SwingBuilder;
import groovy.swing.factory.TextArgWidgetFactory;

import java.util.Map;

import org.kordamp.groovy.wings.WingSBuilder;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class STextArgWidgetFactory extends TextArgWidgetFactory {
   public STextArgWidgetFactory( Class klass ) {
      super( klass );
   }

   public Object newInstance( SwingBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      if( !(builder instanceof WingSBuilder) ){
         throw new RuntimeException( "This factory must be registered to a WingSBuilder" );
      }
      return super.newInstance( builder, name, value, properties );
   }
}