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

package groovy.swing.j2d.operations.filters.texture

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.WeaveFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class WeaveFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['xWidth','yWidth','xGap','yGap','useImageColors','roundThreads','shadeCrossings']

   def xWidth
   def yWidth
   def xGap
   def yGap
   def useImageColors
   def roundThreads
   def shadeCrossings

   WeaveFilterProvider() {
      super( "weave" )
      filter = new WeaveFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "xWidth":
         case "yWidth":
         case "xGap":
         case "yGap":
            return value as float
         default:
            return super.convertValue(property,value)
      }
   }
}