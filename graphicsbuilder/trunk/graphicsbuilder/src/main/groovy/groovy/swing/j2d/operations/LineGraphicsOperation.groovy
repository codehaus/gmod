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

import groovy.swing.j2d.impl.AbstractOutlineGraphicsOperation

import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Line2D
import java.awt.Component

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class LineGraphicsOperation extends AbstractOutlineGraphicsOperation {
    def x1 = 0
    def x2 = 10
    def y1 = 0
    def y2 = 0

    LineGraphicsOperation() {
        super( "line", ["x1", "y1", "x2", "y2"] as String[] )
    }

    protected Shape computeShape( Graphics2D g, Component target ) {
        double x1 = getParameterValue( "x1" )
        double x2 = getParameterValue( "x2" )
        double y1 = getParameterValue( "y1" )
        double y2 = getParameterValue( "y2" )
        return new Line2D.Double( x1, y1, x2, y2 )
    }
}