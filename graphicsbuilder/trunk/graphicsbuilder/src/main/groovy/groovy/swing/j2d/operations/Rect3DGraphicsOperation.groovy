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

import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Shape
import java.awt.image.ImageObserver

import groovy.swing.j2d.impl.AbstractGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class Rect3DGraphicsOperation extends AbstractGraphicsOperation {
    def x
    def y
    def width
    def height
    def raised

    static fillable = true
    static contextual = true

    Rect3DGraphicsOperation() {
        super( "rect", ["x", "y", "width", "height", "raised"] as String[] )
    }

    public Shape getClip( Graphics2D g, ImageObserver observer ) {
        int x = getParameterValue( "x" )
        int y = getParameterValue( "y" )
        int width = getParameterValue( "width" )
        int height = getParameterValue( "height" )
        return new Rectangle( x, y, width, height )
    }

    protected void doExecute( Graphics2D g, ImageObserver observer ){
        g.draw3DRect( x, y, width, height, raised )
    }
}