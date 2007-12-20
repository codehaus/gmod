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

package groovy.swing.j2d.operations

import java.awt.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.AbstractGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ImageGraphicsOperation extends AbstractGraphicsOperation {
    protected static required = ['x','y']
    protected static optional = ['image','classpath','url','file','asImage']

    private Image imageObj

    def image
    def file
    def url
    def classpath
    def x = 0
    def y = 0
    def asImage = false

    ImageGraphicsOperation() {
        super( "image" )
    }

    public void execute( GraphicsContext context ){
        if( asImage ) return
        if( image instanceof ImageGraphicsOperation ){
           context.g.drawImage( image.imageObj, x, y, null )
        }else{
           context.g.drawImage( getImageObj(), x, y, null )
        }
    }

    public Image getImageObj() {
       if( !this.@imageObj ){
          loadImage()
       }
       return this.@imageObj
    }

    private void loadImage() {
       if( image ){
          if( image instanceof Image || image instanceof BufferedImage ){
             this.@imageObj = image
          }
          throw new IllegalArgumentException("image.image is not a java.awt.Image nor a java.awt.image.BufferedImage")
       }else if( classpath ){
          URL imageUrl = Thread.currentThread().getContextClassLoader().getResource( classpath )
          this.@imageObj = ImageIO.read( imageUrl )
       }else if( url ){
          this.@imageObj = ImageIO.read( url instanceof String ? url.toURL(): url )
       }else if( file ){
          this.@imageObj = ImageIO.read( new File(file) )
       }else{
          throw new IllegalArgumentException("Must define one of [image,classpath,url,file]")
       }
    }
}