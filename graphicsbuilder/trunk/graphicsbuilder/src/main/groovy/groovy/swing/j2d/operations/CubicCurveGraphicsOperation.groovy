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
import java.awt.geom.CubicCurve2D

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class CubicCurveGraphicsOperation extends AbstractOutlineGraphicsOperation {
    def x1
    def x2
    def y1
    def y2
    def ctrlx1
    def ctrly1
    def ctrlx2
    def ctrly2

    CubicCurveGraphicsOperation() {
        super( "cubicCurve", ["x1", "x2", "y1", "y2", "ctrlx1", "ctrlx2", "ctrly1",
                "ctrly2" ] as String[] )
    }

    protected Shape computeShape( GraphicsContext context ) {
        double x1 = getParameterValue( "x1" )
        double x2 = getParameterValue( "x2" )
        double y1 = getParameterValue( "y1" )
        double y2 = getParameterValue( "y2" )
        double ctrlx1 = getParameterValue( "ctrlx1" )
        double ctrlx2 = getParameterValue( "ctrlx2" )
        double ctrly1 = getParameterValue( "ctrly1" )
        double ctrly2 = getParameterValue( "ctrly2" )
        return new CubicCurve2D.Double( x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2 )
    }
}