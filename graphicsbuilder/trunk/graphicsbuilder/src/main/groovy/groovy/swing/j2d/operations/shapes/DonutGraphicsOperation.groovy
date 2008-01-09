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

import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.geom.RegularPolygon

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class DonutGraphicsOperation extends AbstractShapeGraphicsOperation {
   protected static required = super.required + ['cx','cy','or','ir']
   protected static optional = super.optional + ['sides','angle']

   // properties
   def cx = 20
   def cy = 20
   def or = 20
   def ir = 10
   def sides
   def angle

   private Shape shape

   DonutGraphicsOperation(){
      super("donut")
   }

   public Shape getShape( GraphicsContext context ){
      if( !shape ){ calculateShape(context) }
      shape
   }

   protected void localPropertyChange( PropertyChangeEvent event ){
      super.localPropertyChange( event )
      shape = null
   }

   private void calculateShape( GraphicsContext context ){
      if( ir >= or ){
         throw new IllegalArgumentException("donut.ir can not be greater or equal than donut.or")
      }
      if( ir <= 0 || or <= 0 ){
         throw new IllegalArgumentException("donut.[ir|or] can not be equal or less than zero")
      }

      def outerShape
      def innerShape

      if( sides ){
         if( angle != null ){
            outerShape = new RegularPolygon( cx as double,
                                             cy as double,
                                             or as double,
                                             sides as int,
                                             angle as double )
            innerShape = new RegularPolygon( cx as double,
                                             cy as double,
                                             ir as double,
                                             sides as int,
                                             angle as double )
         }else{
            outerShape = new RegularPolygon( cx as double,
                                             cy as double,
                                             or as double,
                                             sides as int )
            innerShape = new RegularPolygon( cx as double,
                                             cy as double,
                                             ir as double,
                                             sides as int )
         }
      }else{
         outerShape = new Ellipse2D.Double( (cx - or) as double,
                                            (cy - or) as double,
                                            (or * 2) as double,
                                            (or * 2) as double )
         innerShape = new Ellipse2D.Double( (cx - ir) as double,
                                            (cy - ir) as double,
                                            (ir * 2) as double,
                                            (ir * 2) as double )
      }

      shape = new Area(outerShape)
      shape.subtract(new Area(innerShape))
   }
}