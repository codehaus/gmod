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

package groovy.swing.j2d.operations.shapes

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.ShapeProvider

import java.awt.Shape
import java.awt.geom.AffineTransform
import java.beans.PropertyChangeEvent
import org.jdesktop.swingx.geom.Morphing2D

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class MorphGraphicsOperation extends AbstractShapeGraphicsOperation {
   public static required = ["start", "end", "morph"]

   private Shape morphedShape

   def start
   def end
   def morph = 0

   MorphGraphicsOperation() {
      super( "morph" )
   }

   public Shape getShape( GraphicsContext context ) {
      if( morphedShape == null ){
         calculateMorphedShape( context )
      }
      morphedShape
   }

   protected void localPropertyChange( PropertyChangeEvent event ){
      super.localPropertyChange( event )
      morphedShape = null
   }

   private void calculateMorphedShape( GraphicsContext context ) {
      if( start instanceof ShapeProvider && start.asShape != null && start.asShape ){
         start = start.getLocallyTransformedShape(context)
      }
      if( end instanceof ShapeProvider && end.asShape != null && end.asShape ){
         end = end.getLocallyTransformedShape(context)
      }

      morphedShape = new Morphing2D( start, end )
      morphedShape.morphing = morph as double
      morphedShape = new AffineTransform().createTransformedShape(morphedShape)
   }
}