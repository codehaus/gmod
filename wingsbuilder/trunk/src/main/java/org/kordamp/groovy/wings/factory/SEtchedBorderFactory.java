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

import java.util.Map;

import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.SBorderFactory;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SEtchedBorderFactory extends WingSBorderFactory {
   private final int type;

   public SEtchedBorderFactory( int newType ) {
      type = newType;
   }

   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map attributes )
         throws InstantiationException, IllegalAccessException {
      Map context = builder.getContext();
      context.put( "applyBorderToParent", attributes.remove( "parent" ) );
      // no if-else-if chain so that we can have one attribute failure block
      /*
      if( attributes.containsKey( "highlight" ) ){
         Color highlight = (Color) attributes.remove( "highlight" );
         Color shadow = (Color) attributes.remove( "shadow" );
         if( highlight != null && shadow != null && attributes.isEmpty() ){
            return SBorderFactory.createSEtchedBorder( type, highlight, shadow );
         }
      }
      */
      if( !attributes.isEmpty() ){
         throw new RuntimeException( name
               + "only accepts no attributes, or highlight: and shadow: attributes" );
      }
      return SBorderFactory.createSEtchedBorder( type );
   }
}