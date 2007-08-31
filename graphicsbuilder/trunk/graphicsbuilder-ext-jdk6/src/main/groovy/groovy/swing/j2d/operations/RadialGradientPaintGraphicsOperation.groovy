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
import java.awt.Paint
import java.awt.RadialGradientPaint
import java.awt.Rectangle
import java.awt.MultipleGradientPaint.*
import java.awt.image.ImageObserver

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class RadialGradientPaintGraphicsOperation extends AbstractGraphicsOperation implements
     GradientSupportGraphicsOperation {
   private Paint paint
   private def stops = []
   def cx
   def cy
   def fx
   def fy
   def radius
   def cycle

   static supportsFill = true

   RadialGradientPaintGraphicsOperation() {
      super( "radialGradient", ["cx","cy","fx","fy","radius"] as String[], ["cycle"] as String[] )
   }

   public void addStop( GradientStop stop ) {
      stops.add( stop );
   }

   public Paint adjustPaintToBounds( Rectangle bounds ) {
      return getPaint()
   }

   public Paint getPaint() {
      float cx = getParameterValue( "cx" )
      float cy = getParameterValue( "cy" )
      float fx = getParameterValue( "fx" )
      float fy = getParameterValue( "fy" )
      float radius = getParameterValue( "radius" )

      int n = stops.size()
      float[] fractions = new float[n]
      Color[] colors = new Color[n]
      n.times { i ->
         GradientStop stop = stops[i]
         fractions[i] = stop.offset
         colors[i] = stop.color
      }

      if( parameterHasValue( "cycle" ) ){
         paint = new RadialGradientPaint( cx, cy, radius, fx, fy, fractions, colors, getCycleMethod() )
      }else{
         paint = new RadialGradientPaint( cx, cy, radius, fx, fy, fractions, colors,
               CycleMethod.NO_CYCLE )
      }
      return paint
   }

   public void verify() {
      if( !parameterHasValue("fx") ){ fx = cx }
      if( !parameterHasValue("fy") ){ fy = cy }
      Map parameters = getParameterMap()
      parameters.each { k, v ->
         if( k.equals( "cycle" ) ){/* optional */}
         else if( !v ){
            throw new IllegalStateException( "Property '${k}' for 'radialGradient' has no value" );
         }
      }
   }

   protected void doExecute( Graphics2D g, ImageObserver observer ){
      g.setPaint( adjustPaintToBounds( g.getClipBounds() ) )
   }

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
}