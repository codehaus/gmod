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
import java.awt.LinearGradientPaint
import java.awt.geom.Point2D
import java.awt.MultipleGradientPaint.*
import groovy.swing.j2d.PaintProvider
import groovy.swing.j2d.impl.AbstractLinearGradientPaintGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class LinearGradientPaintGraphicsOperation extends AbstractLinearGradientPaintGraphicsOperation implements
     MultipleGradientPaintProvider {
   protected static DEFAULT_CYCLE_VALUE = 'nocycle' 
    
   private def stops = []

   LinearGradientPaintGraphicsOperation() {
      super( "linearGradient" )
   }

   public void addStop( GradientStop stop ) {
      if( !stop ) return
      stops.add( stop )
      stop.addPropertyChangeListener( this )
   }

   public PaintProvider asCopy() {
      PaintProvider copy = super.asCopy()
      stops.each { stop ->
         copy.addStop( stop.copy() )
      }
      copy
   }

   protected Paint makePaint( x1, y1, x2, y2 ){
      int n = stops.size()
      float[] fractions = new float[n]
      Color[] colors = new Color[n]
      n.times { i ->
         GradientStop stop = stops[i]
         fractions[i] = stop.offset
         colors[i] = stop.color
      }

      return new LinearGradientPaint( new Point2D.Double(x1,y1),
                                      new Point2D.Double(x2,y2),
                                      fractions,
                                      colors,
                                      getCycleMethod() )
   }

   private def getCycleMethod() {
      if( !cycle ){ 
         return CycleMethod.NO_CYCLE
      }else if( cycle instanceof CycleMethod ){
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