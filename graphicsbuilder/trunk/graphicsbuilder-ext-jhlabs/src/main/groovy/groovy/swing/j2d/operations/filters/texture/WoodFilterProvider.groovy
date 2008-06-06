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
import groovy.swing.j2d.operations.filters.JHlabsFilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.Colormap
import com.jhlabs.image.WoodFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class WoodFilterProvider extends PropertiesBasedFilterProvider implements ColormapAware {
   public static required = ['scale','stretch','angle','rings','turbulence','fibres','gain','colormap']

   def scale
   def stretch
   def angle
   def rings
   def turbulence
   def fibres
   def gain
   Colormap colormap

   WoodFilterProvider() {
      super( "wood" )
      filter = new WoodFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "scale":
         case "stretch":
         case "rings":
         case "turbulence":
         case "fibres":
         case "gain":
            return value as float
         case "angle":
            return JHlabsFilterUtils.getAngle(value)
         default:
            return super.convertValue(property,value)
      }
   }
}