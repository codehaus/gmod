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
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class Arrow implements Shape, Cloneable {
   private double angle;
   private Shape arrow;
   private double depth;
   private double height;
   private double rise;
   private double width;
   private double x;
   private double y;

   public Arrow( double x, double y, double width, double height ) {
      this( x, y, width, height, height / 4, width / 2, 0 );
   }

   public Arrow( double x, double y, double width, double height, double angle ) {
      this( x, y, width, height, height / 4, width / 2, angle );
   }

   public Arrow( double x, double y, double width, double height, double rise, double depth ) {
      this( x, y, width, height, rise, depth, 0 );
   }

   public Arrow( double x, double y, double width, double height, double rise, double depth,
         double angle ) {
      if( depth < 0 || depth > 1 ){
         throw new IllegalArgumentException( "depth must be inside the range [0..1]" );
      }
      if( rise < 0 || rise > 1 ){
         throw new IllegalArgumentException( "rise must be inside the range [0..1]" );
      }
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.rise = rise;
      this.depth = depth;
      this.angle = angle;
      calculateArrow();
   }

   public Object clone() {
      return new Arrow( x, y, width, height, rise, depth, angle );
   }

   public boolean contains( double x, double y ) {
      return arrow.contains( x, y );
   }

   public boolean contains( double x, double y, double w, double h ) {
      return arrow.contains( x, y, w, h );
   }

   public boolean contains( Point2D p ) {
      return arrow.contains( p );
   }

   public boolean contains( Rectangle2D r ) {
      return arrow.contains( r );
   }

   public double getAngle() {
      return angle;
   }

   public Rectangle getBounds() {
      return arrow.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return arrow.getBounds2D();
   }

   public double getDepth() {
      return depth;
   }

   public double getHeight() {
      return height;
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return arrow.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return arrow.getPathIterator( at, flatness );
   }

   public double getRise() {
      return rise;
   }

   public double getWidth() {
      return width;
   }

   public double getX() {
      return x;
   }

   public double getY() {
      return y;
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return arrow.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return arrow.intersects( r );
   }

   private void calculateArrow() {
      double d = width * depth;
      double r = height * rise / 2;
      GeneralPath head = new GeneralPath();
      head.moveTo( x + d, y );
      head.lineTo( x + d, y + height );
      head.lineTo( x + width, y + (height / 2) );
      head.closePath();
      arrow = new Area( new Rectangle2D.Double( x, y + (height / 2) - r, d, r * 2 ) );
      ((Area) arrow).add( new Area( head ) );

      if( angle > 0 ){
         arrow = AffineTransform.getRotateInstance( Math.toRadians( 360 - angle ), x + (width / 2),
               y + (height / 2) )
               .createTransformedShape( arrow );
      }
   }
}