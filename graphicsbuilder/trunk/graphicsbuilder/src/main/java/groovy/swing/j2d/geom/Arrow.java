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
   private Area area;
   private double depth;
   private double height;
   private double rise;
   private double width;
   private double x;
   private double y;

   public Arrow( double x, double y, double width, double height ) {
      this( x, y, width, height, height / 4, width / 2 );
   }

   public Arrow( double x, double y, double width, double height, double rise, double depth ) {
      if( depth >= width ){
         throw new IllegalArgumentException( "depth can not be equal or greater than width" );
      }
      if( rise > height / 2 ){
         throw new IllegalArgumentException( "rise can not be greater than height/2 ("
               + (height / 2) + ")" );
      }
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.rise = rise;
      this.depth = depth;
      calculateArrow();
   }

   public Object clone() {
      return new Arrow( x, y, width, height, rise, depth );
   }

   public boolean contains( double x, double y ) {
      return area.contains( x, y );
   }

   public boolean contains( double x, double y, double w, double h ) {
      return area.contains( x, y, w, h );
   }

   public boolean contains( Point2D p ) {
      return area.contains( p );
   }

   public boolean contains( Rectangle2D r ) {
      return area.contains( r );
   }

   public Rectangle getBounds() {
      return area.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return area.getBounds2D();
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return area.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return area.getPathIterator( at, flatness );
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return area.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return area.intersects( r );
   }

   private void calculateArrow() {
      GeneralPath head = new GeneralPath();
      head.moveTo( x + depth, y );
      head.lineTo( x + depth, y + height );
      head.lineTo( x + width, y + (height / 2) );
      head.closePath();
      area = new Area( new Rectangle2D.Double( x, y + (height / 2) - rise, depth, rise * 2 ) );
      area.add( new Area( head ) );
   }
}