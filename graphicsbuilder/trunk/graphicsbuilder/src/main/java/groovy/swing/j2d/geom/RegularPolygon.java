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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class RegularPolygon implements Shape, Cloneable, Centered {
   private float angle;
   private float cx;
   private float cy;
   private GeneralPath path;
   private Point2D[] points;
   private float radius;
   private int sides;

   public RegularPolygon( float cx, float cy, float radius, int sides ) {
      this( cx, cy, radius, sides, 0 );
   }

   public RegularPolygon( float cx, float cy, float radius, int sides, float angle ) {
      if( sides < 3 ){
         throw new IllegalArgumentException( "sides can not be less than 3" );
      }
      if( angle < 0 || angle > 360 ){
         throw new IllegalArgumentException( "angle can not be less than 0 or greater than 360" );
      }
      this.cx = cx;
      this.cy = cy;
      this.radius = radius;
      this.sides = sides;
      this.angle = angle;
      calculatePath();
   }

   public Object clone() {
      return new RegularPolygon( cx, cy, radius, sides, angle );
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

   public Point2D[] getPoints() {
      return points;
   }

   public float getRadius() {
      return radius;
   }

   public int getSides() {
      return sides;
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return path.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return path.intersects( r );
   }

   private void calculatePath() {
      float t = 360 / sides;
      float a = angle;
      points = new Point2D[sides];
      path = new GeneralPath();
      for( int i = 0; i < sides; i++ ){
         float ra = (float)Math.toRadians( a );
         float x = (float) Math.abs( radius * Math.cos( ra ) );
         float y = (float) Math.abs( radius * Math.sin( ra ) );
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
         if( i == 0 ){
            path.moveTo( x, y );
         }else{
            path.lineTo( x, y );
         }
         points[i] = new Point2D.Float( x, y );
         a += t;
         a = a > 360 ? a - 360 : a;
      }
      path.closePath();
   }
}