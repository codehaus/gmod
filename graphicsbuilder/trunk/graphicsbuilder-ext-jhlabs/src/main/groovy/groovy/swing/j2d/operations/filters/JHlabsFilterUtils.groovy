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

package groovy.swing.j2d.operations.filters

import groovy.swing.j2d.ColorCache
import java.awt.Color
import java.awt.image.BufferedImage
import com.jhlabs.image.PixelUtils
import com.jhlabs.math.Function1D
import com.jhlabs.math.Function2D
import com.jhlabs.math.Function3D

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class JHlabsFilterUtils extends FilterUtils {
   static getColor( value ){
      if( value instanceof String || value instanceof Color ){
         return ColorCache.getInstance().getColor(value).rgb()
      }
      return value as int
   }

   static getPixelOperation( value ){
      if( value instanceof Number ){
         return value.intValue()
      }
      switch( value ){
         case "pixelReplace":
            return PixelUtils.REPLACE
         case "pixelNormal":
            return PixelUtils.NORMAL
         case "pixelMon":
            return PixelUtils.MIN
         case "pixelMax":
            return PixelUtils.MAX
         case "pixelAdd":
            return PixelUtils.ADD
         case "pixelSubtract":
            return PixelUtils.SUBTRACT
         case "pixelDifference":
            return PixelUtils.DIFFERENCE
         case "pixelMultiply":
            return PixelUtils.MULTIPLY
         case "pixelHue":
            return PixelUtils.HUE
         case "pixelSaturation":
            return PixelUtils.SATURATION
         case "pixelValue":
            return PixelUtils.VALUE
         case "pixelColor":
            return PixelUtils.COLOR
         case "pixelScreen":
            return PixelUtils.SCREEN
         case "pixelAverage":
            return PixelUtils.AVERAGE
         case "pixelOverlay":
            return PixelUtils.OVERLAY
         case "pixelClear":
            return PixelUtils.CLEAR
         case "pixelExchange":
            return PixelUtils.EXCHANGE
         case "pixelDissolve":
            return PixelUtils.DISSOLVE
         case "pixelDstIn":
            return PixelUtils.DST_IN
         case "pixelAlpha":
            return PixelUtils.ALPHA
         case "pixelAlphaToGray":
            return PixelUtils.ALPHA_TO_GRAY
         default: return 0
      }
   }

   static getFunction1D( value ){
      if( value instanceof Function1D ){
         return value
      }else if( value instanceof Closure ){
         return value as Function1D
      }
      return null
   }

   static getFunction2D( value ){
      if( value instanceof Function2D ){
         return value
      }else if( value instanceof Closure ){
         return value as Function2D
      }
      return null
   }

   static getFunction3D( value ){
      if( value instanceof Function3D ){
         return value
      }else if( value instanceof Closure ){
         return value as Function3D
      }
      return null
   }
}
