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

package groovy.swing.j2d.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class Star implements Shape, Cloneable, Centered {
   private double angle;
   private int count;
   private double cx;
   private double cy;
   private double ir;
   private double or;
   private GeneralPath path;
   private Point2D[] points;

   public Star( double cx, double cy, double or, double ir, int count ) {
      this( cx, cy, or, ir, count, 0 );
   }

   public Star( double cx, double cy, double or, double ir, int count, double angle ) {
      if( angle < 0 || angle > 360 ){
         throw new IllegalArgumentException(
               "'angle' can not be less than 0 or greater than 360 [angle=" + angle + "]" );
      }
      if( ir >= or ){
         throw new IllegalArgumentException( "'ir' can not be equal greater than 'or' [ir=" + ir
               + ", or=" + or + "]" );
      }
      if( ir < 0 || or < 0 ){
         throw new IllegalArgumentException( "radiuses can not be less than zero [ir=" + ir
               + ", or=" + or + "]" );
      }
      if( count < 2 ){
         throw new IllegalArgumentException( "'count' can not be less than 2 [count=" + count + "]" );
      }

      this.cx = cx;
      this.cy = cy;
      this.or = or;
      this.ir = ir;
      this.count = count;
      this.angle = angle;
      calculatePath();
   }

   public Object clone() {
      return new Star( cx, cy, or, ir, count, angle );
   }

   public boolean contains( double x, double y ) {
      return path.contains( x, y );
   }

   public boolean contains( double x, double y, double w, double h ) {
      return path.contains( x, y, w, h );
   }

   public boolean contains( Point2D p ) {
      return path.contains( p );
   }

   public boolean contains( Rectangle2D r ) {
      return path.contains( r );
   }

   public Rectangle getBounds() {
      return path.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return path.getBounds2D();
   }

   public int getCount() {
      return count;
   }

   public double getCx() {
      return cx;
   }

   public double getCy() {
      return cy;
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return path.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return path.getPathIterator( at, flatness );
   }

   public Point2D[] getPoints() {
      return points;
   }

   public double getRadius() {
      return or;
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return path.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return path.intersects( r );
   }

   private void calculatePath() {
      double t = 360 / count;
      double a = angle + 90;
      double b = angle + 90 + (t / 2);
      points = new Point2D[count * 2];
      path = new GeneralPath();
      for( int i = 0; i < count; i++ ){
         double ra = Math.toRadians( a );
         double ox = Math.abs( or * Math.cos( ra ) );
         double oy = Math.abs( or * Math.sin( ra ) );
         if( a <= 90 ){
            ox = cx + ox;
            oy = cy - oy;
         }else if( a <= 180 ){
            ox = cx - ox;
            oy = cy - oy;
         }else if( a <= 270 ){
            ox = cx - ox;
            oy = cy + oy;
         }else if( a <= 360 ){
            ox = cx + ox;
            oy = cy + oy;
         }

         double rb = Math.toRadians( b );
         double ix = Math.abs( ir * Math.cos( rb ) );
         double iy = Math.abs( ir * Math.sin( rb ) );
         if( b <= 90 ){
            ix = cx + ix;
            iy = cy - iy;
         }else if( b <= 180 ){
            ix = cx - ix;
            iy = cy - iy;
         }else if( b <= 270 ){
            ix = cx - ix;
            iy = cy + iy;
         }else if( b <= 360 ){
            ix = cx + ix;
            iy = cy + iy;
         }

         if( i == 0 ){
            path.moveTo( ox, oy );
            path.lineTo( ix, iy );
         }else{
            path.lineTo( ox, oy );
            path.lineTo( ix, iy );
         }
         points[2 * i] = new Point2D.Double( ox, oy );
         points[(2 * i) + 1] = new Point2D.Double( ix, iy );
         a += t;
         a = a > 360 ? a - 360 : a;
         b += t;
         b = b > 360 ? b - 360 : b;
      }
      path.closePath();
   }
}