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

import java.awt.Rectangle
import java.awt.Shape
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.ShapeProvider
import groovy.swing.j2d.operations.DrawingGraphicsRuntime

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShapeGraphicsRuntime extends DrawingGraphicsRuntime {
   private Shape shape
    
   ShapeGraphicsRuntime( GraphicsOperation operation, GraphicsContext context ){
      super( operation, context )
   }
   
   /**
    * Returns the shape to be drawn<p>
    * 
    * @return a java.awt.Shape
    */
   public Shape getShape() {
      if( !shape ){
         if( operation.shape instanceof ShapeProvider ){
            // TODO apply local
            shape = operation.shape.runtime.locallyTransformedShape
            if( operation.transformations && !operation.transformations.isEmpty() ){
               shape = operation.transformations.apply(s)
            }
         }else if( operation.shape instanceof Shape ){
            shape = operation.shape
         }else{
            throw new IllegalArgumentException("shape.shape must be one of [java.awt.Shape,ShapeProvider]")
         }
      }
      shape
   }
    
   public Shape getLocallyTransformedShape() {
      getShape()
   }
   
   public Shape getGloballyTransformedShape() {
      getShape()
   }
}