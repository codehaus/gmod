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

package groovy.swing.j2d.operations.filters.transform

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.JHlabsFilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.FlipFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class FlipFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['operation']

   def operation

   FlipFilterProvider() {
      super( "flip" )
      filter = new FlipFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "operation":
            return getOperation(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private def getOperation( value ){
      if( value instanceof Number ){
         return value.intValue()
      }else if( value instanceof String ){
         switch( value ){
            case "h":
            case "horizontal":
               return FlipFilter.FLIP_H
            case "v":
            case "vertical":
               return FlipFilter.FLIP_V
            case "hv":
            case "mixed":
               return FlipFilter.FLIP_HV
            case "90cw":
               return FlipFilter.FLIP_90CW
            case "90ccw":
               return FlipFilter.FLIP_90CCW
            case "180":
               return FlipFilter.FLIP_180
         }
      }
      throw new IllegalArgumentException("Invalid value for ${this}.operation")
   }
}