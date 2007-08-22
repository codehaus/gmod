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

import groovy.swing.j2d.impl.DelegatingGraphicsOperation;
import groovy.swing.j2d.impl.FillSupportGraphicsOperation;
import groovy.swing.j2d.impl.StrokingAndFillingGraphicsOperation;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class PathGraphicsOperation extends DelegatingGraphicsOperation implements
      FillSupportGraphicsOperation {
   private List<PathOperation> pathOperations = new ArrayList<PathOperation>();

   public PathGraphicsOperation() {
      super( "path", new String[0], new StrokingAndFillingGraphicsOperation( "draw",
            new String[] { "shape" } ) );
   }

   public void addPathOperation( PathOperation operation ) {
      pathOperations.add( operation );
   }

   public Shape getClip( Graphics2D g, ImageObserver observer ) {
      Path2D path = new GeneralPath();
      path.moveTo( 0, 0 );
      for( PathOperation pathOperation : pathOperations ){
         pathOperation.apply( path );
      }
      path.closePath();
      return path;
   }

   protected void setupDelegateProperties( Graphics2D g, ImageObserver observer ) {
      InvokerHelper.setProperty( getDelegate(), "shape", getClip( g, observer ) );
   }

   public static class CurveTo implements PathOperation {
      private double x1;
      private double x2;
      private double x3;
      private double y1;
      private double y2;
      private double y3;

      public void apply( Path2D path ) {
         path.curveTo( x1, y1, x2, y2, x3, y3 );
      }

      public double getX1() {
         return x1;
      }

      public double getX2() {
         return x2;
      }

      public double getX3() {
         return x3;
      }

      public double getY1() {
         return y1;
      }

      public double getY2() {
         return y2;
      }

      public double getY3() {
         return y3;
      }

      public void setX1( double x1 ) {
         this.x1 = x1;
      }

      public void setX2( double x2 ) {
         this.x2 = x2;
      }

      public void setX3( double x3 ) {
         this.x3 = x3;
      }

      public void setY1( double y1 ) {
         this.y1 = y1;
      }

      public void setY2( double y2 ) {
         this.y2 = y2;
      }

      public void setY3( double y3 ) {
         this.y3 = y3;
      }

      public String toString() {
         return new StringBuffer( "curveTo(x1:" ).append( x1 )
               .append( ", y1:" )
               .append( y1 )
               .append( ", x2:" )
               .append( x2 )
               .append( ", y2:" )
               .append( y2 )
               .append( ", x3:" )
               .append( x3 )
               .append( ", y3:" )
               .append( y3 )
               .append( ")" )
               .toString();
      }
   }

   public static class LineTo implements PathOperation {
      private double x;
      private double y;

      public void apply( Path2D path ) {
         path.lineTo( x, y );
      }

      public double getX() {
         return x;
      }

      public double getY() {
         return y;
      }

      public void setX( double x ) {
         this.x = x;
      }

      public void setY( double y ) {
         this.y = y;
      }

      public String toString() {
         return new StringBuffer( "lineTo(x1:" ).append( x )
               .append( ", y:" )
               .append( y )
               .append( ")" )
               .toString();
      }
   }

   public static class MoveTo implements PathOperation {
      private double x;
      private double y;

      public void apply( Path2D path ) {
         path.moveTo( x, y );
      }

      public double getX() {
         return x;
      }

      public double getY() {
         return y;
      }

      public void setX( double x ) {
         this.x = x;
      }

      public void setY( double y ) {
         this.y = y;
      }

      public String toString() {
         return new StringBuffer( "lineTo(x1:" ).append( x )
               .append( ", y:" )
               .append( y )
               .append( ")" )
               .toString();
      }
   }

   public interface PathOperation {
      void apply( Path2D path );
   }

   public static class QuadTo implements PathOperation {
      private double x1;
      private double x2;
      private double y1;
      private double y2;

      public void apply( Path2D path ) {
         path.quadTo( x1, y1, x2, y2 );
      }

      public double getX1() {
         return x1;
      }

      public double getX2() {
         return x2;
      }

      public double getY1() {
         return y1;
      }

      public double getY2() {
         return y2;
      }

      public void setX1( double x1 ) {
         this.x1 = x1;
      }

      public void setX2( double x2 ) {
         this.x2 = x2;
      }

      public void setY1( double y1 ) {
         this.y1 = y1;
      }

      public void setY2( double y2 ) {
         this.y2 = y2;
      }

      public String toString() {
         return new StringBuffer( "quadeTo(x1:" ).append( x1 )
               .append( ", y1:" )
               .append( y1 )
               .append( ", x2:" )
               .append( x2 )
               .append( ", y2:" )
               .append( y2 )
               .toString();
      }
   }
}