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

package groovy.swing.j2d.operations.filters

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import java.awt.Color
import com.jhlabs.image.TransformFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractTransformFilterProvider extends PropertiesBasedFilterProvider {
   public static optional = PropertiesBasedFilterProvider.optional + ['edgeAction','interpolation']

   def edgeAction
   def interpolation

   AbstractTransformFilterProvider( String name ) {
      super( name )
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "edgeAction":
            return getEdgeAction(value)
         case "interpolation":
            return getInterpolation(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private def getEdgeAction( value ){
      if( value instanceof Number ){
         return value.intValue()
      }
      if( value instanceof String ){
         switch( value ){
            case 'zero':
            case 'edgeActionZero':
               return TransformFilter.ZERO
            case 'clamp':
            case 'edgeActionClamp':
               return TransformFilter.CLAMP
            case 'wrap':
            case 'edgeActionWrap':
               return TransformFilter.WRAP
         }
      }
      throw new IllegalArgumentException("Invalid value for ${this}.edgeAction")
   }

   private def getInterpolation( value ){
      if( value instanceof Number ){
         return value.intValue()
      }
      if( value instanceof String ){
         switch( value ){
            case 'nearest':
            case 'interpolationNearest':
               return TransformFilter.NEAREST_NEIGHBOUR
            case 'bilinear':
            case 'interpolationBilinear':
               return TransformFilter.BILINEAR
         }
      }
      throw new IllegalArgumentException("Invalid value for ${this}.interpolation")
   }
}