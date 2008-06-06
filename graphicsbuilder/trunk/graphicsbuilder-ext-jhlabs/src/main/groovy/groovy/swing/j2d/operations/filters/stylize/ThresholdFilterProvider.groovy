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

import com.jhlabs.image.ThresholdFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ThresholdFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['lowerThreshold','upperThreshold','white','black','color1','color2']

   def lowerThreshold
   def upperThreshold
   def white
   def black
   def color1
   def color2

   ThresholdFilterProvider() {
      super( "threshold" )
      filter = new ThresholdFilter()
   }

   protected void setFilterProperty( name, value ){
      if( name == "color1" ){
         filter.white = convertValue( 'white', value )
      }else if( name == "color2" ){
         filter.white = convertValue( 'black', value )
      }else{
         super.setFilterProperty( name, value )
      }
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "white":
         case "black":
            return JHlabsFilterUtils.getColor(value)
         default:
            return super.convertValue(property,value)
      }
   }
}