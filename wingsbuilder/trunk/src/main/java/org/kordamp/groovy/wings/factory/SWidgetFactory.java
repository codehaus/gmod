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

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SWidgetFactory extends AbstractWingSFactory {
   protected boolean leaf;
   private Class restrictedType;

   public SWidgetFactory( Class restrictedType, boolean leaf ) {
      this.restrictedType = restrictedType;
      this.leaf = leaf;
   }

   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      if( value == null ){
         value = properties.remove( name );
      }
      if( (value != null) && WingSBuilder.checkValueIsType( value, name, restrictedType ) ){
         return value;
      }else{
         throw new RuntimeException( name
               + " must have either a value argument or an attribute named " + name
               + " that must be of type " + restrictedType.getName() );
      }
   }

   public boolean isLeaf() {
      return leaf;
   }
}