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
import java.awt.Paint
import java.awt.TexturePaint
import java.awt.image.BufferedImage
import java.awt.geom.Rectangle2D
import javax.imageio.ImageIO
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.OutlineProvider
import groovy.swing.j2d.ShapeProvider
import groovy.swing.j2d.impl.AbstractPaintingGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class TexturePaintGraphicsOperation extends AbstractPaintingGraphicsOperation {
    protected static optional = super.optional + ['x','y','width','height','image','classpath','url','file','absolute']

    private Paint paint
    protected Image imageObj

    def image
    def file
    def url
    def classpath
    def x = 0
    def y = 0
    def width
    def height
    def absolute = false

    TexturePaintGraphicsOperation() {
        super( "texturePaint" )
    }

    public void execute( GraphicsContext context ) {
       if( asPaint ) return
       context.g.paint = getPaint( context, null )
    }

    public Paint getPaint( GraphicsContext context, Rectangle2D bounds ) {
       if( !paint ){
          def newBounds = [x as double, y as double, 0d, 0d] as Rectangle2D.Double
          if( !absolute && bounds ){
             newBounds.x += bounds.x
             newBounds.y += bounds.y
          }
          def iobj = null
          if( image ){
             if( image instanceof Image ){
                iobj = image
             }else if( image instanceof ImageGraphicsOperation ){
                // TODO refactor this mess
                if( image.image && image.image instanceof ImageGraphicsOperation ){
                   iobj = image.image.imageObj
                }else{
                   iobj = image.imageObj
                }
             }else if( image instanceof ShapeProvider || image instanceof OutlineProvider ){
                iobj = image.asImage(context)
             }
          }else{
             iobj = getImageObj()
          }
          newBounds.width = iobj.getWidth(null)
          newBounds.height = iobj.getHeight(null)
          if( width ) newBounds.width = width
          if( height ) newBounds.height = height
          paint = new TexturePaint( iobj, newBounds )
       }
       return paint
    }

    public void propertyChange( PropertyChangeEvent event ){
       // TODO review for fine-grain detail
       paint = null
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
          this.@imageObj = ImageIO.read( file instanceof String ? new File(file): file )
       }else{
          throw new IllegalArgumentException("Must define one of [image,classpath,url,file]")
       }
    }
}