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

package groovy.swing.j2d.operations.filters.colors

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.OpacityFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class TransparencyFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['opacity']

   def opacity

   TransparencyFilterProvider() {
      super( "transparency" )
      filter = new OpacityFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "opacity":
            if( opacity instanceof Number ){
               def f = value.floatValue()
               def i = value.intValue()
               if( 0 <= f && f <= 1 ){
                  return Math.abs( 255 * value )
               }else{
                  return i > 255 ? 255: i
               }
            }
         default:
            return super.convertValue(property,value)
      }
   }
}