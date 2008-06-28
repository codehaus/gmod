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
import java.awt.Dimension
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class FilterUtils  {
   static getAngle( value ){
      return Math.toRadians(value)
   }

   static getImage( value ){
      // TODO handle image and shape operations
      return value
   }

   static getDimension( value ){
      if( value instanceof Dimension ){
         return value
      }else if( value instanceof Map ){
         value.width = value.width ? value.width : 0
         value.height = value.height ? value.height : 0
         return value as Dimension
      }else if( value instanceof List && value.size() == 2 ){
         return value as Dimension
      }
      return null
   }

   static getPoint2D( value ){
      if( value instanceof Point2D ){
         return value
      }else if( value instanceof Map ){
         value.x = value.x ? value.x : 0
         value.y = value.y ? value.y : 0
         return value as Point2D.Double
      }else if( value instanceof List && value.size() == 2 ){
         return value as Point2D.Double
      }
      return null
   }
}
