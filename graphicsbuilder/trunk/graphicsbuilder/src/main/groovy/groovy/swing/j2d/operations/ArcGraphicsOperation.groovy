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

package groovy.swing.j2d.operations

import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation

import java.awt.Graphics2D
import java.awt.Shape
import java.awt.image.ImageObserver
import java.awt.geom.Arc2D

/**
 * Draws an Arc2D<br>
 * Parameters<ul>
 * <li>x: (number)</li>
 * <li>y: (number)</li>
 * <li>width: (number)</li>
 * <li>height: (number)</li>
 * <li>start: (number)</li>
 * <li>extent: (number)</li>
 * </ul>Optional<ul>
 * <li>close: 'open', 'chord', 'pie' (default: 'open')</li>
 * </ul>
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ArcGraphicsOperation extends AbstractShapeGraphicsOperation {
    def x
    def y
    def width
    def height
    def start
    def extent
    def close

    public ArcGraphicsOperation() {
        super( "arc", ["x", "y", "width", "height", "start", "extent"] as String[],
               ["close"] as String[] )
    }

    protected Shape computeShape( Graphics2D g, ImageObserver observer ){
        double x = getParameterValue( "x" )
        double y = getParameterValue( "y" )
        double width = getParameterValue( "width" )
        double height = getParameterValue( "height" )
        double start = getParameterValue( "start" )
        double extent = getParameterValue( "extent" )
        int close = getCloseParameter()
        return new Arc2D.Double( x, y, width, height, start, extent, close )
    }

    private int getCloseParameter() {
        if( !parameterHasValue( "close" ) ){
            return Arc2D.OPEN
        }
        def closeValue = getParameterValue( "close" )
        if( closeValue instanceof Number ){
            return closeValue as int
        }else if( closeValue instanceof String ){
           if( closeValue.compareToIgnoreCase("OPEN") == 0){
               return Arc2D.OPEN
           }else if( closeValue.compareToIgnoreCase("CHORD") == 0 ){
               return Arc2D.CHORD
           }else if( closeValue.compareToIgnoreCase("PIE") == 0){
               return Arc2D.PIE
           }
        }
        return Arc2D.OPEN
    }
}