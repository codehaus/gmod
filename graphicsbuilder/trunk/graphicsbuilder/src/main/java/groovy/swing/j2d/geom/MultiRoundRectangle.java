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
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class MultiRoundRectangle implements Shape, Cloneable {
   private float bottomLeftHeight;
   private float bottomLeftWidth;
   private float bottomRightHeight;
   private float bottomRightWidth;
   private float height;
   private GeneralPath rectangle;
   private float topLeftHeight;
   private float topLeftWidth;
   private float topRightHeight;
   private float topRightWidth;
   private float width;
   private float x;
   private float y;

   public MultiRoundRectangle( float x, float y, float width, float height, float topLeft,
         float topRight, float bottomLeft, float bottomRight ) {
      this( x, y, width, height, topLeft, topLeft, topRight, topRight, bottomLeft, bottomLeft,
            bottomRight, bottomRight );
   }

   public MultiRoundRectangle( float x, float y, float width, float height,
         float topLeftWidth, float topLeftHeight, float topRightWidth, float topRightHeight,
         float bottomLeftWidth, float bottomLeftHeight, float bottomRightWidth,
         float bottomRightHeight ) {
      if( topLeftWidth + topRightWidth > width ){
         throw new IllegalArgumentException( "top rounding factors are invalid (" + topLeftWidth
               + ") (" + topRightWidth + ")" );
      }
      if( bottomLeftWidth + bottomRightWidth > width ){
         throw new IllegalArgumentException( "bottom rounding factors are invalid ("
               + bottomLeftWidth + ") (" + bottomRightWidth + ")" );
      }
      if( topLeftHeight + bottomLeftHeight > height ){
         throw new IllegalArgumentException( "left rounding factors are invalid (" + topLeftWidth
               + ") (" + bottomLeftWidth + ")" );
      }
      if( topRightHeight + bottomRightHeight > height ){
         throw new IllegalArgumentException( "ritgh rounding factors are invalid (" + topRightWidth
               + ") (" + bottomRightWidth + ")" );
      }

      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.topLeftWidth = topLeftWidth;
      this.topLeftHeight = topLeftHeight;
      this.topRightWidth = topRightWidth;
      this.topRightHeight = topRightHeight;
      this.bottomLeftWidth = bottomLeftWidth;
      this.bottomLeftHeight = bottomLeftHeight;
      this.bottomRightWidth = bottomRightWidth;
      this.bottomRightHeight = bottomRightHeight;
      calculateRectangle();
   }

   public Object clone() {
      return new MultiRoundRectangle( x, y, width, height, topLeftWidth, topLeftHeight,
            topRightWidth, topRightHeight, bottomLeftWidth, bottomLeftHeight, bottomRightWidth,
            bottomRightHeight );
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

   public float getBottomLeft() {
      return bottomLeftWidth;
   }

   public float getBottomRight() {
      return bottomRightWidth;
   }

   public Rectangle getBounds() {
      return rectangle.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return rectangle.getBounds2D();
   }

   public float getHeight() {
      return height;
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return rectangle.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return rectangle.getPathIterator( at, flatness );
   }

   public float getTopLeft() {
      return topLeftWidth;
   }

   public float getTopRight() {
      return topRightWidth;
   }

   public float getWidth() {
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
      if( topLeftWidth > 0 ){
         rectangle.moveTo( x + topLeftWidth, y );
         rectangle.append( new Arc2D.Float( x, y, topLeftWidth * 2, topLeftHeight * 2, 90, 90,
               Arc2D.OPEN ), true );
      }else{
         rectangle.moveTo( x, y );
         rectangle.lineTo( x, y + height - bottomLeftHeight );
      }

      if( bottomLeftWidth > 0 ){
         rectangle.append( new Arc2D.Float( x, y + height - (bottomLeftHeight * 2),
               bottomLeftWidth * 2, bottomLeftHeight * 2, 180, 90, Arc2D.OPEN ), true );
      }else{
         rectangle.lineTo( x, y + height );
      }

      if( bottomRightWidth > 0 ){
         rectangle.append( new Arc2D.Float( x + width - (bottomRightWidth * 2), y + height
               - (bottomRightHeight * 2), bottomRightWidth * 2, bottomRightHeight * 2, 270, 90,
               Arc2D.OPEN ), true );
      }else{
         rectangle.lineTo( x + width, y + height );
      }

      if( topRightWidth > 0 ){
         rectangle.append( new Arc2D.Float( x + width - (topRightWidth * 2), y, topRightWidth * 2,
               topRightHeight * 2, 0, 90, Arc2D.OPEN ), true );
      }else{
         rectangle.lineTo( x + width, y );
      }

      rectangle.closePath();
   }
}