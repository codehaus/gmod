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

package groovy.swing.j2d.operations.filters.distort

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.FilterUtils
import groovy.swing.j2d.operations.filters.AbstractTransformFilterProvider

import com.jhlabs.image.CurlFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class CurlFilterProvider extends AbstractTransformFilterProvider {
   public static required = ['angle','transition','width','height','radius']

   def angle
   def transition
   def width
   def height
   def radius

   CurlFilterProvider() {
      super( "curl" )
      filter = new CurlFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "transition":
         case "width":
         case "height":
         case "radius":
            return value as float
         case "angle":
            return FilterUtils.getAngle(value)
         default:
            return super.convertValue(property,value)
      }
   }
}