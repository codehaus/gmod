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

package groovy.swing.j2d.operations.filters.effects

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.GlintFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GlintFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['threshold','blur','amount','length','glintOnly','colormap']

   def threshold
   def blur
   def amount
   def length
   def glintOnly
   def colormap

   GlintFilterProvider() {
      super( "glint" )
      filter = new GlintFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "threshold":
         case "blur":
         case "amount":
            return value as float
         case "length":
            return value as int
         case "glintOnly":
            return value as boolean
         default:
            return super.convertValue(property,value)
      }
   }
}