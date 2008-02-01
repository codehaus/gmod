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

package groovy.swing.j2d.operations.filters.blur

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.EdgeFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class DetectEdgesFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['vEdgeMatrix','hEdgeMatrix']

   def vEdgeMatrix
   def hEdgeMatrix

   DetectEdgesFilterProvider() {
      super( "detectEdges" )
      filter = new EdgeFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "vEdgeMatrix":
         case "hEdgeMatrix":
            return getMatrix(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private def getMatrix( value ){
      if( value instanceof int[] ){
         return value
      }else if( value instanceof List ){
         return value as int[]
      }else if( value instanceof String ){
         value = value.toUpperCase().replaceAll(" ","_")
         return EdgeFilter."$value"
      }
   }
}