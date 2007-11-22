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

import java.awt.Rectangle
import java.awt.Shape

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class Rect3DGraphicsOperation extends AbstractShapeGraphicsOperation {
    def x = 0
    def y = 0
    def width = 10
    def height = 10
    def raised = true

    Rect3DGraphicsOperation() {
        super( "rect", ["x", "y", "width", "height", "raised"] as String[] )
    }

    protected Shape computeShape( GraphicsContext context ) {
        int x = getParameterValue( "x" )
        int y = getParameterValue( "y" )
        int width = getParameterValue( "width" )
        int height = getParameterValue( "height" )
        return new Rectangle( x, y, width, height )
    }
}