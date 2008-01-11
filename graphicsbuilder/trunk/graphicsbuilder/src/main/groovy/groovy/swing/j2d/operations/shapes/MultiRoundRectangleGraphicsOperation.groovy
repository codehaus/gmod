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

package groovy.swing.j2d.operations.shapes

import java.awt.Shape
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.geom.MultiRoundRectangle

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class MultiRoundRectangleGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = super.required + ['x','y','width','height']
    public static optional = super.optional + ['topLeftWidth','topLeftHeight',
                                               'topRightWidth','topRightHeight',
                                               'bottomLeftWidth','bottomLeftHeight',
                                               'bottomRightWidth','bottomRightHeight']

    private MultiRoundRectangle roundRect

    def x = 0
    def y = 0
    def width = 10
    def height = 10
    def topLeftWidth
    def topLeftHeight
    def topRightWidth
    def topRightHeight
    def bottomLeftWidth
    def bottomLeftHeight
    def bottomRightWidth
    def bottomRightHeight

    public MultiRoundRectangleGraphicsOperation() {
        super( "roundRect" )
    }

    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       roundRect = null
    }

    public Shape getShape( GraphicsContext context ) {
       if( roundRect == null ){
          calculateRoundRect()
       }
       roundRect
    }

    private void calculateRoundRect() {
       def tlw = topLeftWidth != null ? topLeftWidth :
                    topLeftHeight != null ? topLeftHeight : 0
       def tlh = topLeftHeight != null ? topLeftHeight :
                    topLeftWidth != null ? topLeftWidth : 0

       def trw = topRightWidth != null ? topRightWidth :
                    topRightHeight != null ? topRightHeight : 0
       def trh = topRightHeight != null ? topRightHeight :
                    topRightWidth != null ? topRightWidth : 0

       def blw = bottomLeftWidth != null ? bottomLeftWidth :
                    bottomLeftHeight != null ? bottomLeftHeight : 0
       def blh = bottomLeftHeight != null ? bottomLeftHeight :
                    bottomLeftWidth != null ? bottomLeftWidth : 0

       def brw = bottomRightWidth != null ? bottomRightWidth :
                    bottomRightHeight != null ? bottomRightHeight : 0
       def brh = bottomRightHeight != null ? bottomRightHeight :
                    bottomRightWidth != null ? bottomRightWidth : 0

       roundRect = new MultiRoundRectangle( x as double,
                                            y as double,
                                            width as double,
                                            height as double,
                                            tlw as double,
                                            tlh as double,
                                            trw as double,
                                            trh as double,
                                            blw as double,
                                            blh as double,
                                            brw as double,
                                            brh as double )
    }
}