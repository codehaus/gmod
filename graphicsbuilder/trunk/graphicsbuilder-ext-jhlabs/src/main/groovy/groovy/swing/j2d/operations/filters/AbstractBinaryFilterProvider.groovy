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

import java.awt.Color
import com.jhlabs.image.Colormap
import com.jhlabs.math.BinaryFunction

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractBinaryFilterProvider extends PropertiesBasedFilterProvider implements ColormapAware {
   public static optional = PropertiesBasedFilterProvider.optional + ['newColor','blackFunction','iterations','colormap']

   def newColor
   def blackFunction
   def iterations
   Colormap colormap

   AbstractBinaryFilterProvider( String name ) {
      super( name )
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "newColor":
            return JHlabsFilterUtils.getColor(value)
         case "iterations":
            return value as int
         case "blackFunction":
            if( value instanceof Closure ) return value as BinaryFunction
         default:
            return super.convertValue(property,value)
      }
   }
}