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
import java.awt.Paint;
import java.awt.RadialGradientPaint;
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
public class RadialGradientPaintGraphicsOperation extends AbstractGraphicsOperation implements
      GradientSupportGraphicsOperation, FillingGraphicsOperation, VerifiableGraphicsOperation {
   private Paint paint;

   public RadialGradientPaintGraphicsOperation() {
      super( "radialGradient", new String[] { "cx", "cy", "fx", "fy", "radius", "stops", "cycle" } );
      setProperty( "stops", new ArrayList() );
   }

   @SuppressWarnings("unchecked")
   public void addStop( GradientStop stop ) {
      List<GradientStop> stops = (List) getParameterValue( "stops" );
      stops.add( stop );
   }

   public Paint adjustPaintToBounds( Rectangle bounds ) {
      return paint;
   }

   public void execute( Graphics2D g, ImageObserver observer ) {
      float cx = ((Number) getParameterValue( "cx" )).floatValue();
      float cy = ((Number) getParameterValue( "cy" )).floatValue();
      float fx = ((Number) getParameterValue( "fx" )).floatValue();
      float fy = ((Number) getParameterValue( "fy" )).floatValue();
      float radius = ((Number) getParameterValue( "radius" )).floatValue();
      List stops = (List) getParameterValue( "stops" );

      float[] fractions = new float[stops.size()];
      Color[] colors = new Color[stops.size()];
      for( int i = 0; i < fractions.length; i++ ){
         GradientStop stop = (GradientStop) stops.get( i );
         fractions[i] = stop.getOffset();
         colors[i] = stop.getColor();
      }

      if( parameterHasValue( "cycle" ) ){
         paint = new RadialGradientPaint( cx, cy, radius, fx, fy, fractions, colors, getCycle() );
      }else{
         paint = new RadialGradientPaint( cx, cy, radius, fx, fy, fractions, colors,
               CycleMethod.NO_CYCLE );
      }
      g.setPaint( paint );
   }

   public Paint getPaint() {
      return paint;
   }

   @SuppressWarnings("unchecked")
   public void verify() {
      Map arguments = getParameters();
      for( Iterator entries = arguments.entrySet()
            .iterator(); entries.hasNext(); ){
         Map.Entry entry = (Map.Entry) entries.next();
         String name = (String) entry.getKey();
         Object value = entry.getValue();
         if( name.equals( "cycle" ) ){
            continue;
         }else if( name.equals( "fx" ) && value instanceof NullValue ){
            entry.setValue( arguments.get( "cx" ) );
         }else if( name.equals( "fy" ) && value instanceof NullValue ){
            entry.setValue( arguments.get( "cy" ) );
         }else if( value instanceof NullValue ){
            throw new IllegalStateException( "Property '" + entry.getKey()
                  + "' for 'radialGradient' has no value" );
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
}