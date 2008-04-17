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

import java.awt.Color
import java.awt.Paint
import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractDisplayableGraphicsRuntime extends BasicGraphicsRuntime {
   protected def fill
   protected def paint
   protected def borderColor
   protected def borderWidth
    
   AbstractDisplayableGraphicsRuntime( GraphicsOperation operation, GraphicsContext context ){
      super( operation, context )
   }
   
   /**
    * Returns the bounding shape including stroked border.<p>
    * 
    * @return a java.awt.Shape
    */
   public abstract def getBoundingShape()
   
   /**
    * Returns the borderColor taking into account inherited value from group.<p>
    * 
    * @return a java.awt.Color
    */
   public def getBorderColor(){
      if( !borderColor ){
         borderColor = context?.g?.color
         if( context?.groupContext?.borderColor != null ){
            borderColor = context.groupContext.borderColor
         }
         if( operation.borderColor != null ){
            borderColor = operation.borderColor
         }
         if( borderColor instanceof Boolean && !borderColor ){
            borderColor = null
         }else{
            if( borderColor instanceof String ){
               borderColor = ColorCache.getInstance().getColor(borderColor)
            }
            if( !borderColor ){
               borderColor = context?.g?.color
            }
         }
      }
      borderColor
   }

   /**
    * Returns the borderWidth taking into account inherited value from group.<p>
    * 
    * @return an int
    */
   public def getBorderWidth(){
      if( borderWidth == null ){
         borderWidth = 1
         if( context?.groupContext?.borderWidth != null ){
            borderWidth = context.groupContext.borderWidth
         }
         if( operation.borderWidth != null ){
            borderWidth = operation.borderWidth
         }
      }
      borderWidth
   }
   
   /**
    * Returns the fill to be used.<p>
    * Fill will be determined with the following order<ol>
    * <li>fill property (inherited from group too) if set to false</li>
    * <li>nested paint node</li>
    * <li>fill property (inherited from group too) if set to non null, non false</li>
    * </ol>
    * 
    * @return either <code>false</code>/<code>null</code> (no fill), java.awt.Color, java.awt.Paint or MultiPaintProvider 
    */
   public def getFill() {
      if( fill == null ){
         if( context?.groupContext?.fill != null ){
            fill = context.groupContext.fill
         }
         if( operation.fill != null ){
            fill = operation.fill
         }
         
         def pp = getPaint()
         
         if( fill instanceof Boolean && !fill ){
            fill = null
         }else if( pp != null ){
            if( pp instanceof PaintProvider ){
               fill = pp.runtime(context).paint
            }else if( pp instanceof MultiPaintProvider ){
               // let the caller handle it
               fill = pp
            }
         }else if( fill ){
            switch( fill ){
               case String:
                  fill = ColorCache.getInstance().getColor(fill)
                  break
               case PaintProvider:
                  fill = fill.runtime(context).paint
                  break
               case true:/*
                  def pp = getPaint()
                  if( pp ){
                     if( pp instanceof PaintProvider ){
                        fill = pp.runtime(context).paint
                     }else if( pp instanceof MultiPaintProvider ){
                        // let the caller handle it
                        fill = pp
                     }
                  }else{*/
                     // use current settings on context
                     fill = context?.g.color
                  /*}*/            
                  break
               case Color:   
               case Paint:
                  /* do nothing */
                  break
            }
         }
      }
      fill
   }
    
    /**
     * Returns a nested paint() node if any.<p>
     * 
     * @return either a PaintProvider, MultipaintProvider or null if no nested paint() node is found
     */
    public def getPaint() {
       if( !paint ){
          operation.operations.each { o ->
             if( o instanceof PaintProvider || o instanceof MultiPaintProvider ) paint = o
          }
       }
       paint
    }
}