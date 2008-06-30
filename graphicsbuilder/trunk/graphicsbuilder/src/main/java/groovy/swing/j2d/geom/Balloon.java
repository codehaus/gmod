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
public class Balloon implements Shape, Cloneable {
   public static final int TAB_AT_BOTTOM = 0;
   public static final int TAB_AT_LEFT = 3;
   public static final int TAB_AT_RIGHT = 1;
   public static final int TAB_AT_TOP = 2;

   private int anglePosition = -1;
   private float arc;
   private Shape balloon;
   private float height;
   private float tabDisplacement;
   private float tabHeight;
   private int tabLocation;
   private float tabWidth;
   private float width;
   private float x;
   private float y;

   public Balloon() {
      this( 0, 0, 20, 20, 5, 5, 2.5f, TAB_AT_BOTTOM, 0.5f );
   }

   public Balloon( float x, float y, float width, float height, float arc, float tabWidth,
         float tabHeight, int tabLocation, float tabDisplacement ) {
      super();
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.arc = arc;
      this.tabWidth = tabWidth;
      this.tabHeight = tabHeight;
      this.tabLocation = tabLocation;
      this.tabDisplacement = tabDisplacement;
      calculatePath();
   }

   public Balloon( float x, float y, float width, float height, float arc, float tabWidth,
         float tabHeight, int tabLocation, float tabDisplacement, int anglePosition ) {
      super();
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.arc = arc;
      this.tabWidth = tabWidth;
      this.tabHeight = tabHeight;
      this.tabLocation = tabLocation;
      this.tabDisplacement = tabDisplacement;
      this.anglePosition = anglePosition;
      calculatePath();
   }

   public Object clone() {
      return new Balloon( x, y, width, height, arc, tabWidth, tabHeight, tabLocation,
            tabDisplacement, anglePosition );
   }

   public boolean contains( double x, double y ) {
      return balloon.contains( x, y );
   }

   public boolean contains( double x, double y, double w, double h ) {
      return balloon.contains( x, y, w, h );
   }

   public boolean contains( Point2D p ) {
      return balloon.contains( p );
   }

   public boolean contains( Rectangle2D r ) {
      return balloon.contains( r );
   }

   public int getAnglePosition() {
      return anglePosition;
   }

   public float getArc() {
      return arc;
   }

   public Rectangle getBounds() {
      return balloon.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return balloon.getBounds2D();
   }

   public float getHeight() {
      return height;
   }

   public PathIterator getPathIterator( AffineTransform at ) {
      return balloon.getPathIterator( at );
   }

   public PathIterator getPathIterator( AffineTransform at, double flatness ) {
      return balloon.getPathIterator( at, flatness );
   }

   public float getTabDisplacement() {
      return tabDisplacement;
   }

   public float getTabHeight() {
      return tabHeight;
   }

   public int getTabLocation() {
      return tabLocation;
   }

   public float getTabWidth() {
      return tabWidth;
   }

   public float getWidth() {
      return width;
   }

   public float getX() {
      return x;
   }

   public float getY() {
      return y;
   }

   public boolean intersects( double x, double y, double w, double h ) {
      return balloon.intersects( x, y, w, h );
   }

   public boolean intersects( Rectangle2D r ) {
      return balloon.intersects( r );
   }

   public void setAnglePosition( int anglePosition ) {
      int oldValue = this.anglePosition;
      this.anglePosition = anglePosition;
      if( oldValue != anglePosition ){
         calculatePath();
      }
   }

   public void setArc( float arc ) {
      float oldValue = this.arc;
      this.arc = arc;
      if( oldValue != arc ){
         calculatePath();
      }
   }

   public void setHeight( float height ) {
      float oldValue = this.height;
      this.height = height;
      if( oldValue != height ){
         calculatePath();
      }
   }

   public void setTabDisplacement( float tabDisplacement ) {
      float oldValue = this.tabDisplacement;
      this.tabDisplacement = tabDisplacement;
      if( oldValue != tabDisplacement ){
         calculatePath();
      }
   }

   public void setTabHeight( float tabHeight ) {
      float oldValue = this.tabHeight;
      this.tabHeight = tabHeight;
      if( oldValue != tabHeight ){
         calculatePath();
      }
   }

   public void setTabLocation( int tabLocation ) {
      int oldValue = this.tabLocation;
      this.tabLocation = tabLocation;
      if( oldValue != tabLocation ){
         calculatePath();
      }
   }

   public void setTabWidth( float tabWidth ) {
      float oldValue = this.tabWidth;
      this.tabWidth = tabWidth;
      if( oldValue != tabWidth ){
         calculatePath();
      }
   }

   public void setWidth( float width ) {
      float oldValue = this.width;
      this.width = width;
      if( oldValue != width ){
         calculatePath();
      }
   }

   public void setX( float x ) {
      float oldValue = this.x;
      this.x = x;
      if( oldValue != x ){
         calculatePath();
      }
   }

   public void setY( float y ) {
      float oldValue = this.y;
      this.y = y;
      if( oldValue != y ){
         calculatePath();
      }
   }

   private void calculatePath() {
      Shape rectangle = new RoundRectangle2D.Float( x, y, width, height, arc, arc );
      Triangle triangle = null;
      AffineTransform at = new AffineTransform();

      switch( tabLocation ){
         case TAB_AT_RIGHT:
            if( anglePosition == -1 ){
               triangle = new Triangle( 0f, 0f, tabWidth, 270f, tabHeight );
            }else{
               triangle = new Triangle( 0f, 0f, tabWidth, 270f, calculateAnglePosition(), tabHeight );
            }
            float a = (tabWidth + arc - height) * tabDisplacement;
            at = AffineTransform.getTranslateInstance( x + width, y + height - tabWidth - (arc / 2)
                  + a );
            break;
         case TAB_AT_LEFT:
            if( anglePosition == -1 ){
               triangle = new Triangle( 0, 0, tabWidth, 90, tabHeight );
            }else{
               triangle = new Triangle( 0, 0, tabWidth, 90, calculateAnglePosition(), tabHeight );
            }
            float b = (height - arc - tabWidth) * tabDisplacement;
            at = AffineTransform.getTranslateInstance( x, y + tabWidth + (arc / 2) + b );
            break;
         case TAB_AT_TOP:
            if( anglePosition == -1 ){
               triangle = new Triangle( 0, 0, tabWidth, 0, tabHeight );
            }else{
               triangle = new Triangle( 0, 0, tabWidth, 0, calculateAnglePosition(), tabHeight );
            }
            float c = (tabWidth + arc - width) * tabDisplacement;
            at = AffineTransform.getTranslateInstance( x + width - tabWidth - (arc / 2) + c, y );
            break;
         default:
            if( anglePosition == -1 ){
               triangle = new Triangle( 0, 0, tabWidth, 180, tabHeight );
            }else{
               triangle = new Triangle( 0, 0, tabWidth, 180, calculateAnglePosition(), tabHeight );
            }
            float d = (width - arc - tabWidth) * tabDisplacement;
            at = AffineTransform.getTranslateInstance( x + tabWidth + (arc / 2) + d, y + height );
      }

      balloon = new Area( rectangle );
      ((Area) balloon).add( new Area( at.createTransformedShape( triangle ) ) );
   }

   private int calculateAnglePosition() {
      return (int) Math.abs( anglePosition - Triangle.ANGLE_AT_END );
   }
}