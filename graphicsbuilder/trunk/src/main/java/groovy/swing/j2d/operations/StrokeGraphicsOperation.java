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

import groovy.lang.Closure;
import groovy.swing.j2d.GraphicsBuilder;
import groovy.swing.j2d.impl.AbstractGraphicsOperation;
import groovy.swing.j2d.impl.FillingGraphicsOperation;
import groovy.swing.j2d.impl.MethodInvokingGraphicsOperation;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.ImageObserver;
import java.util.List;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StrokeGraphicsOperation extends AbstractGraphicsOperation implements
      FillingGraphicsOperation {
   private MethodInvokingGraphicsOperation colorDelegate;
   private MethodInvokingGraphicsOperation paintDelegate;
   private Stroke stroke;
   private MethodInvokingGraphicsOperation strokeDelegate;

   public StrokeGraphicsOperation() {
      super( "stroke", new String[] { "paint", "color", "width", "cap", "join", "miterlimit",
            "dash", "dashphase" } );
      strokeDelegate = new MethodInvokingGraphicsOperation( "setStroke", new String[] { "stroke" } );
      paintDelegate = new MethodInvokingGraphicsOperation( "setPaint", new String[] { "paint" } );
      colorDelegate = new MethodInvokingGraphicsOperation( "setColor", new String[] { "color" } );
   }

   public void execute( Graphics2D g, ImageObserver observer ) {
      if( parameterHasValue( "color" ) ){
         Object color = getParameterValue( "color" );
         if( color instanceof String ){
            color = GraphicsBuilder.getColor( color );
         }
         colorDelegate.setProperty( "color", color );
         colorDelegate.execute( g, observer );
      }
      if( parameterHasValue( "paint" ) ){
         Object paint = getParameterValue( "paint" );
         if( paint instanceof String ){
            paint = GraphicsBuilder.getColor( paint );
         }
         paintDelegate.setProperty( "paint", paint );
         paintDelegate.execute( g, observer );
      }

      if( stroke == null ){
         createStroke();
      }

      strokeDelegate.setProperty( "stroke", stroke );
      strokeDelegate.execute( g, observer );
   }

   public Shape getClip(Graphics2D g, ImageObserver observer) {
      throw null;
   }

   public void setPaint( Object paint ) {
      if( paint instanceof Paint ){
         super.setProperty( "paint", paint );
      }else if( paint instanceof String ){
         super.setProperty( "paint", GraphicsBuilder.getColor( paint ) );
      }
   }

   public void verify() {
      // because all values are actually optional
      return;
   }

   private void createStroke() {
      boolean hasWidth = parameterHasValue( "width" );
      boolean hasCap = parameterHasValue( "cap" );
      boolean hasJoin = parameterHasValue( "join" );
      boolean hasMiterLimit = parameterHasValue( "miterlimit" );
      boolean hasDash = parameterHasValue( "dash" );
      boolean hasDashPhase = parameterHasValue( "dashphase" );

      if( hasWidth ){
         int width = ((Number) getParameterValue( "width" )).intValue();
         if( hasCap && hasJoin ){
            int cap = getCap();
            int join = getJoin();
            if( hasMiterLimit ){
               int miterlimit = ((Number) getParameterValue( "miterlimit" )).intValue();
               if( hasDash && hasDashPhase ){
                  float[] dash = getDash();
                  float dashphase = ((Number) getParameterValue( "dashphase" )).floatValue();
                  stroke = new BasicStroke( width, cap, join, miterlimit, dash, dashphase );
               }else{
                  stroke = new BasicStroke( width, cap, join, miterlimit );
               }
            }else{
               stroke = new BasicStroke( width, cap, join );
            }
         }else{
            stroke = new BasicStroke( width );
         }
      }else{
         stroke = new BasicStroke();
      }
   }

   private int getCap() {
      int cap = 0;
      Object capvalue = getParameterValue( "cap" );
      if( capvalue instanceof Number ){
         cap = ((Number) capvalue).intValue();
      }else if( capvalue instanceof String ){
         if( "butt".compareToIgnoreCase( (String) capvalue ) == 0 ){
            cap = BasicStroke.CAP_BUTT;
         }else if( "round".compareToIgnoreCase( (String) capvalue ) == 0 ){
            cap = BasicStroke.CAP_ROUND;
         }else if( "square".compareToIgnoreCase( (String) capvalue ) == 0 ){
            cap = BasicStroke.CAP_SQUARE;
         }else{
            throw new IllegalStateException( "'cap=" + capvalue
                  + "' is not one of [butt,round,square]" );
         }
      }else{
         throw new IllegalStateException( "'cap' value is not a String nor an int" );
      }
      return cap;
   }

   private float[] getDash() {
      List dash = (List) getParameterValue( "dash" );
      float[] array = new float[dash.size()];
      for( int i = 0; i < array.length; i++ ){
         Object value = dash.get( i );
         if( value instanceof Closure ){
            value = ((Closure) value).call();
         }
         if( value instanceof Number ){
            array[i] = ((Number) value).floatValue();
         }else{
            throw new IllegalStateException( "dash[" + i + "] is not a Number" );
         }
      }
      return array;
   }

   private int getJoin() {
      int join = 0;
      Object joinvalue = getParameterValue( "join" );
      if( joinvalue instanceof Number ){
         join = ((Number) joinvalue).intValue();
      }else if( joinvalue instanceof String ){
         if( "bevel".compareToIgnoreCase( (String) joinvalue ) == 0 ){
            join = BasicStroke.JOIN_BEVEL;
         }else if( "round".compareToIgnoreCase( (String) joinvalue ) == 0 ){
            join = BasicStroke.JOIN_ROUND;
         }else if( "miter".compareToIgnoreCase( (String) joinvalue ) == 0 ){
            join = BasicStroke.JOIN_MITER;
         }else{
            throw new IllegalStateException( "'join=" + joinvalue
                  + "' is not one of [bevel,miter,round]" );
         }
      }else{
         throw new IllegalStateException( "'join' value is not a String nor an int" );
      }
      return join;
   }
}