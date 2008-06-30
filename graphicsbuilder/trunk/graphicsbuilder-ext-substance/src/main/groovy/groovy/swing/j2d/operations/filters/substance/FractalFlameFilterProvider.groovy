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

package groovy.swing.j2d.operations.filters.substance

import groovy.swing.j2d.GraphicsBuilderHelper
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import java.awt.Shape
import java.awt.Rectangle
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

import org.jvnet.substance.api.SubstanceColorScheme
import org.jvnet.substance.watermarkpack.flamefractal.IteratedFunctionSystem
import org.jvnet.substance.watermarkpack.flamefractal.Singularity
import org.jvnet.substance.watermarkpack.flamefractal.FractalFlameFactory

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class FractalFlameFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['iterations','ifs']
   public static optional = PropertiesBasedFilterProvider.optional + ['colorScheme1','colorScheme2']

   private fractal
   private lastWidth
   private lastHeight
   
   def iterations = 100000
   def ifs = new Singularity()
   def colorScheme1
   def colorScheme2

   FractalFlameFilterProvider() {
      super( "fractalFlame" )
      filter = this
   }
   
   public BufferedImage filter( BufferedImage src, BufferedImage dst, Shape clip ){
       def img = dst ?: src
      
	   def width = img.width
	   def height = img.height
	   
	   if( !fractal || lastWidth != width || lastHeight != height ){
	      computeFractal( width, height )
	   }
	   
       //def composite = GraphicsBuilderHelper.createCompatibleImage(width,height)
       def g = img.createGraphics()
       def b = clip?.bounds ?: new Rectangle(0,0,0,0)
       g.clip = AffineTransform.getTranslateInstance(parent.offset-b.x,parent.offset-b.y).createTransformedShape(clip)
       g.drawImage( fractal, 0, 0, null )
       g.dispose()
       return img
   }
   
   protected void localPropertyChange( PropertyChangeEvent event ) {
      fractal = null
   }
   
   protected void setFilterProperty( name, value ) {
      // empty
   }
   
   private synchronized void computeFractal( width, height ) {
	  if( colorScheme1 && colorScheme2 ){
         fractal = FractalFlameFactory.getFractalFlameImage( colorScheme1, colorScheme2, width, height, iterations, ifs )
	  }else if( colorScheme1 ){
	     fractal = FractalFlameFactory.getFractalFlameImage( colorScheme1, width, height, iterations, ifs )
	  }else{
	     fractal = FractalFlameFactory.getFractalFlameImage( width, height, iterations, ifs )
	  }
	  
      lastWidth = width
      lastHeight = height
   }
}