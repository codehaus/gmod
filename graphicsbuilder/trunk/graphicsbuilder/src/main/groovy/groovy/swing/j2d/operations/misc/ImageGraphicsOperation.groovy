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
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Transparency
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.event.GraphicsInputEvent
import groovy.swing.j2d.event.GraphicsInputListener
import groovy.swing.j2d.operations.Filterable
import groovy.swing.j2d.operations.FilterProvider
import groovy.swing.j2d.operations.FilterGroup
import groovy.swing.j2d.operations.Transformable
import groovy.swing.j2d.operations.TransformationGroup
import groovy.swing.j2d.operations.AbstractGraphicsOperation
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ImageGraphicsOperation extends AbstractGraphicsOperation implements Transformable, Filterable, GraphicsInputListener {
    public static required = ['x','y']
    public static optional = ['image','classpath','url','file','asImage','opacity','composite','passThrough']

    private BufferedImage filteredImage
    private Image imageObj
    private Rectangle boundingShape
    protected Image locallyTransformedImage
    protected Image globallyTransformedImage
    
    TransformationGroup transformations
    TransformationGroup globalTransformations
    FilterGroup filters

    Closure keyPressed
    Closure keyReleased
    Closure keyTyped
    Closure mouseClicked
    Closure mouseDragged
    Closure mouseEntered
    Closure mouseExited
    Closure mouseMoved
    Closure mousePressed
    Closure mouseReleased
    Closure mouseWheelMoved

    def image
    def file
    def url
    def classpath
    def x = 0
    def y = 0
    def asImage = false
    def opacity
    def composite
    def passThrough

    ImageGraphicsOperation() {
        super( "image" )
    }

    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == transformations ||
           event.source == globalTransformations ||
           event.source == image ||
           event.source == filters ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       filteredImage = null
       imageObj = null
       boundingShape = null
       locallyTransformedImage = null
       globallyTransformedImage = null
    }

    public Image getImageObj( GraphicsContext context ) {
       if( !this.@imageObj ){
          loadImage( context )
       }
       return this.@imageObj
    }

    public Shape getBoundingShape( GraphicsContext context ) {
       boundingShape
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

    protected void doExecute( GraphicsContext context ){
       if( !filters || filters.empty ){
          executeOperation( context )
       }else{
          executeWithFilters( context )
       }

       addAsEventTarget(context)
    }

    public void setTransformations( TransformationGroup transformations ){
       if( transformations ) {
          if( this.transformations ){
             this.transformations.removePropertyChangeListener( this )
          }
          this.transformations = transformations
          this.transformations.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getTransformations() {
       transformations
    }
    
    public TransformationGroup getTxs() {
       transformations
    }    

    public void setGlobalTransformations( TransformationGroup globalTransformations ){
       if( globalTransformations ) {
          if( this.globalTransformations ){
             this.globalTransformations.removePropertyChangeListener( this )
          }
          this.globalTransformations = globalTransformations
          this.globalTransformations.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getGlobalTransformations() {
       globalTransformations
    }

    public void setFilters( FilterGroup filters ){
       if( filters ) {
          if( this.filters ){
             this.filters.removePropertyChangeListener( this )
          }
          this.filters = filters
          this.filters.addPropertyChangeListener( this )
       }
    }

    public FilterGroup getFilters() {
       filters
    }

    public void keyPressed( GraphicsInputEvent e ) {
       if( keyPressed ) this.@keyPressed(e)
    }

    public void keyReleased( GraphicsInputEvent e ) {
       if( keyReleased ) this.@keyReleased(e)
    }

    public void keyTyped( GraphicsInputEvent e ) {
       if( keyTyped ) this.@keyTyped(e)
    }

    public void mouseClicked( GraphicsInputEvent e ) {
       if( mouseClicked ) this.@mouseClicked(e)
    }

    public void mouseDragged( GraphicsInputEvent e ) {
       if( mouseDragged ) this.@mouseDragged(e)
    }

    public void mouseEntered( GraphicsInputEvent e ) {
       if( mouseEntered ) this.@mouseEntered(e)
    }

    public void mouseExited( GraphicsInputEvent e ) {
       if( mouseExited ) this.@mouseExited(e)
    }

    public void mouseMoved( GraphicsInputEvent e ) {
       if( mouseMoved ) this.@mouseMoved(e)
    }

    public void mousePressed( GraphicsInputEvent e ) {
       if( mousePressed ) this.@mousePressed(e)
    }

    public void mouseReleased( GraphicsInputEvent e ) {
       if( mouseReleased ) this.@mouseReleased(e)
    }

    public void mouseWheelMoved( GraphicsInputEvent e ) {
       if( mouseWheelMoved ) this.@mouseWheelMoved(e)
    }

    private void executeOperation( GraphicsContext context ){
       def gi = getGloballyTransformedImage(context)
       boundingShape = new Rectangle(x,y,gi.width,gi.height)

       if( asImage ) return

       def o = opacity

       /*
       if( context.groupContext.opacity ){
          o = context.groupContext.opacity
       }
       if( opacity != null ){
          o = opacity
       }
       */

       def g = context.g
       context.g = context.g.create()
       if( o != null ){
          context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, o as float)
       }/*else{
          context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
       }*/

       context.g.drawImage( getGloballyTransformedImage(context), x, y, null )

       context.g = g
    }

    private void executeWithFilters( GraphicsContext context ){
       def dx = x - filters.offset
       def dy = y - filters.offset
       calculateFilteredImage( context )

       boundingShape = new Rectangle(dx,dy,filteredImage.width,filteredImage.height)

       if( asImage ) return
       def o = opacity
       
       /*
       if( context.groupContext.opacity ){
          o = context.groupContext.opacity
       }
       if( opacity != null ){
          o = opacity
       }
       */

       def g = context.g
       context.g = context.g.create()
       if( o != null ){
          context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, o as float)
       }/*else{
          context.g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
       }*/

       context.g.drawImage( filteredImage, dx, dy, null )

       context.g = g
    }

    private void calculateFilteredImage( GraphicsContext context ){
       def input = getGloballyTransformedImage(context)
       def bounds = new Rectangle( x as int,
                                   y as int,
                                   (input.width+(filters.offset*2)) as int,
                                   (input.height+(filters.offset*2)) as int )
       def clip = new Rectangle( 0i, 0i, input.width as int, input.height as int )

       BufferedImage src = context.g.deviceConfiguration.createCompatibleImage(
             bounds.width as int, bounds.height as int, Transparency.BITMASK )

       def graphics = src.createGraphics()
       def contextCopy = context.copy()
       graphics.setClip( 0i, 0i, bounds.width as int, bounds.height as int )
       graphics.renderingHints.putAll( context.g.renderingHints )
       contextCopy.g = graphics
       graphics.drawImage( input, filters.offset as int, filters.offset as int, null )

       // apply filters
       filteredImage = filters.apply( src, clip )
    }

    private void calculateLocallyTransformedImage( GraphicsContext context ) {
       if( transformations && !transformations.isEmpty() ){
          this.@locallyTransformedImage = transformations.apply( getImageObj(context), context )
       }else{
          this.@locallyTransformedImage = getImageObj(context)
       }
    }

    private void calculateGloballyTransformedImage( GraphicsContext context ) {
       if( globalTransformations && !globalTransformations.isEmpty() ){
          this.@globallyTransformedImage = globalTransformations.apply(
                getLocallyTransformedImage(context), context )
       }else{
          this.@globallyTransformedImage = getLocallyTransformedImage(context)
       }
    }

    private void loadImage( GraphicsContext context ) {
       if( image ){
          if( image instanceof ImageGraphicsOperation ){
             image.addPropertyChangeListener( this )
             this.@imageObj= image.getLocallyTransformedImage(context)
          }else if( image instanceof Image || image instanceof BufferedImage ){
             this.@imageObj = image
          }else if( image instanceof GraphicsOperation && image.image ){
             image.addPropertyChangeListener( this )
             this.@imageObj = image.image
             boundingShape = image.getBoundingShape(context)
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

    private void addAsEventTarget( GraphicsContext context ){
        if( !asImage && (keyPressed ||
            keyReleased || keyTyped || mouseClicked ||
            mouseDragged || mouseEntered || mouseExited ||
            mouseMoved || mousePressed || mouseReleased ||
            mouseWheelMoved) ){
           context.eventTargets << this
        }
    }
}
