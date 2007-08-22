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
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.image.ImageObserver;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StrokingGraphicsOperation extends MethodInvokingGraphicsOperation {
   private Paint previousPaint;
   private Stroke previousStroke;

   public StrokingGraphicsOperation( String methodName, String[] arguments ) {
      super( methodName, arguments, new String[] { "color", "strokeWidth" } );
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
      if( parameterHasValue( "color" ) ){
         Object value = getParameterValue( "color" );
         if( value instanceof String ){
            previousPaint = g.getPaint();
            g.setPaint( GraphicsBuilder.getColor( value ) );
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
}