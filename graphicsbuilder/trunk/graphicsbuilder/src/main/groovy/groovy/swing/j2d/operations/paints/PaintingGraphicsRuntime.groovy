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

import java.awt.Rectangle
import java.awt.Shape
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.BasicGraphicsRuntime

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class PaintingGraphicsRuntime extends BasicGraphicsRuntime {
   private def paint
    
   PaintingGraphicsRuntime( GraphicsOperation operation, GraphicsContext context ){
      super( operation, context )
   }
   
   /**
    * Returns the paint.<p>
    * 
    * @return a java.awt.Paint
    */
   public def getPaint() {
      if( !paint ){
         def bounds = context?.bounds
         bounds = bounds ?: context?.g?.clipBounds
         paint = operation.getPaint(context, bounds)
      }
      paint
   }
}