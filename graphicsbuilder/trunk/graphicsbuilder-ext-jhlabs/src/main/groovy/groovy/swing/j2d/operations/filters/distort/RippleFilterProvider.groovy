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

package groovy.swing.j2d.operations.filters.distort

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.AbstractTransformFilterProvider

import com.jhlabs.image.RippleFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class RippleFilterProvider extends AbstractTransformFilterProvider {
   public static required = ['xAmplitude','yAmplitude','xWavelength','yWavelength','waveType']

   def xAmplitude
   def yAmplitude
   def xWavelength
   def yWavelength
   def waveType

   RippleFilterProvider() {
      super( "ripple" )
      filter = new RippleFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "xAmplitude":
         case "yAmplitude":
         case "xWavelength":
         case "yWavelength":
            return value as float
         case "waveType":
            return getWaveType(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private def getWaveType( value ){
      if( value instanceof Number ){
         return value.intValue()
      }
      if( value instanceof String ){
         switch( waveType ){
            case "sine":
            case "rippleSine":
               return RippleFilter.SINE
            case "sawtooth":
            case "rippleSawtooth":
               return RippleFilter.SAWTOOTH
            case "triangle":
            case "rippleTriangle":
               return RippleFilter.TRIANGLE
            case "noise":
            case "rippleNoise":
               return RippleFilter.NOISE
         }
      }
      throw new IllegalArgumentException("Invalid value for ${this}.waveType")
   }
}