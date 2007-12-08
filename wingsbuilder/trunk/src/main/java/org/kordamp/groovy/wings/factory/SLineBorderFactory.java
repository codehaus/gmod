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

import java.awt.Color;
import java.util.Map;

import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.border.SLineBorder;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SLineBorderFactory extends WingSBorderFactory {
   private final String style;

   public SLineBorderFactory( String style ) {
      this.style = style;
   }

   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map attributes )
         throws InstantiationException, IllegalAccessException {
      Map context = builder.getContext();
      context.put( "applyBorderToParent", attributes.remove( "parent" ) );

      Color color = (Color) attributes.remove( "color" );
      if( color == null ){
         throw new RuntimeException( "color: is a required attribute for " + name );
      }
      Number thickness = (Number) attributes.remove( "thickness" );
      if( thickness == null ){
         thickness = new Integer( 1 );
      }
      if( !attributes.isEmpty() ){
         throw new RuntimeException( name
               + "does not know how to handle the remaining attibutes: ${attributes.keySet()}" );
      }

      SLineBorder lineBorder = new SLineBorder( color, thickness.intValue() );
      lineBorder.setBorderStyle( style );
      return lineBorder;
   }
}