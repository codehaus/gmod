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
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.NoiseFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class NoiseFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['amount','distribution','monochrome','density']

   def amount
   def distribution
   def monochrome
   def density

   NoiseFilterProvider() {
      super( "noise" )
      filter = new NoiseFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "amount":
            return value as int
         case "density":
            return value as float
         case "monochrome":
            return value as boolean
         case "distribution":
            return getDistributionValue(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private int getDistributionValue( value ){
      if( value instanceof Number ){
         return value as int
      }
      if( value instanceof String ){
         switch( value ){
            case "gaussian":
            case "noiseGaussian":
               return NoiseFilter.GAUSSIAN
            case "uniform":
            case "noiseUniform":
               return NoiseFilter.UNIFORM
         }
         throw new IllegalArgumentException("noise.distribution has an invalid value of '${value}'")
      }
   }
}