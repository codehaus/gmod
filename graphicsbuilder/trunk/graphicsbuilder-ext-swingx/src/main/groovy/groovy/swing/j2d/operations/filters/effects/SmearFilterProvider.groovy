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
import groovy.swing.j2d.operations.filters.FilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.SmearFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SmearFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['colormap','angle','density','distance','shape',
                             'mix','background']

   def colormap
   def angle
   def density
   def distance
   def shape
   def mix
   def background

   SmearFilterProvider() {
      super( "smear" )
      filter = new SmearFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "density":
         case "mix":
            return value as float
         case "background":
            return value as boolean
         case "distance":
            return value as int
         case "angle":
            return FilterUtils.getAngle(value)
         case "shape":
            return getShape(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private def getShape( value ){
      if( value instanceof Number ){
         return value.intValue()
      }
      switch( value ){
         case "crosses":
         case "smearCrosses":
            return SmearFilter.CROSSES
         case "lines":
         case "smearLines":
            return SmearFilter.LINES
         case "circles":
         case "smearCircles":
            return SmearFilter.CIRCLES
         case "squares":
         case "smearSquares":
            return SmearFilter.SQUARES
      }
      return null
   }
}