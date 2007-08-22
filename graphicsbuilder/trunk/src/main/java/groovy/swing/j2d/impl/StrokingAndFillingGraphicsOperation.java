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

package groovy.swing.j2d.impl;

import groovy.swing.j2d.GraphicsBuilder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.image.ImageObserver;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StrokingAndFillingGraphicsOperation extends MethodInvokingGraphicsOperation implements
      FillSupportGraphicsOperation {
   private Paint previousPaint;
   private Stroke previousStroke;

   public StrokingAndFillingGraphicsOperation( String methodName, String[] arguments ) {
      super( methodName, arguments, new String[] { "fill", "color", "strokeWidth" } );
   }

   protected void afterExecute( Graphics2D g ) {
      if( previousStroke != null ){
         g.setStroke( previousStroke );
      }
      if( previousPaint != null ){
         g.setPaint( previousPaint );
      }
   }

   protected void executeOptional( Graphics2D g, ImageObserver observer ) {
      if( parameterHasValue( "fill" ) ){
         Object fill = getParameterValue( "fill" );
         if( fill instanceof Boolean && ((Boolean) fill).booleanValue() ){
            invokeFillMethod( g );
         }else if( fill instanceof Color ){
            // Color is a sunclass of Paint
            // we need to check it first
            Color color = g.getColor();
            g.setColor( (Color) fill );
            invokeFillMethod( g );
            g.setColor( color );
         }else if( fill instanceof Paint ){
            Paint paint = g.getPaint();
            g.setPaint( (Paint) fill );
            invokeFillMethod( g );
            g.setPaint( paint );
         }else if( fill instanceof String ){
            Color color = g.getColor();
            g.setColor( GraphicsBuilder.getColor( fill ) );
            invokeFillMethod( g );
            g.setColor( color );
         }else if( fill instanceof PaintSupportGraphicsOperation ){
            Paint paint = g.getPaint();
            PaintSupportGraphicsOperation psgo = (PaintSupportGraphicsOperation) fill;
            Paint p = psgo.adjustPaintToBounds( getClip(g, observer).getBounds() );
            if( p != null ){
               g.setPaint( p );
            }else{
               psgo.execute( g, observer );
               p = psgo.adjustPaintToBounds( getClip(g, observer).getBounds() );
            }
            invokeFillMethod( g );
            g.setPaint( paint );
         }
      }
      if( parameterHasValue( "color" ) ){
         Object value = getParameterValue( "color" );
         if( value instanceof String ){
            previousPaint = g.getPaint();
            g.setPaint( GraphicsBuilder.getColor( (String) value ) );
         }else if( value instanceof Paint ){
            previousPaint = g.getPaint();
            g.setPaint( (Paint) value );
         }
      }
      if( parameterHasValue( "strokeWidth" ) ){
         previousStroke = g.getStroke();
         g.setStroke( new BasicStroke( ((Number) getParameterValue( "strokeWidth" )).floatValue() ) );
      }
   }

   private void invokeFillMethod( Graphics2D g ) {
      try{
         invokeGraphingMethod( g, getMethodName().replaceAll( "draw", "fill" ) );
      }catch( Exception e ){
         // ignore ??
      }
   }
}