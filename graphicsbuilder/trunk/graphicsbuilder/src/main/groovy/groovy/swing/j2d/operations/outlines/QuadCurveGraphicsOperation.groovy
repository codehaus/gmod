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

package groovy.swing.j2d.operations.outlines

import java.awt.Shape
import java.awt.geom.QuadCurve2D
import groovy.swing.j2d.GraphicsContext

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class QuadCurveGraphicsOperation extends AbstractOutlineGraphicsOperation {
    public static required = ['x1','x2','y1','y2','ctrlx','ctrly']

    def x1
    def x2
    def y1
    def y2
    def ctrlx
    def ctrly

    public QuadCurveGraphicsOperation() {
        super( "quadCurve" )
    }

    public Shape getShape( GraphicsContext context ) {
       return new QuadCurve2D.Double( x1 as double,
                                       y1 as double,
                                       ctrlx as double,
                                       ctrly as double,
                                       x2 as double,
                                       y2 as double )
    }
}