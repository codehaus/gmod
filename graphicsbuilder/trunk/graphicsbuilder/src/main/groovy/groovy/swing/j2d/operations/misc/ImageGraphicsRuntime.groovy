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

package groovy.swing.j2d.operations.misc

import java.awt.Image
import java.awt.Rectangle
import java.awt.Shape
import java.awt.image.BufferedImage
import java.awt.GraphicsConfiguration
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import javax.imageio.ImageIO
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.GraphicsBuilderHelper
import groovy.swing.j2d.operations.AbstractDisplayableGraphicsRuntime

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ImageGraphicsRuntime extends AbstractDisplayableGraphicsRuntime {
   private def filteredImage
   private def image
   private def boundingShape
   private def locallyTransformedImage
   private def globallyTransformedImage
    
   ImageGraphicsRuntime( GraphicsOperation operation, GraphicsContext context ){
      super( operation, context )
   }
   
   public def getImage() {
      if( !image ){
         if( operation.image ){
            if( operation.image instanceof ImageGraphicsOperation ){
               operation.image.addPropertyChangeListener( this )
               image= image.runtime.locallyTransformedImage
            }else if( operation.image instanceof Image || operation.image instanceof BufferedImage ){
               image = operation.image
            }else if( operation.image instanceof GraphicsOperation && operation.image.runtime.image ){
               operation.image.addPropertyChangeListener( this )
               image = operation.image.runtime.image
               boundingShape = operation.image.runtime.boundingShape
            }else {
               throw new IllegalArgumentException("image.image is not a java.awt.Image nor a java.awt.image.BufferedImage")
            }
         }else if( operation.classpath ){
            URL imageUrl = Thread.currentThread().getContextClassLoader().getResource( operation.classpath )
            image = ImageIO.read( imageUrl )
         }else if( operation.url ){
            image = ImageIO.read( operation.url instanceof String ? operation.url.toURL(): operation.url )
         }else if( operation.file ){
            image = ImageIO.read( operation.file instanceof String ? new File(operation.file): operation.file  )
         }else{
            throw new IllegalArgumentException("Must define one of [image,classpath,url,file]")
         }
      }
      image
   }
   
   public def getLocallyTransformedImage() {
      if( !locallyTransformedImage ){
         if( operation.transformations && !operation.transformations.isEmpty() ){
            locallyTransformedImage = operation.transformations.apply( getImage(), context )
         }else{
            locallyTransformedImage = getImage()
         }
      }
      locallyTransformedImage
   }
   
   public def getGloballyTransformedImage() {
      if( !globallyTransformedImage ){
         if( operation.globalTransformations && !operation.globalTransformations.isEmpty() ){
            globallyTransformedImage = operation.globalTransformations.apply(
                  getLocallyTransformedImage(), context )
         }else{
            globallyTransformedImage = getLocallyTransformedImage()
         }
      }
      globallyTransformedImage
   }
   
   public def getFilteredImage() {
      if( !filteredImage ) {
         def input = getGloballyTransformedImage()
         def bounds = new Rectangle( operation.x as int,
                                     operation.y as int,
                                     (input.width+(operation.filters.offset*2)) as int,
                                     (input.height+(operation.filters.offset*2)) as int )
         def clip = new Rectangle( 0i, 0i, input.width as int, input.height as int )

         def dc = context?.g?.deviceConfiguration
         BufferedImage src = GRaphicsBuilderHelper.createCompatibleImage( bounds.width as int, bounds.height as int )

         def graphics = src.createGraphics()
         def contextCopy = context.copy()
         graphics.setClip( 0i, 0i, bounds.width as int, bounds.height as int )
         graphics.renderingHints.putAll( context.g.renderingHints )
         contextCopy.g = graphics
         graphics.drawImage( input, operation.filters.offset as int, operation.filters.offset as int, null )

         // apply filters
         filteredImage = operation.filters.apply( src, clip )
      }
      filteredImage
   }
   
   /**
    * Returns the bounding shape.<p>
    * 
    * @return a java.awt.Shape
    */
   public def getBoundingShape() {
      if( !boundingShape ){
         if( operation.hasFilters() ){
            getFilteredImage()
            boundingShape = new Rectangle(operation.x - operation.filters.offset,
                                          operation.y - operation.filters.offset,
                                          filteredImage.width,
                                          filteredImage.height)
         }else{
            def gi = getGloballyTransformedImage()
            boundingShape = new Rectangle(operation.x,
                                          operation.y,
                                          gi.width,
                                          gi.height)
         }
      }
      boundingShape
   }
}