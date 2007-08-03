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

import java.net.URL;
import java.util.Map;

import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.SForm;
import org.wings.SLayoutManager;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SFormFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingSBuilder.checkValueIsNull( value, name );
      SForm form = null;
      URL action = (URL) properties.remove( "action" );
      SLayoutManager layout = (SLayoutManager) properties.remove( "layout" );

      if( action != null ){
         form = new SForm( action );
      }else if( layout != null ){
         form = new SForm( layout );
      }else{
         form = new SForm();
      }
      builder.addFormToHierarchy( form );

      return form;
   }
}