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
public class Triangle implements Shape, Cloneable, Centered {
   public static final int ANGLE_AT_END = 1;
   public static final int ANGLE_AT_START = 0;

   public static final int EQUILATERAL = 0;
   public static final int ISOSCELES = 1;
   public static final int RIGHT = 2;

   private double angle;
   private int anglePosition;
   private double cx;
   private double cy;
   private double height = Double.NaN;
   private boolean rotateAtCenter;
   private Shape triangle;
   private int type;
   private double width;
   private double x;
   private double y;

   public Triangle( double x, double y, double width, double angle ) {
      this( x, y, width, angle, false );
   }

   public Triangle( double x, double y, double width, double angle, boolean rotateAtCenter ) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.angle = angle;
      this.rotateAtCenter = rotateAtCenter;
      this.type = EQUILATERAL;
      calculateEquilateralTriangle();
   }

   public Triangle( double x, double y, double width, double angle, double height ) {
      this( x, y, width, angle, height, false );
   }

   public Triangle( double x, double y, double width, double angle, double height,
         boolean rotateAtCenter ) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.angle = angle;
      this.height = height;
      this.rotateAtCenter = rotateAtCenter;
      this.type = ISOSCELES;
      calculateIsoscelesTriangle();
   }

   public Triangle( double x, double y, double width, double angle, int anglePosition ) {
      this( x, y, width, angle, anglePosition, false );
   }

   public Triangle( double x, double y, double width, double angle, int anglePosition,
         boolean rotateAtCenter ) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.angle = angle;
      this.anglePosition = anglePosition;
      this.rotateAtCenter = rotateAtCenter;
      this.type = RIGHT;
      calculateRightTriangle();
   }

   public Triangle( double x, double y, double width, double angle, int anglePosition, double height ) {
      this( x, y, width, angle, anglePosition, height, false );
   }

   public Triangle( double x, double y, double width, double angle, int anglePosition,
         double height, boolean rotateAtCenter ) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.angle = angle;
      this.anglePosition = anglePosition;
      this.height = height;
      this.rotateAtCenter = rotateAtCenter;
      this.type = RIGHT;
      calculateRightTriangle();
   }

   public Object clone() {
      switch( type ){
         case ISOSCELES:
            return new Triangle( x, y, width, angle, height, rotateAtCenter );
         case RIGHT:
            return new Triangle( x, y, width, angle, anglePosition, height, rotateAtCenter );
         default:
            return new Triangle( x, y, width, angle, rotateAtCenter );
      }
   }

   public boolean contains( double x, double y ) {
      return triangle.contains( x, y );
   }

   public boolean contains( double x, double y, double w, double h ) {
      return triangle.contains( x, y, w, h );
   }

   public boolean contains( Point2D p ) {
      return triangle.contains( p );
   }

   public boolean contains( Rectangle2D r ) {
      return triangle.contains( r );
   }

   public double getAngle() {
      return angle;
   }

   public int getAnglePosition() {
      return anglePosition;
   }

   public Rectangle getBounds() {
      return triangle.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return triangle.getBounds2D();
   }

   public double getCx() {
      return cx;
   }

   public double getCy() {
      return cy;
   }

   public double getHeight() {
      return height;
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return triangle.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return triangle.getPathIterator( at, flatness );
   }

   public int getType() {
      return type;
   }

   public double getWidth() {
      return width;
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return triangle.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return triangle.intersects( r );
   }

   public boolean isRotateAtCenter() {
      return rotateAtCenter;
   }

   private void calculateEquilateralTriangle() {
      this.height = Math.abs( Math.sqrt( 3 ) / 2 * width );
      GeneralPath t = new GeneralPath();
      t.moveTo( x, y );
      t.lineTo( x + width, y );
      t.lineTo( x + (width / 2), y - height );
      t.closePath();
      triangle = t;

      rotate( new double[] { width, width, width }, new double[][] { { x, y }, { x + width, y },
            { x + (width / 2), y - height } } );
   }

   private void calculateIsoscelesTriangle() {
      this.height = !Double.isNaN( height ) && !Double.isInfinite( height ) ? Math.abs( height )
            : Math.abs( Math.sqrt( 3 ) / 2 * width );
      GeneralPath t = new GeneralPath();
      t.moveTo( x, y );
      t.lineTo( x + width, y );
      t.lineTo( x + (width / 2), y - height );
      t.closePath();
      triangle = t;

      double d = Math.sqrt( Math.pow( width / 2, 2 ) + Math.pow( height, 2 ) );
      rotate( new double[] { width, d, d }, new double[][] { { x, y }, { x + width, y },
            { x + (width / 2), y - height } } );
   }

   private void calculateRightTriangle() {
      this.anglePosition = anglePosition > ANGLE_AT_END ? ANGLE_AT_START : anglePosition;
      this.height = !Double.isNaN( height ) && !Double.isInfinite( height ) ? Math.abs( height )
            : Math.abs( Math.sqrt( 3 ) / 2 * width );
      GeneralPath t = new GeneralPath();
      t.moveTo( x, y );
      if( anglePosition == ANGLE_AT_START ){
         t.lineTo( x, y - height );
         t.lineTo( x + width, y );
      }else{
         t.lineTo( x + width, y );
         t.lineTo( x + width, y - height );
      }
      t.closePath();
      triangle = t;

      double d = Math.sqrt( Math.pow( width, 2 ) + Math.pow( height, 2 ) );
      if( anglePosition == ANGLE_AT_END ){
         rotate( new double[] { width, height, d }, new double[][] { { x, y }, { x + width, y },
               { x + width, y - height } } );
      }else{
         rotate( new double[] { width, height, d }, new double[][] { { x + width, y }, { x, y },
               { x, y - height } } );
      }
   }

   private void rotate( double[] sides, double[][] points ) {
      double perimeter = sides[0] + sides[1] + sides[2];
      cx = ((sides[0] * points[2][0]) + (sides[1] * points[0][0]) + (sides[2] * points[1][0]))
            / perimeter;
      cy = ((sides[0] * points[2][1]) + (sides[1] * points[0][1]) + (sides[2] * points[1][1]))
            / perimeter;
      if( angle > 0 ){
         if( rotateAtCenter ){
            triangle = AffineTransform.getRotateInstance( Math.toRadians( 360 - angle ), cx, cy )
                  .createTransformedShape( triangle );
         }else{
            triangle = AffineTransform.getRotateInstance( Math.toRadians( 360 - angle ), x, y )
                  .createTransformedShape( triangle );
         }
      }
   }
}