/*
 * Copyright 2007-2008 the original author or authors.
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
import java.awt.geom.Arc2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class Rays implements Shape, Cloneable, Centered {
   private float angle;
   private float cx;
   private float cy;
   private GeneralPath path;
   private float radius;
   private int rays;
   private float extent;
   private boolean rounded;

   public Rays( float cx, float cy, float radius, int rays ) {
      this( cx, cy, radius, rays, 0, 0.5f, false );
   }

   public Rays( float cx, float cy, float radius, int rays, float extent ) {
      this( cx, cy, radius, rays, 0, extent, false );
   }

   public Rays( float cx, float cy, float radius, int rays, float angle, float extent,
         boolean rounded ) {
      if( rays < 2 ){
         throw new IllegalArgumentException( "rays can not be less than 2" );
      }
      if( angle < 0 || angle > 360 ){
         throw new IllegalArgumentException( "angle can not be less than 0 or greater than 360" );
      }
      if( extent < 0 || extent > 1 ){
         throw new IllegalArgumentException( "extent must be inside the range [0..1]" );
      }
      this.cx = cx;
      this.cy = cy;
      this.radius = radius;
      this.rays = rays;
      this.angle = angle;
      this.extent = extent;
      this.rounded = rounded;
      calculatePath();
   }

   public Object clone() {
      return new Rays( cx, cy, radius, rays, angle, extent, rounded );
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

   public float getAngle() {
      return angle;
   }

   public Rectangle getBounds() {
      return path.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return path.getBounds2D();
   }

   public float getCx() {
      return cx;
   }

   public float getCy() {
      return cy;
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return path.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return path.getPathIterator( at, flatness );
   }

   public float getRadius() {
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
      float sides = rays * 2;
      float t = 360 / sides;
      float a = angle;
      float e = (extent * t * 2) - t;
      float[][] points = new float[rays * 2][];
      float[] angles = new float[rays * 2];
      for( int i = 0; i < sides; i++ ){
         float r = i % 2 == 0 ? a : a + e;
         r = r < 0 ? 360 + r : r;
         // r = r > 360 ? r - 360 : r;
         float ra = (float)Math.toRadians( r );
         float x = (float) Math.abs( radius * Math.cos( ra ) );
         float y = (float) Math.abs( radius * Math.sin( ra ) );
         if( r <= 90 || r > 360 ){
            x = cx + x;
            y = cy - y;
         }else if( r <= 180 ){
            x = cx - x;
            y = cy - y;
         }else if( r <= 270 ){
            x = cx - x;
            y = cy + y;
         }else if( r <= 360 ){
            x = cx + x;
            y = cy + y;
         }
         points[i] = new float[] { x, y };
         angles[i] = r;
         a += t;
         a = a > 360 ? a - 360 : a;
      }

      path = new GeneralPath();
      for( int i = 0; i < rays; i++ ){
         path.moveTo( cx, cy );
         path.lineTo( points[(2 * i)][0], points[(2 * i)][1] );
         if( rounded ){
            path.append( new Arc2D.Float( cx - radius, cy - radius, radius * 2, radius * 2,
                  angles[(2 * i)], t + e, Arc2D.OPEN ), true );
         }else{
            path.lineTo( points[(2 * i) + 1][0], points[(2 * i) + 1][1] );
         }
         path.closePath();
      }
   }
}