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
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class RoundPin implements Shape, Cloneable {
   private double angle;
   private double cx;
   private double cy;
   private double height;
   private Shape pin;
   private double radius;

   public RoundPin( double cx, double cy, double radius ) {
      this( cx, cy, radius, radius * 2, 0 );
   }

   public RoundPin( double cx, double cy, double radius, double height, double angle ) {
      this.cx = cx;
      this.cy = cy;
      this.radius = radius;
      this.height = height;
      this.angle = angle;
      calculatePath();
   }

   public Object clone() {
      return new RoundPin( cx, cy, radius, height, angle );
   }

   public boolean contains( double x, double y ) {
      return pin.contains( x, y );
   }

   public boolean contains( double x, double y, double w, double h ) {
      return pin.contains( x, y, w, h );
   }

   public boolean contains( Point2D p ) {
      return pin.contains( p );
   }

   public boolean contains( Rectangle2D r ) {
      return pin.contains( r );
   }

   public Rectangle getBounds() {
      return pin.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return pin.getBounds2D();
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return pin.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return pin.getPathIterator( at, flatness );
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return pin.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return pin.intersects( r );
   }

   private void calculatePath() {
      Arc2D.Double head = new Arc2D.Double( cx - (radius * 1), cy - (radius * 1), radius * 2,
            radius * 2, 0, 181, Arc2D.PIE );
      GeneralPath body = new GeneralPath();
      body.moveTo( cx - radius, cy );
      body.lineTo( cx, cy + height );
      body.lineTo( cx + radius, cy );
      body.closePath();
      pin = new Area( head );
      ((Area) pin).add( new Area( body ) );
      if( angle > 0 ){
         pin = AffineTransform.getRotateInstance( Math.toRadians( 360 - angle ), cx, cy )
               .createTransformedShape( pin );
      }
   }
}