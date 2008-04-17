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

import java.awt.Rectangle
import java.awt.Shape
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.AbstractDisplayableGraphicsRuntime

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GroupGraphicsRuntime extends AbstractDisplayableGraphicsRuntime {
   private Shape boundingShape
    
   GroupGraphicsRuntime( GraphicsOperation operation, GraphicsContext context ){
      super( operation, context )
   }
   
   /**
    * Returns the bounding shape including stroked border.<p>
    * 
    * @return a java.awt.Shape
    */
   public def getBoundingShape() {
      if( !boundingShape ){
         boundingShape = [0,0,0,0] as Rectangle
         if( operation.viewBox ){
            boundingShape = operation.viewBox.getLocallyTransformedShape(context)
            if( !operation.viewBox.pinned ){
               if( operation.transformations ) boundingShape = operation.transformations.apply(boundingShape)
               if( operation.globalTransformations ) boundingShape = operation.globalTransformations.apply(boundingShape)
            }
         }else if( context?.g?.clipBounds ){
            boundingShape = new Rectangle(context.g.clipBounds)
         }else if( context ){
            boundingShape.width = context.component?.bounds?.width
            boundingShape.height = context.component?.bounds?.height         
         }
      }
      boundingShape
   }
}