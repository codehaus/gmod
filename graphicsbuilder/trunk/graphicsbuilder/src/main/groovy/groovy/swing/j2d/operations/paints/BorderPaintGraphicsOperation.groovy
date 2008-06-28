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

package groovy.swing.j2d.operations.paints

import java.awt.Paint
import java.awt.geom.Rectangle2D
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.AbstractGraphicsOperation
import groovy.swing.j2d.operations.BorderPaintProvider
import groovy.swing.j2d.operations.PaintProvider
import groovy.swing.j2d.operations.MultiPaintProvider
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class BorderPaintGraphicsOperation extends AbstractGraphicsOperation implements BorderPaintProvider {
    public static required = ['paint']

    def paint

    public BorderPaintGraphicsOperation() {
        super( "borderPaint" )
    }

    void setProperty( String property, Object value ) {
       if( property == "paint" ){
          if( value instanceof PaintProvider ){
             def paintCopy = value.asCopy()
             paintCopy.addPropertyChangeListener( this )
             super.setProperty( property, paintCopy )
          }else if( value instanceof MultiPaintProvider ){
             value.addPropertyChangeListener( this )
             super.setProperty( property, value )
          }else{
             super.setProperty( property, value )
          }
       }else if( this.@paint != null ){
          this.@paint.setProperty( property, value )
       }
    }

    public void propertyChange( PropertyChangeEvent event ){
       if( operations.contains(event.source) ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    Object getProperty( String property ) {
       if( property == "paint" ){
          return this.@paint
       }else if( this.@paint != null ){
          return this.@paint.getProperty( property )
       }
       throw new MissingPropertyException( property, BorderPaintGraphicsOperation )
    }

    /*
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
       super.addPropertyChangeListener( listener )
       if( this.@paint ) this.@paint.addPropertyChangeListener( listener )
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
       super.removePropertyChangeListener( listener )
       if( this.@paint ) this.@paint.removePropertyChangeListener( listener )
    }
    */

    public Paint getPaint( GraphicsContext context, Rectangle2D bounds ) {
       if( !paint ) throw new IllegalStateException("borderPaint.paint is null!")
       if( paint instanceof MultiPaintProvider ) return null
       if( !(paint instanceof PaintProvider) ){
          throw new IllegalStateException("borderPaint.paint is not a PaintProvider!")
       }
       return paint.getPaint( context, bounds )
    }

    protected void doExecute( GraphicsContext context ) {
        // empty
    }
}
