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

package groovy.swing.j2d.operations.filters.transitions

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.JHlabsFilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.ShatterFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShatterFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['centreX','centreY','transition','rotation',
                             'zoom','startAlpha','endAlpha','iterations','tile']

   def centreX
   def centreY
   def transition
   def rotation
   def zoom
   def startAlpha
   def endAlpha
   def iterations
   def tile

   ShatterFilterProvider() {
      super( "shatter" )
      filter = new ShatterFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "centreX":
         case "centreY":
         case "transition":
         case "zoom":
         case "startAlpha":
         case "endAlpha":
            return value as float
         case "iterations":
         case "tile":
            return value as int
         case "angle":
         case "rotation":
            return JHlabsFilterUtils.getAngle(value)
         default:
            return super.convertValue(property,value)
      }
   }
}