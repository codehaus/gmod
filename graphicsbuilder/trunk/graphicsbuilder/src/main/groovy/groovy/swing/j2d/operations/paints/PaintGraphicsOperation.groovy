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
import java.awt.geom.Rectangle2D
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.PaintProvider
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class PaintGraphicsOperation extends AbstractPaintingGraphicsOperation {
    public static required = ['paint']

    def paint

    public PaintGraphicsOperation() {
        super( "paint" )
    }

    void setProperty( String property, Object value ) {
       if( property == "paint" ){
          def paintCopy = value.asCopy()
          paintCopy.addPropertyChangeListener( this )
          super.setProperty( property, paintCopy )
       }else if( this.@paint != null ){
          this.@paint.setProperty( property, value )
       }
    }

    Object getProperty( String property ) {
       if( property == "paint" ){
          return this.@paint
       }else if( this.@paint != null ){
          return this.@paint.getProperty( property )
       }
       throw new MissingPropertyException( property, PaintGraphicsOperation )
    }

    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == paint ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    public Paint getPaint( GraphicsContext context, Rectangle2D bounds ) {
       if( !paint ) throw new IllegalStateException("paint.paint is null!")
       if( !(paint instanceof PaintProvider) ){
          throw new IllegalStateException("paint.paint is not a PaintProvider!")
       }
       return paint.getPaint( context, bounds )
    }
}