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

package groovy.swing.j2d.operations.filters.stylize

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import java.awt.image.BufferedImage
import com.jhlabs.image.ShapeFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShapeBurstFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['factor','colorMap','useAlpha','invert','merge','type']

   def factor = 1
   def colorMap
   def useAlpha = true
   def invert = false
   def merge = false
   def type = 'linear'

   ShapeBurstFilterProvider() {
      super( "shapeBurst" )
      filter = new ShapeFilter()
   }

   public BufferedImage filter( BufferedImage src, BufferedImage dst ){
      return filter.filter( src, dst )
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "type":
            return getTypeValue(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private int getTypeValue( value ){
      if( value instanceof Number ){
         return value as int
      }
      if( value instanceof String ){
         switch( value ){
            case "linear":
               return ShapeFilter.LINEAR
            case "up":
            case "circleup":
            case "circle up":
            case "circle_up":
               return ShapeFilter.CIRCLE_UP
            case "down":
            case "circledown":
            case "circle down":
            case "circle_down":
               return ShapeFilter.CIRCLE_DOWN
            case "smooth":
               return ShapeFilter.SMOOTH
         }
         throw new IllegalArgumentException("shapeBurst.type has an invalid value of '${value}'")
      }
   }
}