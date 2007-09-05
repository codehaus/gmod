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

import groovy.swing.j2d.impl.AbstractGraphicsOperation
import groovy.swing.j2d.impl.GradientStop
import groovy.swing.j2d.impl.GradientSupportGraphicsOperation

import java.awt.Color
import java.awt.Graphics2D
import java.awt.LinearGradientPaint
import java.awt.Paint
import java.awt.Rectangle
import java.awt.MultipleGradientPaint.*
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.image.ImageObserver

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class LinearGradientPaintGraphicsOperation extends AbstractGraphicsOperation implements
    GradientSupportGraphicsOperation {
   def x1
   def x2
   def y1
   def y2
   def cycle
   private List stops = []

   static supportsFill = true

   // TODO support isDirty()
   LinearGradientPaintGraphicsOperation() {
      super( "linearGradient", ["x1", "x2", "y1", "y2"] as String[], ["cycle"] as String[] )
   }

   public void addStop( GradientStop stop ) {
      stops.add( stop );
   }

   public Paint getPaint() {
      Paint paint = null
      double x1 = getParameterValue( "x1" )
      double x2 = getParameterValue( "x2" )
      double y1 = getParameterValue( "y1" )
      double y2 = getParameterValue( "y2" )

      int n = stops.size()
      float[] fractions = new float[n]
      Color[] colors = new Color[n]
      n.times { i ->
         GradientStop stop = stops[i]
         fractions[i] = stop.offset
         colors[i] = stop.color
      }

      if( parameterHasValue( "cycle" ) ){
         paint = new LinearGradientPaint( new Point2D.Double(x1,y1), new Point2D.Double(x2,y2),
               fractions, colors, getCycleMethod() )
      }else{
         paint = new LinearGradientPaint( new Point2D.Double(x1,y1), new Point2D.Double(x2,y2),
               fractions, colors )
      }

      return paint
   }

   public void verify() {
      Map parameters = getParameterMap()
      parameters.each { k, v ->
          if( !k.equals( "cycle" ) ){
              if( !v ){
                  throw new IllegalStateException( "Property '${k}' for 'linearPaint' has no value" );
              }
          }
      }
   }

   public Paint adjustPaintToBounds( Rectangle bounds ) {
      Paint paint = null
      double x1 = getParameterValue( "x1" )
      double x2 = getParameterValue( "x2" )
      double y1 = getParameterValue( "y1" )
      double y2 = getParameterValue( "y2" )

      int n = stops.size()
      float[] fractions = new float[n]
      Color[] colors = new Color[n]
      n.times { i ->
         GradientStop stop = stops[i]
         fractions[i] = stop.offset
         colors[i] = stop.color
      }

      if( parameterHasValue( "cycle" ) ){
         paint = new LinearGradientPaint( new Point2D.Double(x1,y1), new Point2D.Double(x2,y2),
               fractions, colors, getCycleMethod(), ColorSpaceType.SRGB, computeTransform(bounds) )
      }else{
         paint = new LinearGradientPaint( new Point2D.Double(x1,y1), new Point2D.Double(x2,y2),
               fractions, colors, CycleMethod.NO_CYCLE, ColorSpaceType.SRGB, computeTransform(bounds) )
      }

      return paint
   }

   protected void doExecute( Graphics2D g, ImageObserver observer ){
      g.setPaint( getPaint() )
   }
/*
   private CycleMethod getCycleMethod() {
      Object cycleValue = getParameterValue( "cycle" )

      if( cycleValue instanceof CycleMethod ){
         return cycleValue
      }else if( cycleValue instanceof String ){
         if( "nocycle".compareToIgnoreCase( cycleValue ) == 0 ){
            return cycleMethod = CycleMethod.NO_CYCLE
         }else if( "reflect".compareToIgnoreCase( cycleValue ) == 0 ){
            return cycleMethod = CycleMethod.REFLECT
         }else if( "repeat".compareToIgnoreCase( cycleValue ) == 0 ){
            return cycleMethod = CycleMethod.REPEAT
         }else{
            throw new IllegalStateException( "'cycle=" + cycleValue
                  + "' is not one of [nocycle,reflect,repeat]" )
         }
      }else{
         throw new IllegalStateException( "'cycle' value is not a String nor a CycleMethod" )
      }
   }*/

   private CycleMethod getCycleMethod() {
      Object cycleValue = getParameterValue( "cycle" )
      CycleMethod cycle = null;

      if( cycleValue instanceof CycleMethod ){
         cycle = (CycleMethod) cycleValue
      }else if( cycleValue instanceof String ){
         if( "nocycle".compareToIgnoreCase( (String) cycleValue ) == 0 ){
            cycle = CycleMethod.NO_CYCLE
         }else if( "reflect".compareToIgnoreCase( (String) cycleValue ) == 0 ){
            cycle = CycleMethod.REFLECT
         }else if( "repeat".compareToIgnoreCase( (String) cycleValue ) == 0 ){
            cycle = CycleMethod.REPEAT
         }else{
            throw new IllegalStateException( "'cycle=" + cycleValue
                  + "' is not one of [nocycle,reflect,repeat]" )
         }
      }else{
         throw new IllegalStateException( "'cycle' value is not a String nor a CycleMethod" );
      }

      return cycle
   }

   private AffineTransform computeTransform( Rectangle bounds ){
      AffineTransform tx = AffineTransform.getScaleInstance( bounds.width as double, bounds.height as double )
      tx.preConcatenate( AffineTransform.getTranslateInstance(bounds.x as int, bounds.y as double) )
      return tx
   }
}