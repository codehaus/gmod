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

package groovy.swing.j2d.operations.shapes

import java.awt.Shape
import java.awt.geom.Arc2D
import groovy.swing.j2d.GraphicsContext

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ArcGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = ['x','y','width','height','start','extent']
    public static optional = AbstractShapeGraphicsOperation.optional + ['close']

    def x = 0
    def y = 0
    def width = 10
    def height = 10
    def start = 0
    def extent = 90
    def close

    public ArcGraphicsOperation() {
        super( "arc" )
    }

    public Shape getShape( GraphicsContext context ) {
        return new Arc2D.Double( x as double,
                                 y as double,
                                 width as double,
                                 height as double,
                                 start as double,
                                 extent as double,
                                 getCloseValue() )
    }

    public boolean hasXY() {
       true
    }
    
    private def getCloseValue() {
        if( !close ){
            return Arc2D.OPEN
        }
        if( close instanceof Number ){
          return close as int
        }else if( close instanceof String ){
           if( close.compareToIgnoreCase("OPEN") == 0 ){
               return Arc2D.OPEN
           }else if( close.compareToIgnoreCase("CHORD") == 0 ){
               return Arc2D.CHORD
           }else if( close.compareToIgnoreCase("PIE") == 0 ){
               return Arc2D.PIE
           }
        }
        return Arc2D.OPEN
    }
}