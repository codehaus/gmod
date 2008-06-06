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

package groovy.swing.j2d.operations.filters.render

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.JHlabsFilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.ScratchFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ScratchFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['angle','angleVariation','density','width','length','color','seed']

   def angle
   def angleVariation
   def density
   def width
   def length
   def color
   def seed

   ScratchFilterProvider() {
      super( "scratch" )
      filter = new ScratchFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "color":
            return JHlabsFilterUtils.getColor(value)
         case "angle":
            return JHlabsFilterUtils.getAngle(value)
         default:
            return super.convertValue(property,value)
      }
   }
}