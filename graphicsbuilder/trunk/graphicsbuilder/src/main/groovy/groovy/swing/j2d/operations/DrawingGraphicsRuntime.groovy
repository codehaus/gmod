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

package groovy.swing.j2d.operations

import java.awt.BasicStroke
import java.awt.Stroke
import java.awt.Shape
import java.awt.geom.Area
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class DrawingGraphicsRuntime extends AbstractDisplayableGraphicsRuntime {
   private Shape boundingShape
   private def stroke
   private Shape shape
   private Shape locallyTransformedShape
   private Shape globallyTransformedShape
    
   DrawingGraphicsRuntime( GraphicsOperation operation, GraphicsContext context ){
      super( operation, context )
   }
   
   /**
    * Returns the bounding shape including stroked border.<p>
    * 
    * @return a java.awt.Shape
    */
   public def getBoundingShape() {
      if( !boundingShape ){
         def st = getStroke()
         boundingShape = new Area(getGloballyTransformedShape())
         boundingShape.add(new Area(st.createStrokedShape(boundingShape)))
      }
      boundingShape
   }
   
   /**
    * Returns a java.awt.Stroke.<p>
    * Stroke will be determined with the following order<ol>
    * <li>nested stroke operation</li>
    * <li>borderWidth property (inherited from group too)</li>
    * <li>default stroke on Graphics object</li>
    * </ol>
    * 
    * @return a java.awt.Stroke
    */
   public def getStroke() {
      if( stroke == null ){
         def s = operation.findLast { it instanceof StrokeProvider }      
         def bw = getBorderWidth()
         
         if( s ){
            stroke = s.stroke
         }else if( bw ){
            def ps = context?.g?.stroke
            if( ps instanceof BasicStroke ){
               stroke = ps.derive(width:bw)
            }else{
               stroke = new BasicStroke( bw as float )
            }
         }else{
            stroke = context?.g?.stroke
         }
      }
      stroke
   }
   
   /**
    * Returns the shape to be drawn.<p>
    * 
    * @return a java.awt.Shape
    */
   public Shape getShape() {
      if( !shape ){
         shape = operation.getShape(context)
      }
      shape
   }
   
   /**
    * Returns the shape after applying local transformations.<p>
    * 
    * @return a java.awt.Shape transformed by any local transformations
    */
   public Shape getLocallyTransformedShape() {
      if( !locallyTransformedShape ){
         if( operation.transformations && !operation.transformations.empty ){
            locallyTransformedShape = operation.transformations.apply( getShape() )
         }else{
            locallyTransformedShape = getShape()
         }
      }
      locallyTransformedShape
   }
   
   /**
    * Returns the shape after applying global transformations.<p>
    * 
    * @return a java.awt.Shape transformed by any global transformations
    */
   public Shape getGloballyTransformedShape() {
      if( !globallyTransformedShape ){
         if( operation.globalTransformations && !operation.globalTransformations.empty ){
            globallyTransformedShape = operation.globalTransformations.apply( getLocallyTransformedShape() )
         }else{
            globallyTransformedShape = getLocallyTransformedShape()
         }
      }
      globallyTransformedShape
   }
}