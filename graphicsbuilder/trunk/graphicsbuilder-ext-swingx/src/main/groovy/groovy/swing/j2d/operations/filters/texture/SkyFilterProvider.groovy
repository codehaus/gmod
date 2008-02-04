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
import groovy.swing.j2d.operations.filters.FilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.SkyFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SkyFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['amount','operation','scale','stretch','t','fov','cloudCover','cloudSharpness',
                             'time','glow','glowFalloff','angle','octaves','h','lacunarity','gain','bias',
                             'haziness','sunAzimuth','sunColor','cameraElevation','cameraAzimuth','windSpeed']

   def amount
   def operation
   def scale
   def stretch
   def t
   def fov
   def cloudCover
   def cloudSharpness
   def time
   def glow
   def glowFalloff
   def angle
   def octaves
   def h
   def lacunarity
   def gain
   def bias
   def haziness
   def sunElevation
   def sunAzimuth
   def sunColor
   def cameraElevation
   def cameraAzimuth
   def windSpeed

   SkyFilterProvider() {
      super( "sky" )
      filter = new SkyFilter()
   }

   protected void setFilterProperty( name, value ){
      if( name == "fov" ){
         filter.setFOV( convertValue( name, value ) )
      }else{
         super.setFilterProperty( name, value )
      }
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "sunColor":
            return FilterUtils.getColor(value)
         case "angle":
            return FilterUtils.getAngle(value)
         default:
            return super.convertValue(property,value)
      }
   }
}