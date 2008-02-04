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

import com.jhlabs.image.MapFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class MapCoordinatesFilterProvider extends AbstractTransformFilterProvider {
   public static required = ['xMapFunction','yMapFunction']

   def xMapFunction
   def yMapFunction

   MapCoordinatesFilterProvider() {
      super( "mapCoordinates" )
      filter = new MapFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "xMapFunction":
         case "yMapFunction":
            return FilterUtils.getFunction2D(value)
         default:
            return super.convertValue(property,value)
      }
   }
}