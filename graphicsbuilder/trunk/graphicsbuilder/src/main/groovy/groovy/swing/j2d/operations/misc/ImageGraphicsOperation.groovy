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
import javax.imageio.ImageIO
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.GraphicsRuntime
import groovy.swing.j2d.operations.AbstractDisplayableGraphicsOperation
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ImageGraphicsOperation extends AbstractDisplayableGraphicsOperation {
    public static required = ['x','y']
    public static optional = AbstractDisplayableGraphicsOperation.optional + ['image','classpath','url','file']

    def image
    def file
    def url
    def classpath
    def x = 0
    def y = 0

    ImageGraphicsOperation() {
        super( "image" )
    }

    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == image ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }
    
    public boolean hasXY() {
       true
    }

    protected void doExecute( GraphicsContext context ){
       if( !hasFilters() ){
          executeOperation( context )
       }else{
          executeWithFilters( context )
       }

       addAsEventTarget(context)
    }

    private void executeOperation( GraphicsContext context ){
       if( asImage ) return

       def g = context.g
       context.g = context.g.create()
       applyOpacity(context)

       context.g.drawImage( runtime.globallyTransformedImage, x, y, null )     

       context.g = g
    }

    private void executeWithFilters( GraphicsContext context ){
       def dx = x - filters.offset
       def dy = y - filters.offset
       def filteredImage = runtime.filteredImage

       if( asImage ) return 

       def g = context.g
       context.g = context.g.create()
       applyOpacity(context)

       context.g.drawImage( filteredImage, dx, dy, null )

       context.g = g
    }

    protected void addAsEventTarget( GraphicsContext context ){
        if( !asImage ){
           super.addAsEventTarget(context)
        }
    }
    
    protected GraphicsRuntime createRuntime( GraphicsContext context ){
       return new ImageGraphicsRuntime(this,context)
    }
}
