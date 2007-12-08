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

package org.kordamp.groovy.wings.factory;

import java.util.List;
import java.util.Map;

import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.SBorderFactory;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SEmptyBorderFactory extends WingSBorderFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map attributes )
         throws InstantiationException, IllegalAccessException {
      Map context = builder.getContext();
      context.put( "applyBorderToParent", attributes.remove( "parent" ) );

      if( attributes.isEmpty() ){
         if( value instanceof Integer ){
            int val = ((Number) value).intValue();
            return SBorderFactory.createSEmptyBorder( val, val, val, val );
         }else if( value instanceof List && ((List) value).size() == 4 ){
            // assume that all elements are numbers
            List list = (List) value;
            return SBorderFactory.createSEmptyBorder( ((Number) list.get( 0 )).intValue(),
                  ((Number) list.get( 1 )).intValue(), ((Number) list.get( 2 )).intValue(),
                  ((Number) list.get( 3 )).intValue() );
         }
         throw new RuntimeException( name
               + " only accepts a single integer or an array of four integers as a value argument" );
      }
      if( value == null ){
         if( attributes.isEmpty() ){
            return SBorderFactory.createSEmptyBorder();
         }
         Number top = (Number) attributes.remove( "top" );
         Number left = (Number) attributes.remove( "left" );
         Number bottom = (Number) attributes.remove( "bottom" );
         Number right = (Number) attributes.remove( "right" );
         if( (top == null) || (top == null) || (top == null) || (top == null)
               || !attributes.isEmpty() ){
            throw new RuntimeException(
                  "When "
                        + name
                        + " is called it must be called with top:, left:, bottom:, right:, and no other attributes" );
         }
         return SBorderFactory.createSEmptyBorder( top.intValue(), left.intValue(),
               bottom.intValue(), right.intValue() );
      }
      throw new RuntimeException( name
            + " cannot be called with both an argument value and attributes" );
   }
}