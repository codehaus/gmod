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

import java.awt.Shape
import java.awt.geom.Line2D
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.AbstractOutlineGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class LineGraphicsOperation extends AbstractOutlineGraphicsOperation {
    public static required = ['x1','y1','x2','y2']

    def x1 = 0
    def y1 = 0
    def x2 = 0
    def y2 = 10

    public LineGraphicsOperation() {
        super( "line" )
    }

    public Shape getShape( GraphicsContext context ) {
        return new Line2D.Double( x1 as double,
                                  y1 as double,
                                  x2 as double,
                                  y2 as double )
    }
}