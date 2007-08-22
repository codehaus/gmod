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

package groovy.swing.j2d.operations;

import groovy.swing.j2d.impl.AbstractGraphicsOperation;
import groovy.swing.j2d.impl.FillingGraphicsOperation;
import groovy.swing.j2d.impl.GradientStop;
import groovy.swing.j2d.impl.GradientSupportGraphicsOperation;
import groovy.swing.j2d.impl.NullValue;
import groovy.swing.j2d.impl.VerifiableGraphicsOperation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class LinearGradientPaintGraphicsOperation extends AbstractGraphicsOperation implements
      GradientSupportGraphicsOperation, FillingGraphicsOperation, VerifiableGraphicsOperation {
   public LinearGradientPaintGraphicsOperation() {
      super( "linearGradient", new String[] { "x1", "x2", "y1", "y2", "stops", "cycle" } );
      setProperty( "stops", new ArrayList() );
   }

   @SuppressWarnings("unchecked")
   public void addStop( GradientStop stop ) {
      List<GradientStop> stops = (List) getParameterValue( "stops" );
      stops.add( stop );
   }

   public void execute( Graphics2D g, ImageObserver observer ) {
      g.setPaint( getPaint() );
   }

   @SuppressWarnings("unchecked")
   public Paint getPaint() {
      Paint paint = null;
      int x1 = ((Number) getParameterValue( "x1" )).intValue();
      int x2 = ((Number) getParameterValue( "x2" )).intValue();
      int y1 = ((Number) getParameterValue( "y1" )).intValue();
      int y2 = ((Number) getParameterValue( "y2" )).intValue();
      List<GradientStop> stops = (List) getParameterValue( "stops" );

      float[] fractions = new float[stops.size()];
      Color[] colors = new Color[stops.size()];
      for( int i = 0; i < fractions.length; i++ ){
         GradientStop stop = stops.get( i );
         fractions[i] = stop.getOffset();
         colors[i] = stop.getColor();
      }

      if( parameterHasValue( "cycle" ) ){
         paint = new LinearGradientPaint( x1, y1, x2, y2, fractions, colors, getCycle() );
      }else{
         paint = new LinearGradientPaint( x1, y1, x2, y2, fractions, colors );
      }

      return paint;
   }

   public void verify() {
      Map arguments = getParameters();
      for( Iterator entries = arguments.entrySet()
            .iterator(); entries.hasNext(); ){
         Map.Entry entry = (Map.Entry) entries.next();
         if( entry.getKey()
               .equals( "cycle" ) ){
            continue;
         }
         if( entry.getValue() instanceof NullValue ){
            throw new IllegalStateException( "Property '" + entry.getKey()
                  + "' for 'linearGradient' has no value" );
         }
      }
   }

   private CycleMethod getCycle() {
      Object cycleValue = getParameterValue( "cycle" );
      CycleMethod cycle = null;

      if( cycleValue instanceof CycleMethod ){
         cycle = (CycleMethod) cycleValue;
      }else if( cycleValue instanceof String ){
         if( "nocycle".compareToIgnoreCase( (String) cycleValue ) == 0 ){
            cycle = CycleMethod.NO_CYCLE;
         }else if( "reflect".compareToIgnoreCase( (String) cycleValue ) == 0 ){
            cycle = CycleMethod.REFLECT;
         }else if( "repeat".compareToIgnoreCase( (String) cycleValue ) == 0 ){
            cycle = CycleMethod.REPEAT;
         }else{
            throw new IllegalStateException( "'cycle=" + cycleValue
                  + "' is not one of [nocycle,reflect,repeat]" );
         }
      }else{
         throw new IllegalStateException( "'cycle' value is not a String nor a CycleMethod" );
      }

      return cycle;
   }

   public Paint adjustPaintToBounds( Rectangle bounds ) {
      Paint paint = null;
      int x1 = ((Number) getParameterValue( "x1" )).intValue();
      int x2 = ((Number) getParameterValue( "x2" )).intValue();
      int y1 = ((Number) getParameterValue( "y1" )).intValue();
      int y2 = ((Number) getParameterValue( "y2" )).intValue();
      List stops = (List) getParameterValue( "stops" );

      // TODO adjust the gradient to bounds
      // int rx1 = x1 < x2 ? x1 : x2;
      // int rx2 = x2 > x1 ? x2 : x1;
      // int ry1 = y1 < y2 ? y1 : y2;
      // int ry2 = y2 > y1 ? y1 : y2;

      float[] fractions = new float[stops.size()];
      Color[] colors = new Color[stops.size()];
      for( int i = 0; i < fractions.length; i++ ){
         GradientStop stop = (GradientStop) stops.get( i );
         fractions[i] = stop.getOffset();
         colors[i] = stop.getColor();
      }

      if( parameterHasValue( "cycle" ) ){
         paint = new LinearGradientPaint( x1, y1, x2, y2, fractions, colors, getCycle() );
      }else{
         paint = new LinearGradientPaint( x1, y1, x2, y2, fractions, colors );
      }

      return paint;
   }
}