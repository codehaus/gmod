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

package groovy.swing.j2d.operations.filters.texture

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.JHlabsFilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.CausticsFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class CausticsFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['scale','brightness','amount','turbulence','dispersion','time','samples','bgColor']

   def scale
   def brightness
   def amount
   def turbulence
   def dispersion
   def time
   def samples
   def bgColor

   CausticsFilterProvider() {
      super( "caustics" )
      filter = new CausticsFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "brightness":
         case "samples":
            return value as int
         case "scale":
         case "amount":
         case "turbulence":
         case "dispersion":
         case "time":
            return value as float
         case "bgColor":
            return JHlabsFilterUtils.getColor(value)
         default:
            return super.convertValue(property,value)
      }
   }
}