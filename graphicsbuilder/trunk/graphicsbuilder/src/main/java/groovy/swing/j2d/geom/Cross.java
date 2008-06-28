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
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class Cross implements Shape, Cloneable, Centered {
   private double angle;
   private Shape cross;
   private double cx;
   private double cy;
   private double radius;
   private double roundness;
   private double width;

   public Cross( double cx, double cy, double radius, double width ) {
      this( cx, cy, radius, width, 0, 0 );
   }

   public Cross( double cx, double cy, double radius, double width, double angle ) {
      this( cx, cy, radius, width, angle, 0 );
   }

   public Cross( double cx, double cy, double radius, double width, double angle, double roundness ) {
      if( width > radius * 2 ){
         throw new IllegalArgumentException( "width can not be greater than radius*2 ("
               + (radius * 2) + ")" );
      }
      if( roundness < 0 || roundness > 1 ){
         throw new IllegalArgumentException( "roundness must be inside the range [0..1]" );
      }
      this.cx = cx;
      this.cy = cy;
      this.radius = radius;
      this.width = width;
      this.angle = angle;
      this.roundness = roundness;
      calculatePath();
   }

   public Object clone() {
      return new Cross( cx, cy, radius, width, angle, roundness );
   }

   public boolean contains( double x, double y ) {
      return cross.contains( x, y );
   }

   public boolean contains( double x, double y, double w, double h ) {
      return cross.contains( x, y, w, h );
   }

   public boolean contains( Point2D p ) {
      return cross.contains( p );
   }

   public boolean contains( Rectangle2D r ) {
      return cross.contains( r );
   }

   public double getAngle() {
      return angle;
   }

   public Rectangle getBounds() {
      return cross.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return cross.getBounds2D();
   }

   public double getCx() {
      return cx;
   }

   public double getCy() {
      return cy;
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return cross.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return cross.getPathIterator( at, flatness );
   }

   public double getRadius() {
      return radius;
   }

   public double getRoundness() {
      return roundness;
   }

   public double getWidth() {
      return width;
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return cross.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return cross.intersects( r );
   }

   private void calculatePath() {
      double arcwh = width * roundness;
      Shape beam1 = new RoundRectangle2D.Double( cx - radius, cy - (width / 2), radius * 2, width,
            arcwh, arcwh );
      Shape beam2 = new RoundRectangle2D.Double( cx - (width / 2), cy - radius, width, radius * 2,
            arcwh, arcwh );
      cross = new Area( beam1 );
      ((Area) cross).add( new Area( beam2 ) );
      if( angle > 0 ){
         cross = AffineTransform.getRotateInstance( Math.toRadians( 360 - angle ), cx, cy )
               .createTransformedShape( cross );
      }
   }
}