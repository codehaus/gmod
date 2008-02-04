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

package groovy.swing.j2d.operations.filters.blur

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.FilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.MotionBlurFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class MotionBlurFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['angle','distance','zoom','rotation','wrapEdges','premultiplyAlpha']

   def angle
   def distance
   def zoom
   def rotation
   def wrapEdges
   def premultiplyAlpha

   MotionBlurFilterProvider() {
      super( "motionBlur" )
      filter = new MotionBlurFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "distance":
         case "zoom":
            return value as float
         case "angle":
         case "rotation":
            return FilterUtils.getAngle(value)
         case "wrapEdges":
         case "premultiplyAlpha":
            return value as boolean
         default:
            return super.convertValue(property,value)
      }
   }
}