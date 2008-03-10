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

package groovy.swing.j2d.operations.filters

import groovy.swing.j2d.GraphicsContext

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractMotionBlurFilterProvider extends PropertiesBasedFilterProvider {
   public static optional = PropertiesBasedFilterProvider.optional + ['distance','angle','zoom','centreX','centreY','rotation']

   def distance
   def angle
   def zoom
   def centreX
   def centreY
   def rotation

   AbstractMotionBlurFilterProvider( String name ) {
      super( name )
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "distance":
         case "zoom":
         case "centreX":
         case "centreY":
            return value as float
         case "angle":
         case "rotation":
            return FilterUtils.getAngle(value)
         default:
            return super.convertValue(property,value)
      }
   }
}