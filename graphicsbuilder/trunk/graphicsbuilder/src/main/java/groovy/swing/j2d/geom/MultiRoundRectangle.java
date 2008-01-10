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
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class MultiRoundRectangle implements Shape, Cloneable {
   private double bottomLeft;
   private double bottomRight;
   private double height;
   private GeneralPath rectangle;
   private double topLeft;
   private double topRight;
   private double width;
   private double x;
   private double y;

   public MultiRoundRectangle( double x, double y, double width, double height ) {
      this( x, y, width, height, 0, 0, 0, 0 );
   }

   public MultiRoundRectangle( double x, double y, double width, double height, double topLeft,
         double topRight, double bottomLeft, double bottomRight ) {
      if( topLeft + topRight > width ){
         throw new IllegalArgumentException( "top rounding factors are invalid (" + topLeft + ") ("
               + topRight + ")" );
      }
      if( bottomLeft + bottomRight > width ){
         throw new IllegalArgumentException( "bottom rounding factors are invalid (" + bottomLeft
               + ") (" + bottomRight + ")" );
      }
      if( topLeft + bottomLeft > height ){
         throw new IllegalArgumentException( "left rounding factors are invalid (" + topLeft
               + ") (" + bottomLeft + ")" );
      }
      if( topRight + bottomRight > height ){
         throw new IllegalArgumentException( "ritgh rounding factors are invalid (" + topRight
               + ") (" + bottomRight + ")" );
      }

      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.topLeft = topLeft;
      this.topRight = topRight;
      this.bottomLeft = bottomLeft;
      this.bottomRight = bottomRight;
      calculateRectangle();
   }

   public Object clone() {
      return new MultiRoundRectangle( x, y, width, height, topLeft, topRight, bottomLeft,
            bottomRight );
   }

   public boolean contains( double x, double y ) {
      return rectangle.contains( x, y );
   }

   public boolean contains( double x, double y, double w, double h ) {
      return rectangle.contains( x, y, w, h );
   }

   public boolean contains( Point2D p ) {
      return rectangle.contains( p );
   }

   public boolean contains( Rectangle2D r ) {
      return rectangle.contains( r );
   }

   public double getBottomLeft() {
      return bottomLeft;
   }

   public double getBottomRight() {
      return bottomRight;
   }

   public Rectangle getBounds() {
      return rectangle.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return rectangle.getBounds2D();
   }

   public double getHeight() {
      return height;
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return rectangle.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return rectangle.getPathIterator( at, flatness );
   }

   public double getTopLeft() {
      return topLeft;
   }

   public double getTopRight() {
      return topRight;
   }

   public double getWidth() {
      return width;
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return rectangle.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return rectangle.intersects( r );
   }

   private void calculateRectangle() {
      rectangle = new GeneralPath();
      if( topLeft > 0 ){
         rectangle.moveTo( x + topLeft, y );
         rectangle.append( new Arc2D.Double( x, y, topLeft * 2, topLeft * 2, 90, 90, Arc2D.OPEN ),
               true );
      }else{
         rectangle.moveTo( x, y );
         rectangle.lineTo( x, y + height - bottomLeft );
      }

      if( bottomLeft > 0 ){
         rectangle.append( new Arc2D.Double( x, y + height - (bottomLeft * 2), bottomLeft * 2,
               bottomLeft * 2, 180, 90, Arc2D.OPEN ), true );
      }else{
         rectangle.lineTo( x, y + height );
      }

      if( bottomRight > 0 ){
         rectangle.append( new Arc2D.Double( x + width - (bottomRight * 2), y + height
               - (bottomRight * 2), bottomRight * 2, bottomRight * 2, 270, 90, Arc2D.OPEN ), true );
      }else{
         rectangle.lineTo( x + width, y + height );
      }

      if( topRight > 0 ){
         rectangle.append( new Arc2D.Double( x + width - (topRight * 2), y, topRight * 2,
               topRight * 2, 0, 90, Arc2D.OPEN ), true );
      }else{
         rectangle.lineTo( x + width, y );
      }

      rectangle.closePath();
   }
}