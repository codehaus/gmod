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
import groovy.swing.j2d.geom.Triangle

import static java.lang.Math.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class TriangleGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = AbstractShapeGraphicsOperation.required + ['x','y','width']
    public static optional = AbstractShapeGraphicsOperation.optional + ['height','rightAngleAt','angle','rotateAtCenter']

    private Triangle triangle

    def x = 0
    def y = 0
    def width = 10
    def height
    def rightAngleAt
    def angle
    def rotateAtCenter

    public TriangleGraphicsOperation() {
        super( "triangle" )
    }

    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       triangle = null
    }

    public Shape getShape( GraphicsContext context ) {
       if( triangle == null ){
          calculateTriangle()
       }
       triangle
    }

    private void calculateTriangle() {
       if( rightAngleAt ){
          calculateRightTriangle()
       }else if( height ){
          calculateIsoscelesTriangle()
       }else{
          calculateEquilateralTriangle()
       }
    }

    private void calculateEquilateralTriangle(){
       def a = angle != null ? angle : 0
       triangle = new Triangle( x as double,
                                y as double,
                                width as double,
                                a as double,
                                rotateAtCenter ? true : false )
    }

    private void calculateIsoscelesTriangle(){
       def h = height != null ? height : Double.NaN
       def a = angle != null ? angle : 0
       triangle = new Triangle( x as double,
                                y as double,
                                width as double,
                                a as double,
                                h as double,
                                rotateAtCenter ? true : false )
    }

    private void calculateRightTriangle(){
       def ap = null
       switch( rightAngleAt ){
          case Triangle.ANGLE_AT_START:
          case Triangle.ANGLE_AT_END:
             ap = rightAngleAt
             break
          case 'start':
             ap = Triangle.ANGLE_AT_START
             break
          case 'end':
             ap = Triangle.ANGLE_AT_END
             break
          default:
             throw new IllegalArgumentException("rightAngleAt must be one of ['start','end',"+
                          "Triangle.ANGLE_AT_START,Triangle.ANGLE_AT_END]")
       }

       def h = height != null ? height : Double.NaN
       def a = angle != null ? angle : 0
       triangle = new Triangle( x as double,
                                y as double,
                                width as double,
                                a as double,
                                ap,
                                h as double,
                                rotateAtCenter ? true : false )
    }
}