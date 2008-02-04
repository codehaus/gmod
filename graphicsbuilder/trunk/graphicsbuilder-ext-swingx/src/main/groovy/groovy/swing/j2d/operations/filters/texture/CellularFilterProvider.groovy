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
import com.jhlabs.image.CellularFilter
import com.jhlabs.image.Colormap
import com.jhlabs.image.CellularFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class CellularFilterProvider extends PropertiesBasedFilterProvider implements ColormapAware {
   public static required = ['scale','stretch','angleCoefficient','gradientCoefficient',
                             'f1','f2','f3','f4','randomness','distancePower','turbulence',
                             'amount','gridType','angle','colormap']

   def scale
   def stretch
   def angleCoefficient
   def gradientCoefficient
   def f1
   def f2
   def f3
   def f4
   def randomness
   def distancePower
   def turbulence
   def amount
   def gridType
   def angle
   Colormap colormap

   CellularFilterProvider() {
      super( "cellular" )
      filter = new CellularFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "scale":
         case "stretch":
         case "angleCoefficient":
         case "gradientCoefficient":
         case "f1":
         case "f2":
         case "f3":
         case "f4":
         case "randomness":
         case "distancePower":
         case "turbulence":
         case "amount":
            return value as float
         case "gridType":
            return getGridType(value)
         case "angle":
            return FilterUtils.getAngle(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private def getGridType( value ){
      if( value instanceof Number ){
         return value.intValue()
      }
      if( value instanceof String ){
         switch( value ){
            case "random":
            case "cellRandom":
               return CellularFilter.RANDOM
            case "square":
            case "cellSquare":
               return CellularFilter.SQUARE
            case "hexagonal":
            case "cellHexagonal":
               return CellularFilter.HEXAGONAL
            case "octagonal":
            case "cellOctagonal":
               return CellularFilter.OCTAGONAL
            case "triangular":
            case "cellTriangular":
               return CellularFilter.TRIANGULAR
         }
      }
      throw new IllegalArgumentException("${this}.gridType has an invalid value of '${value}'")
   }
}