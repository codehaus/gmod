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

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.AbstractOutlineGraphicsOperation

import java.awt.Shape
import java.awt.geom.QuadCurve2D

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class QuadCurveGraphicsOperation extends AbstractOutlineGraphicsOperation {
    def x1
    def x2
    def y1
    def y2
    def ctrlx
    def ctrly

    QuadCurveGraphicsOperation() {
        super( "quadCurve", ["x1", "x2", "y1", "y2", "ctrlx", "ctrly"] as String[] )
    }

    protected Shape computeShape( GraphicsContext context ) {
        double x1 = getParameterValue( "x1" )
        double x2 = getParameterValue( "x2" )
        double y1 = getParameterValue( "y1" )
        double y2 =  getParameterValue( "y2" )
        double ctrlx = getParameterValue( "ctrlx" )
        double ctrly = getParameterValue( "ctrly" )
        return new QuadCurve2D.Double( x1, y1, ctrlx, ctrly, x2, y2 )
    }
}