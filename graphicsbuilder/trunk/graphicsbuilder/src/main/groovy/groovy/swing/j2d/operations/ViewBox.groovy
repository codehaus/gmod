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

import java.awt.Shape
import java.awt.geom.Rectangle2D
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.ObservableSupport
import groovy.swing.j2d.impl.ExtPropertyChangeEvent
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ViewBox extends ObservableSupport implements Transformable {
    protected Shape locallyTransformedShape

    TransformationGroup transformations

    def x = 0
    def y = 0
    def width = 10
    def height = 10
    def pinned = false
    def shape

    ViewBox(){
       shape = new Rectangle2D.Double(0,0,10,10)
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

    public Shape getShape( GraphicsContext context ){
       return shape
    }

    public Shape getLocallyTransformedShape( GraphicsContext context ){
       if( !this.@locallyTransformedShape ){
          calculateLocallyTransformedShape( context )
       }
       return this.@locallyTransformedShape
    }

    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == transformations ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    public void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       this.@locallyTransformedShape = null
       this.@shape = null
    }

    void setProperty( String property, Object value ) {
       if( ["x","y","width","height"].contains(property) ){
          if( value == null ) return
          def oldValue = getProperty( property )
          super.setProperty( property, value )
          this.@shape = new Rectangle2D.Double( x as double,
                                                y as double,
                                                width as double,
                                                height as double )
          firePropertyChange( property, oldValue, value )
       }else{
           super.setProperty( property, value )
       }
   }

    protected void calculateLocallyTransformedShape( GraphicsContext context ) {
       if( transformations && !transformations.empty){
          this.@locallyTransformedShape = transformations.apply( getShape(context) )
       }else{
          this.@locallyTransformedShape = getShape(context)
       }
    }
}
