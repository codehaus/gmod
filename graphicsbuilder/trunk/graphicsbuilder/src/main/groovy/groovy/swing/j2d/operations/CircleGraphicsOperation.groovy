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
import java.awt.geom.Ellipse2D
import java.awt.Component

/**
 * Draws an Ellipse2D as a circle<br>
 * Parameters<ul>
 * <li>cx: (number)</li>
 * <li>cy: (number)</li>
 * <li>radius: (number)</li>
 * </ul>
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class CircleGraphicsOperation extends AbstractShapeGraphicsOperation {
    def cx
    def cy
    def radius

    public CircleGraphicsOperation() {
        super( "circle", ["cx", "cy", "radius"] as String[] )
    }

    protected Shape computeShape( Graphics2D g, Component target ) {
        int radius = getParameterValue( "radius" )
        int cx = getParameterValue( "cx" )
        int cy = getParameterValue( "cy" )
        return new Ellipse2D.Double( cx - radius, cy - radius, radius * 2, radius * 2 )
    }
}