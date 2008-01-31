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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider
import com.jhlabs.image.LightFilterimport com.jhlabs.image.LightFilter
import java.awt.Color
import com.jhlabs.image.LightFilter
import com.jhlabs.image.LightFilter.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractLightFilterProvider extends PropertiesBasedFilterProvider {
   public static optional = super.optional + ['bumpHeight','bumpSoftness','bumpShape',
                                              'viewDistance','material','colorSource','bumpSource',
                                              'bumpFunction','environmentMap','diffuseColor']

   def bumpHeight
   def bumpSoftness
   def bumpShape
   def viewDistance
   def material
   def colorSource
   def bumpSource
   def bumpFunction
   def environmentMap
   def diffuseColor

   AbstractLightFilterProvider( String name ) {
      super( name )
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "bumpHeight":
         case "bumpSoftness":
         case "viewDistance":
            return value as float
         case "bumpShape":
            return value as int
         case "colorSource":
            return getColorSource(value)
         case "bumpSource":
            return getBumpSource(value)
         case "diffuseColor":
            return FilterUtils.getColor(value)
         default:
            return super.convertValue(property,value)
      }
   }

   public void addLight( Light light ){
      filter.addLight( light )
   }

   public void removeLight( Light light ){
      filter.removeLight( light )
   }

   private def getColorSource( value ){
      if( value instanceof Number ){
         return value.intValue()
      }
      if( value instanceof String ){
         switch( value ){
            case 'image': return LightFilter.COLORS_FROM_IMAGE
            case 'constant': return LightFilter.COLORS_FROM_IMAGE
         }
      }
      throw new IllegalArgumentException("Invalid value for ${this}.colorSource")
   }

   private def getBumpSource( value ){
      if( value instanceof Number ){
         return value.intValue()
      }
      if( value instanceof String ){
         switch( value ){
            case 'image':return LightFilter.BUMPS_FROM_IMAGE
            case 'alpha': return LightFilter.BUMPS_FROM_IMAGE_ALPHA
            case 'map': return LightFilter.BUMPS_FROM_MAP
            case 'bevel': return LightFilter.BUMPS_FROM_BEVEL
         }
      }
      throw new IllegalArgumentException("Invalid value for ${this}.bumpSource")
   }
}