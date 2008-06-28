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

package groovy.swing.j2d.operations.filters.blur

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.LensBlurFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class LensBlurFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['radius','bloom','bloomThreshold','sides']

   def radius
   def bloom
   def bloomThreshold
   def sides

   LensBlurFilterProvider() {
      super( "lensBlur" )
      filter = new LensBlurFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "radius":
         case "bloom":
         case "bloomThreshold":
            return value as float
         case "sides":
            return value as int
         default:
            return super.convertValue(property,value)
      }
   }
}