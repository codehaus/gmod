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
public class Rays implements Shape, Cloneable, Centered {
   private double angle;
   private double cx;
   private double cy;
   private GeneralPath path;
   private double radius;
   private int rays;

   public Rays( double cx, double cy, double radius, int rays ) {
      this( cx, cy, radius, rays, 0 );
   }

   public Rays( double cx, double cy, double radius, int rays, double angle ) {
      if( rays < 2 ){
         throw new IllegalArgumentException( "rays can not be less than 2" );
      }
      if( angle < 0 || angle > 360 ){
         throw new IllegalArgumentException( "angle can not be less than 0 or greater than 360" );
      }
      this.cx = cx;
      this.cy = cy;
      this.radius = radius;
      this.rays = rays;
      this.angle = angle;
      calculatePath();
   }

   public Object clone() {
      return new Rays( cx, cy, radius, rays, angle );
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

   public double getAngle() {
      return angle;
   }

   public Rectangle getBounds() {
      return path.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return path.getBounds2D();
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

   public double getRadius() {
      return radius;
   }

   public int getRays() {
      return rays;
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return path.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return path.intersects( r );
   }

   private void calculatePath() {
      double sides = rays * 2;
      double t = 360 / sides;
      double a = angle;
      double[][] points = new double[rays * 2][];
      for( int i = 0; i < sides; i++ ){
         double ra = Math.toRadians( a );
         double x = Math.abs( radius * Math.cos( ra ) );
         double y = Math.abs( radius * Math.sin( ra ) );
         if( a <= 90 ){
            x = cx + x;
            y = cy - y;
         }else if( a <= 180 ){
            x = cx - x;
            y = cy - y;
         }else if( a <= 270 ){
            x = cx - x;
            y = cy + y;
         }else if( a <= 360 ){
            x = cx + x;
            y = cy + y;
         }
         points[i] = new double[] { x, y };
         a += t;
         a = a > 360 ? a - 360 : a;
      }
      path = new GeneralPath();
      for( int i = 0; i < rays; i++ ){
         path.moveTo( cx, cy );
         path.lineTo( points[(2 * i)][0], points[(2 * i)][1] );
         path.lineTo( points[(2 * i) + 1][0], points[(2 * i) + 1][1] );
         path.closePath();
      }
   }
}