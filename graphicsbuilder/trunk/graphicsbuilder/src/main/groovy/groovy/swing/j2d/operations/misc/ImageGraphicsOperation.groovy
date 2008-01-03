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

package groovy.swing.j2d.operations.misc

import java.awt.AlphaComposite
import java.awt.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.OutlineProvider
import groovy.swing.j2d.operations.ShapeProvider
import groovy.swing.j2d.operations.Transformable
import groovy.swing.j2d.operations.TransformationGroup
import groovy.swing.j2d.operations.AbstractGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ImageGraphicsOperation extends AbstractGraphicsOperation implements Transformable {
    public static required = ['x','y']
    public static optional = ['image','classpath','url','file','asImage','opacity']

    private Image imageObj
    protected Image locallyTransformedImage
    protected Image globallyTransformedImage
    TransformationGroup transformationGroup
    TransformationGroup globalTransformationGroup

    def image
    def file
    def url
    def classpath
    def x = 0
    def y = 0
    def asImage = false
    def opacity

    ImageGraphicsOperation() {
        super( "image" )
    }

    public void propertyChange( PropertyChangeEvent event ){
       // TODO review for fine-grain detail
       imageObj = null
       locallyTransformedImage = null
       globallyTransformedImage = null
    }

    public Image getImageObj( GraphicsContext context ) {
       if( !this.@imageObj ){
          loadImage( context )
       }
       return this.@imageObj
    }

    public Image getLocallyTransformedImage( GraphicsContext context ){
       if( !locallyTransformedImage ){
          calculateLocallyTransformedImage( context )
       }
       return locallyTransformedImage
    }

    public Image getGloballyTransformedImage( GraphicsContext context ){
       if( !globallyTransformedImage ){
          calculateGloballyTransformedImage( context )
       }
       return globallyTransformedImage
    }

    public void execute( GraphicsContext context ){
        if( asImage ) return
        def o = opacity
        if( context.groupContext.opacity ){
           o = context.groupContext.opacity
        }
        if( opacity != null ){
           o = opacity
        }

        def g = context.g
        context.g = context.g.create()
        if( o != null ){
           context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, o as float)
        }else{
           context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
        }

        context.g.drawImage( getGloballyTransformedImage(context), x, y, null )

        context.g = g
    }

    public void setTransformationGroup( TransformationGroup transformationGroup ){
       if( transformationGroup ) {
          if( this.transformationGroup ){
             this.transformationGroup.removePropertyChangeListener( this )
          }
          this.transformationGroup = transformationGroup
          this.transformationGroup.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getTransformationGroup() {
       transformationGroup
    }

    public void setGlobalTransformationGroup( TransformationGroup globalTransformationGroup ){
       if( globalTransformationGroup ) {
          if( this.globalTransformationGroup ){
             this.globalTransformationGroup.removePropertyChangeListener( this )
          }
          this.globalTransformationGroup = globalTransformationGroup
          this.globalTransformationGroup.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getGlobalTransformationGroup() {
       globalTransformationGroup
    }

    private void calculateLocallyTransformedImage( GraphicsContext context ) {
       if( transformationGroup && !transformationGroup.isEmpty() ){
          this.@locallyTransformedImage = transformationGroup.apply( getImageObj(context), context )
       }else{
          this.@locallyTransformedImage = getImageObj(context)
       }
    }

    private void calculateGloballyTransformedImage( GraphicsContext context ) {
       if( globalTransformationGroup && !globalTransformationGroup.isEmpty() ){
          this.@globallyTransformedImage = globalTransformationGroup.apply(
                getLocallyTransformedImage(context), context )
       }else{
          this.@globallyTransformedImage = getLocallyTransformedImage(context)
       }
    }

    private void loadImage( GraphicsContext context ) {
       if( image ){
          if( image instanceof ImageGraphicsOperation ){
             this.@imageObj= image.getLocallyTransformedImage(context)
          }else if( image instanceof Image || image instanceof BufferedImage ){
             this.@imageObj = image
          }else if( image instanceof ShapeProvider || image instanceof OutlineProvider ){
             this.@imageObj = image.asImage(context)
          }else {
             throw new IllegalArgumentException("image.image is not a java.awt.Image nor a java.awt.image.BufferedImage")
          }
       }else if( classpath ){
          URL imageUrl = Thread.currentThread().getContextClassLoader().getResource( classpath )
          this.@imageObj = ImageIO.read( imageUrl )
       }else if( url ){
          this.@imageObj = ImageIO.read( url instanceof String ? url.toURL(): url )
       }else if( file ){
          this.@imageObj = ImageIO.read( file instanceof String ? new File(file): file  )
       }else{
          throw new IllegalArgumentException("Must define one of [image,classpath,url,file]")
       }
    }
}