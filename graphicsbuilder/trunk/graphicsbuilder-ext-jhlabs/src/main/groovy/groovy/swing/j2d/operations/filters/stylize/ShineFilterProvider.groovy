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

package groovy.swing.j2d.operations.filters.stylize

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.JHlabsFilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.ShineFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShineFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['radius','angle','distance','bevel','shineColor',
                             'shadowOnly','brightness','softness']

   def radius
   def angle
   def distance
   def bevel
   def shineColor
   def shadowOnly
   def brightness
   def softness

   ShineFilterProvider() {
      super( "shine" )
      filter = new ShineFilter()
   }

   protected void setFilterProperty( name, value ){
      if( name == "dimensions" ){
         def dimension = JHlabsFilterUtils.getDimension(value)
         filter.setDimensions( dimension.width as int, dimension.height as int )
      }else{
         super.setFilterProperty( name, value )
      }
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "angle":
               return JHlabsFilterUtils.getColor(value)
         case "shineColor":
            return JHlabsFilterUtils.getColor(value)
         default:
            return super.convertValue(property,value)
      }
   }
}