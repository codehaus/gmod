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

package groovy.swing.j2d.operations.filters.stylize

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.JHlabsFilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.ContourFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ContourFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['levels','scale','offset','contourColor']

   def levels
   def scale
   def offset
   def contourColor

   ContourFilterProvider() {
      super( "contour" )
      filter = new ContourFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "contourColor":
            return JHlabsFilterUtils.getColor(value)
         case "levels":
         case "scale":
         case "offset":
            return value as float
         default:
            return super.convertValue(property,value)
      }
   }
}