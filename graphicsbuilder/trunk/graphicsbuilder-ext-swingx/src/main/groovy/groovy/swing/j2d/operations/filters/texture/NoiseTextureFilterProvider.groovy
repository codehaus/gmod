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
import groovy.swing.j2d.operations.filters.ColormapAware
import groovy.swing.j2d.operations.filters.FilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.Colormap
import com.jhlabs.image.TextureFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class NoiseTextureFilterProvider extends PropertiesBasedFilterProvider implements ColormapAware {
   public static required = ['scale','stretch','angle','amount','turbulence','gain',
                             'bias','colormap','operation','function']

   def scale
   def stretch
   def angle
   def amount
   def turbulence
   def gain
   def bias
   def operation
   def function
   Colormap colormap

   NoiseTextureFilterProvider() {
      super( "noiseTexture" )
      filter = new TextureFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "operation":
            return FilterUtils.getPixelOperation(value)
         case "function":
            return FilterUtils.getFunction2D(value)
         case "angle":
            return FilterUtils.getAngle(value)
         default:
            return super.convertValue(property,value)
      }
   }
}