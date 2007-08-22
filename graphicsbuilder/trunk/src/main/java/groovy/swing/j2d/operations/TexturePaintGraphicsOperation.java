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
import groovy.swing.j2d.impl.NullValue;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TexturePaintGraphicsOperation extends AbstractGraphicsOperation implements
      FillingGraphicsOperation {
   private Paint paint;

   public TexturePaintGraphicsOperation() {
      super( "texturePaint", new String[] { "x1", "y1", "x2", "y2", "image", "file", "url" } );
   }

   public void execute( Graphics2D g, ImageObserver observer ) {
      int x1 = ((Number) getParameterValue( "x1" )).intValue();
      int x2 = ((Number) getParameterValue( "x2" )).intValue();
      int y1 = ((Number) getParameterValue( "y1" )).intValue();
      int y2 = ((Number) getParameterValue( "y2" )).intValue();

      BufferedImage image = loadImage( g, observer );
      if( image != null ){
         paint = new TexturePaint( image, new Rectangle( x1, y1, x2 - x1, y2 - y1 ) );
         g.setPaint( paint );
      }
   }

   public Shape getClip(Graphics2D g, ImageObserver observer) {
      int x1 = ((Number) getParameterValue( "x1" )).intValue();
      int x2 = ((Number) getParameterValue( "x2" )).intValue();
      int y1 = ((Number) getParameterValue( "y1" )).intValue();
      int y2 = ((Number) getParameterValue( "y2" )).intValue();
      return new Rectangle( x1, y1, x2 - x1, y2 - y1 );
   }

   public Paint getPaint() {
      return paint;
   }

   public void verify() {
      Map arguments = getParameters();
      for( Iterator entries = arguments.entrySet()
            .iterator(); entries.hasNext(); ){
         Map.Entry entry = (Map.Entry) entries.next();
         String key = (String) entry.getKey();
         if( key.equals( "image" ) || key.equals( "file" ) || key.equals( "url" ) ){
            continue;
         }
         if( entry.getValue() instanceof NullValue ){
            throw new IllegalStateException( "Property '" + entry.getKey()
                  + "' for 'gradientPaint' has no value" );
         }
      }
      if( !arguments.containsKey( "image" ) || !arguments.containsKey( "file" )
            || !arguments.containsKey( "url" ) ){
         throw new IllegalStateException(
               "Must define  one of [image,file,url] for 'texturePaint()'" );
      }
   }

   private BufferedImage loadImage( Graphics2D g, ImageObserver observer ) {
      Image image = null;
      if( parameterHasValue( "image" ) ){
         if( image instanceof BufferedImage ){
            return (BufferedImage) getParameterValue( "image" );
         }
         image = (Image) getParameterValue( "image" );
      }else if( parameterHasValue( "file" ) ){
         image = Toolkit.getDefaultToolkit()
               .getImage( (String) getParameterValue( "file" ) );
      }else if( parameterHasValue( "url" ) ){
         image = Toolkit.getDefaultToolkit()
               .getImage( (URL) getParameterValue( "url" ) );
      }
      if( image.getWidth( observer ) <= 0 || image.getHeight( observer ) <= 0 ){
         return null;
      }
      BufferedImage bi = g.getDeviceConfiguration()
            .createCompatibleImage( image.getWidth( observer ), image.getHeight( observer ) );
      bi.getGraphics()
            .drawImage( image, 0, 0, observer );
      return bi;
   }
}