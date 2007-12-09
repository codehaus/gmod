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
import java.awt.geom.RoundRectangle2D
import java.awt.geom.Rectangle2D
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class RectGraphicsOperation extends AbstractShapeGraphicsOperation {
    protected static required = ['x','y','width','height']
    protected static optional = super.optional + ['arcWidth','arcHeight']

    def x = 0
    def y = 0
    def width = 10
    def height = 10
    def arcWidth
    def arcHeight

    public RectGraphicsOperation() {
        super( "rect" )
    }

    public Shape getShape( GraphicsContext context ) {
        if( arcWidth && arcHeight ){
           return new RoundRectangle2D.Double( x as double,
                                               y as double,
                                               width as double,
                                               height as double,
                                               arcWidth as double,
                                               arcHeight as double )
        }
        return new Rectangle2D.Double( x as double,
                                       y as double,
                                       width as double,
                                       height as double )
    }
}