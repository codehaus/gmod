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

package groovy.swing.j2d.operations.shapes

import java.awt.Shape
import java.awt.geom.GeneralPath
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext

import static java.lang.Math.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class TriangleGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = ['x1','y1','x2','y2']
    public static optional = super.optional + ['height','rightAngleOn']

    private GeneralPath triangle
    private def x3
    private def y3

    def x1 = 10
    def y1 = 10
    def x2 = 0
    def y2 = 10
    def height
    def rightAngleOn

    public TriangleGraphicsOperation() {
        super( "triangle" )
    }

    public void propertyChange( PropertyChangeEvent event ){
       triangle = null
    }

    public Shape getShape( GraphicsContext context ) {
       if( triangle == null ){
          calculateTriangle()
       }
       triangle
    }

    private void calculateTriangle() {
       if( rightAngleOn && (!'right'.equals(rightAngleOn) && !'left'.equals(rightAngleOn))){
          throw new IllegalArgumentException("triangle.rightAngleOn must be either 'right' or 'left'.")
       }

       if( rightAngleOn ){
          calculateRightTriangle()
       }else if( height ){
          calculateIsocelesTriangle()
       }else{
          calculateEquilateralTriangle()
       }
       triangle = new GeneralPath()
       triangle.moveTo(x1 as double, y1 as double)
       triangle.lineTo(x2 as double, y2 as double)
       triangle.lineTo(x3 as double, y3 as double)
       triangle.closePath()
    }

    private void calculateEquilateralTriangle(){
       def dx = x2 - x1
       def dy = y2 - y1

       if( dy == 0 ){
          def h = abs(sqrt(3)/2*dx)
          if( dx > 0 ){
             x3 = x1 + (dx/2)
             y3 = y1 - h
          }else{
             x3 = x1 + (dx/2)
             y3 = y1 + h
          }
          return
       }

       if( dx == 0 ){
          def h = abs(sqrt(3)/2*dy)
          if( dy > 0 ){
             x3 = x1 + h
             y3 = y1 + (dy/2)
          }else{
             x3 = x1 - h
             y3 = y1 + (dy/2)
          }
          return
       }

       def base = sqrt(pow(dx,2)+pow(dy,2))
       def h = sqrt(3)/2*base
       def m = dy/dx
       def t = toRadians(90 - toDegrees(atan(m)))
       if( dx > 0 && dy > 0 ){
          x3 = x1 + (dx/2) + abs(h*cos(t))
          y3 = y1 + (dy/2) - abs(h*sin(t))
       }else if( dx > 0 && dy < 0 ){
          x3 = x1 + (dx/2) - abs(h*cos(t))
          y3 = y1 + (dy/2) - abs(h*sin(t))
       }else if( dx < 0 && dy > 0 ){
          x3 = x1 + (dx/2) + abs(h*cos(t))
          y3 = y1 + (dy/2) + abs(h*sin(t))
       }else{
          x3 = x1 + (dx/2) - abs(h*cos(t))
          y3 = y1 + (dy/2) + abs(h*sin(t))
       }
    }

    private void calculateIsocelesTriangle(){
       def dx = x2 - x1
       def dy = y2 - y1

       if( dy == 0 ){
          if( dx > 0 ){
             x3 = x1 + (dx/2)
             y3 = y1 - abs(height)
          }else{
             x3 = x1 + (dx/2)
             y3 = y1 + abs(height)
          }
          return
       }

       if( dx == 0 ){
          if( dy > 0 ){
             x3 = x1 + abs(height)
             y3 = y1 + (dy/2)
          }else{
             x3 = x1 - abs(height)
             y3 = y1 + (dy/2)
          }
          return
       }

       def base = sqrt(pow(dx,2)+pow(dy,2))
       def m = dy/dx
       def t = toRadians(90 - toDegrees(atan(m)))
       if( dx >= 0 && dy >= 0 ){
          x3 = x1 + (dx/2) + abs(height*cos(t))
          y3 = y1 + (dy/2) - abs(height*sin(t))
       }else if( dx >= 0 && dy < 0 ){
          x3 = x1 + (dx/2) - abs(height*cos(t))
          y3 = y1 + (dy/2) - abs(height*sin(t))
       }else if( dx < 0 && dy >= 0 ){
          x3 = x1 + (dx/2) + abs(height*cos(t))
          y3 = y1 + (dy/2) + abs(height*sin(t))
       }else{
          x3 = x1 + (dx/2) - abs(height*cos(t))
          y3 = y1 + (dy/2) + abs(height*sin(t))
       }
    }

    private void calculateRightTriangle(){
       def dx = x2 - x1
       def dy = y2 - y1

       if( dy == 0 ){
          def h = height ? abs(height) : abs(sqrt(3)/2*dx)
          if( dx > 0 ){
             if( 'left'.equals(rightAngleOn) ){
                x3 = x1
                y3 = y1 - h
             }else{
                x3 = x2
                y3 = y1 - h
             }
          }else{
             if( 'left'.equals(rightAngleOn) ){
                x3 = x2
                y3 = y1 + h
             }else{
                x3 = x1
                y3 = y1 + h
             }
          }
          return
       }

       if( dx == 0 ){
          def h = height ? abs(height) : abs(sqrt(3)/2*dy)
          if( dy > 0 ){
             if( 'left'.equals(rightAngleOn) ){
                x3 = x1 + h
                y3 = y1
             }else{
                x3 = x1 + h
                y3 = y2
             }
          }else{
             if( 'left'.equals(rightAngleOn) ){
                x3 = x1 - h
                y3 = y1
             }else{
                x3 = x1 - h
                y3 = y2
             }
          }
          return
       }

       def base = sqrt(pow(dx,2)+pow(dy,2))
       def h = height ? height : sqrt(3)/2*base
       def m = dy/dx
       def t = toRadians(90 - toDegrees(atan(m)))
       if( dx >= 0 && dy >= 0 ){
          if( 'left'.equals(rightAngleOn) ){
             x3 = x1 + abs(h*cos(t))
             y3 = y1 - abs(h*sin(t))
          }else{
             x3 = x2 + abs(h*cos(t))
             y3 = y2 - abs(h*sin(t))
          }
       }else if( dx >= 0 && dy < 0 ){
          if( 'left'.equals(rightAngleOn) ){
             x3 = x1 - abs(h*cos(t))
             y3 = y1 - abs(h*sin(t))
          }else{
             x3 = x2 - abs(h*cos(t))
             y3 = y2 - abs(h*sin(t))
          }
       }else if( dx < 0 && dy >= 0 ){
          if( 'left'.equals(rightAngleOn) ){
             x3 = x1 + abs(h*cos(t))
             y3 = y1 + abs(h*sin(t))
          }else{
             x3 = x2 + abs(h*cos(t))
             y3 = y2 + abs(h*sin(t))
          }
       }else{
          if( 'left'.equals(rightAngleOn) ){
             x3 = x1 - abs(h*cos(t))
             y3 = y1 + abs(h*sin(t))
          }else{
             x3 = x2 - abs(h*cos(t))
             y3 = y2 + abs(h*sin(t))
          }
       }
    }
}