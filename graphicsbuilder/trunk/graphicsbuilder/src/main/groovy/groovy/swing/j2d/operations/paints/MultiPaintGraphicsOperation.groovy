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

package groovy.swing.j2d.operations.paints

import java.awt.Paint
import java.awt.Shape
import java.awt.geom.Rectangle2D
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.PaintProvider
import groovy.swing.j2d.operations.MultiPaintProvider
import groovy.swing.j2d.operations.AbstractGraphicsOperation
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class MultiPaintGraphicsOperation extends AbstractGraphicsOperation implements MultiPaintProvider {
    private List paints = []

    public MultiPaintGraphicsOperation() {
        super( "multiPaint" )
    }

    public void addPaint( PaintProvider paint ) {
       if( !paint ) return
       paints << paint
       paint.addPropertyChangeListener( this )
   }

   public void removePaint( PaintProvider paint ) {
       if( !paint ) return
       paints.remove( paint )
       paint.removePropertyChangeListener( this )
   }

   public void propertyChange( PropertyChangeEvent event ){
      if( paints.contains(event.source) ){
         firePropertyChange( new ExtPropertyChangeEvent(this,event) )
      }else{
         super.propertyChange( event )
      }
   }

   public void execute( GraphicsContext context ) {
      // EMPTY
   }

   public void apply( GraphicsContext context, Shape shape ){
      def  p = context.g.paint
      paints.each { paint ->
         context.g.paint = paint.getPaint(context, shape.bounds2D)
         context.g.fill( shape )
      }
      context.g.paint = p
   }
}