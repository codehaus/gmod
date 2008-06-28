/*
 * Copyright 2007-2008 the original author or authors.
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

import com.jhlabs.image.DitherFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class DitherFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['matrix','colorDither','levels']

   def matrix
   def levels
   def colorDither

   DitherFilterProvider() {
      super( "dither" )
      filter = new DitherFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "colorDither":
            return value as boolean
         case "matrix":
            return getMatrix(value)
         case "levels":
            return value as int
         default:
            return super.convertValue(property,value)
      }
   }

   private def getMatrix( value ){
      if( value instanceof int[] ){
         return value
      }else if( value instanceof List ){
         return value as int[]
      }else if( value instanceof String ){
         return DitherFilter."$value"
      }
   }
}