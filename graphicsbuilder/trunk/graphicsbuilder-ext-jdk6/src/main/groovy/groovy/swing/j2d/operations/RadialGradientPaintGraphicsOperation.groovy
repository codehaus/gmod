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

import groovy.swing.j2d.GraphicsContext

import java.awt.Color
import java.awt.Paint
import java.awt.RadialGradientPaint
import java.awt.Rectangle
import java.awt.MultipleGradientPaint.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class RadialGradientPaintGraphicsOperation extends AbstractGraphicsOperation implements
     GradientSupport {
   protected static required = ["cx","cy","fx","fy","radius"]
   protected static optional = ["cycle","apply"]

   private Paint paint
   private def stops = []

   // properties
   def cx
   def cy
   def fx
   def fy
   def radius
   def cycle
   def apply = true

   RadialGradientPaintGraphicsOperation() {
      super( "radialGradient" )
   }

   public void addStop( GradientStop stop ) {
      stops.add( stop )
   }

   public Paint adjustPaintToBounds( Rectangle bounds ) {
      return getPaint()
   }

   public Paint getPaint() {
      int n = stops.size()
      float[] fractions = new float[n]
      Color[] colors = new Color[n]
      n.times { i ->
         GradientStop stop = stops[i]
         fractions[i] = stop.offset
         colors[i] = stop.color
      }

      fx = fx == null ? cx: fx
      fy = fy == null ? cy: fy

      if( cycle != null ){
         paint = new RadialGradientPaint( cx as float,
                                          cy as float,
                                          radius as float,
                                          fx as float,
                                          fy as float,
                                          fractions,
                                          colors,
                                          getCycleMethod() )
      }else{
         paint = new RadialGradientPaint( cx as float,
                                          cy as float,
                                          radius as float,
                                          fx as float,
                                          fy as float,
                                          fractions,
                                          colors,
                                          CycleMethod.NO_CYCLE )
      }
      return paint
   }

   public void execute( GraphicsContext context){
      if( apply ) context.g.setPaint( getPaint() )
   }

   private def getCycleMethod() {
      if( cycle instanceof CycleMethod ){
         return cycle
      }else if( cycle instanceof String ){
         if( "nocycle".compareToIgnoreCase( cycle ) == 0 ){
            return CycleMethod.NO_CYCLE
         }else if( "reflect".compareToIgnoreCase( cycle ) == 0 ){
            return CycleMethod.REFLECT
         }else if( "repeat".compareToIgnoreCase( cycle ) == 0 ){
            return CycleMethod.REPEAT
         }else{
            throw new IllegalStateException( "'cycle=" + cycle
                  + "' is not one of [nocycle,reflect,repeat]" )
         }
      }
      throw new IllegalStateException( "'cycle' value is not a String nor a CycleMethod" );
   }
}
