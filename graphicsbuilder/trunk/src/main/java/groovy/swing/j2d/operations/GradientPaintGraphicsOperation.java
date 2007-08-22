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

import groovy.swing.j2d.GraphicsBuilder;
import groovy.swing.j2d.impl.DelegatingGraphicsOperation;
import groovy.swing.j2d.impl.FillingGraphicsOperation;
import groovy.swing.j2d.impl.MethodInvokingGraphicsOperation;
import groovy.swing.j2d.impl.NullValue;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.ImageObserver;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GradientPaintGraphicsOperation extends DelegatingGraphicsOperation implements
      FillingGraphicsOperation {
   private Paint paint;

   public GradientPaintGraphicsOperation() {
      super( "gradientPaint", new String[] { "x1", "y1", "color1", "x2", "y2", "color2", "cycle" },
            new MethodInvokingGraphicsOperation( "setPaint", new String[] { "paint" } ) );
   }

   public Paint getPaint() {
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
                  + "' for 'gradientPaint' has no value" );
         }
      }
   }

   protected void setupDelegateProperties(Graphics2D g, ImageObserver observer) {
      float x1 = ((Number) getParameterValue( "x1" )).floatValue();
      float x2 = ((Number) getParameterValue( "x2" )).floatValue();
      float y1 = ((Number) getParameterValue( "y1" )).floatValue();
      float y2 = ((Number) getParameterValue( "y2" )).floatValue();
      Object c1 = getParameterValue( "color1" );
      Object c2 = getParameterValue( "color2" );
      Color color1 = null;
      Color color2 = null;
      if( c1 instanceof String ){
         color1 = GraphicsBuilder.getColor( c1 );
      }else if( c1 instanceof Color ){
         color1 = (Color) c1;
      }
      if( c2 instanceof String ){
         color2 = GraphicsBuilder.getColor( c2 );
      }else if( c2 instanceof Color ){
         color2 = (Color) c2;
      }

      boolean cycle = false;
      if( parameterHasValue( "cycle" ) ){
         Object c = getParameterValue( "cycle" );
         if( c instanceof Boolean ){
            cycle = ((Boolean) c).booleanValue();
         }
      }
      paint = new GradientPaint( x1, y1, color1, x2, y2, color2, cycle );
      InvokerHelper.setProperty( getDelegate(), "paint", paint );
   }
}