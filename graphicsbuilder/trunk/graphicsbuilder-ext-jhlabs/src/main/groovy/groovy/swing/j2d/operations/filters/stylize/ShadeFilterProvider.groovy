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

import com.jhlabs.image.ShadeFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShadeFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['bumpFunction','bumpHeight','bumpHeight','environmentMap','bumpSource']

   def bumpFunction
   def bumpHeight
   def bumpSoftness
   def environmentMap
   def bumpSource

   ShadeFilterProvider() {
      super( "shade" )
      filter = new ShadeFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "bumpFunction":
            return JHlabsFilterUtils.getFunction2D(value)
         case "bumpSource":
            return getBumpSource(value)
         case "environmentMap":
            return JHlabsFilterUtils.getImage(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private def getBumpSource( value ){
      if( value instanceof Number ){
         return value.intValue()
      }
      if( value instanceof String ){
         switch( value ){
            case 'bumpsFromImage':return ShadeFilter.BUMPS_FROM_IMAGE
            case 'bumpsFromImageAlpha': return ShadeFilter.BUMPS_FROM_IMAGE_ALPHA
            case 'bumpsFromMap': return ShadeFilter.BUMPS_FROM_MAP
            case 'bumpsFromBevel': return ShadeFilter.BUMPS_FROM_BEVEL
         }
      }
      throw new IllegalArgumentException("Invalid value for ${this}.bumpSource")
   }
}