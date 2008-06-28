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

package groovy.swing.j2d.operations.filters.swingx

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import java.awt.Shape
import java.awt.image.BufferedImage

import org.jdesktop.swingx.image.StackBlurFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class StackBlurFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['radius','iterations']

   def radius = 3
   def iterations = 3

   StackBlurFilterProvider() {
      super( "stackBlur" )
      filter = new StackBlurFilter()
   }
   
   public BufferedImage filter( BufferedImage src, BufferedImage dst, Shape clip ){
	   filter.filter( src, dst )
   }
   
   protected void setFilterProperty( name, value ){
       filter = new StackBlurFilter( radius as int, iterations as int )
   }
}