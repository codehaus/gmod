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
public class SBevelBorderFactory extends WingSBorderFactory {
   private final int type;

   public SBevelBorderFactory( int newType ) {
      type = newType;
   }

   public Object doNewInstance( WingSBuilder builder, Object name, Object value,
         Map attributes ) throws InstantiationException, IllegalAccessException{
      Map context = builder.getContext();
      context.put( "applyBorderToParent", attributes.remove( "parent" ) );

      // no if-else-if chain so that we can have one attribute failure block
      /*
      if( attributes.containsKey( "highlight" ) ){
         Color highlight = attributes.remove( "highlight" );
         Color shadow = attributes.remove( "shadow" );
         if( highlight && shadow && !attributes ){
            return SBorderFactory.createSBevelBorder( type, highlight, shadow );
         }
      }
      if( attributes.containsKey( "highlightOuter" ) ){
         Color highlightOuter = attributes.remove( "highlightOuter" );
         Color highlightInner = attributes.remove( "highlightInner" );
         Color shadowOuter = attributes.remove( "shadowOuter" );
         Color shadowInner = attributes.remove( "shadowInner" );
         if( highlightOuter && highlightInner && shadowOuter && shadowInner && !attributes ){
            return SBorderFactory.createSBevelBorder( type, highlightOuter, highlightInner,
                  shadowOuter, shadowInner );
         }
      }
      */
      if( !attributes.isEmpty() ){
         throw new RuntimeException(
               name
                     + " only accepts no attributes, or highlight: and shadow: attributes, or highlightOuter: and highlightInner: and shadowOuter: and shadowInner: attributes" );
      }
      return SBorderFactory.createSBevelBorder( type );
   }
}