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

package groovy.swing.j2d.operations.shapes

import groovy.swing.j2d.GraphicsContext

import java.awt.Dimension
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.beans.PropertyChangeEvent

import javax.swing.JButton
import org.jvnet.substance.shaperpack.BasePolygonShaper

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShaperGraphicsOperation extends AbstractShapeGraphicsOperation  {
   public static optional = AbstractShapeGraphicsOperation.optional + ['x','y','width','height']

   private Shape path
   private BasePolygonShaper shaper
   private final JButton button

   def x = 0
   def y = 0
   def width = 10
   def height = 10

   ShaperGraphicsOperation( String name, BasePolygonShaper shaper ) {
      super( name )
      this.shaper = shaper
      this.button = new JButton("GB")
   }

   protected void localPropertyChange( PropertyChangeEvent event ){
      super.localPropertyChange( event )
      if( event.propertyName in ["width","height"] ){
         path = null
      }else if( event.propertyName in ["x","y"] ){
         translatePath()
      }
   }

   public Shape getShape( GraphicsContext context ) {
      if( path == null ){
         calculatePath()
         translatePath()
      }
      path
   }
   
   void calculatePath() {
      Dimension d = new Dimension()
      d.setSize( width as int, height as int )
      button.size = d
      path = shaper.getButtonOutline(button)
   }
   
   void translatePath() {
      if( path == null ) calculatePath()
      path = AffineTransform.getTranslateInstance(x,y).createTransformedShape(path)
   }
}