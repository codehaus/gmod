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
import com.jhlabs.image.FBMFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class FBMFilterProvider extends PropertiesBasedFilterProvider implements ColormapAware {
   public static required = ['amount','operation','scale','strecth','angle',
                             'octaves','h','lacunarity','gain','bias','colormap',
                             'basisType','basis']

   def angle
   def amount
   def scale
   def strecth
   def octaves
   def h
   def lacunarity
   def gain
   def bias
   def operation
   Colormap colormap
   def basisType
   def basis

   FBMFilterProvider() {
      super( "fractalBrownianMotion" )
      filter = new FBMFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "angle":
            return JHlabsFilterUtils.getAngle(value)
         case "amount":
         case "scale":
         case "strecth":
         case "octaves":
         case "h":
         case "lacunarity":
         case "gain":
         case "bias":
            return value as float
         case "operation":
            return JHlabsFilterUtils.getPixelOperation(value)
         case "basisType":
            return getBasisType(value)
         case "basis":
            return JHlabsFilterUtils.getFunction2D(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private def getBasisType( value ){
      if( value instanceof Number ){
         return value.intValue()
      }else if( value instanceof String ){
         switch( value ){
            case "cellular":
            case "basisCellular":
               return FBMFilter.CELLULAR
            case "noise":
            case "basisNoise":
               return FBMFilter.NOISE
            case "ridged":
            case "basisRidged":
               return FBMFilter.RIDGED
            case "scnoise":
            case "basisSCNoise":
               return FBMFilter.SCNOISE
            case "vlnoise":
            case "basisVLNoise":
               return FBMFilter.VLNOISE
         }
      }
      throw new IllegalArgumentException("Invalid value for ${this}.basisType")
   }
}