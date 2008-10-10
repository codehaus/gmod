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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import java.awt.Shape
import java.awt.image.BufferedImage

import org.jdesktop.swingx.graphics.GraphicsUtilities
import org.jdesktop.swingx.graphics.ShadowRenderer

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class DropShadowFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['opacity','size','color','shadowOnly']

   def opacity
   def size
   def color
   def shadowOnly = false

   DropShadowFilterProvider() {
      super( "dropShadow" )
      filter = new ShadowRenderer()
   }
   
   public BufferedImage filter( BufferedImage src, BufferedImage dst, Shape clip ){
	   def shadowImage = filter.createShadow( dst ?: src )
	   if( shadowOnly ) return shadowImage
	   def composedImage = GraphicsUtilities.createCompatibleImage(shadowImage)
	   def g = composedImage.createGraphics()
	   g.drawImage( shadowImage, 0, 0, null )
	   g.drawImage( dst ?: src, 0, 0, null )
	   g.dispose()
	   return composedImage
   }
   
   protected def convertValue( property, value ){
      switch( property ){
         case "color":
            return ColorCache.getColor(value)
         default:
            return super.convertValue(property,value)
      }
   }
}