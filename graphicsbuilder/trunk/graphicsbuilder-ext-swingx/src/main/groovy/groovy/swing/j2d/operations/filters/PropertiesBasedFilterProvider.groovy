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

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.AbstractFilterProvider

import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class PropertiesBasedFilterProvider extends AbstractFilterProvider {
   protected def filter

   PropertiesBasedFilterProvider( String name ) {
      super( name )
   }

   public void propertyChange( PropertyChangeEvent event ) {
      def propertyName = event.propertyName
      if( isParameter(propertyName) && hasProperty(filter,propertyName) ){
         filter."$propertyName" = convertValue(propertyName,event.newValue)
      }
      super.propertyChange( event )
   }

   protected def convertValue( property, value ){
      return value
   }
}