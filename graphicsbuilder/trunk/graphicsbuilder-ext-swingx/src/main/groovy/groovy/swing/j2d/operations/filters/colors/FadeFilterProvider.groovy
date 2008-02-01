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

package groovy.swing.j2d.operations.filters.colors

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import java.beans.PropertyChangeEvent
import java.awt.Dimension
import com.jhlabs.image.FadeFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class FadeFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['fadeStart','fadeWidth','sides','invert','dimensions']

   def fadeStart
   def fadeWidth
   def sides
   def invert
   def dimensions

   FadeFilterProvider() {
      super( "fade" )
      filter = new FadeFilter()
   }

   public void propertyChange( PropertyChangeEvent event ) {
      def propertyName = event.propertyName
      if( isParameter(propertyName) && hasProperty(filter,propertyName) ){
         if( propertyName == "Dimensions"){
            def dimension = getDimension(value)
            filter.setDimensions( dimension.width as int, dimension.height as int )
         }else{
            filter."$propertyName" = convertValue(propertyName,event.newValue)
         }
      }
      super.propertyChange( event )
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "invert":
            return value as boolean
         case "fadeStart":
         case "fadeWidth":
            return value as float
         case "sides":
            return value as int
         default:
            return super.convertValue(property,value)
      }
   }

   private def getDimension( value ){
      if( value instanceof Dimension ){
         return value
      }else if( value instanceof Map ){
         value.width = value.width ? value.width : 0
         value.height = value.height ? value.height : 0
         return value
      }else if( value instanceof List && value.size() == 2 ){
         return value as Dimension
      }
   }
}