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
    public static optional = super.optional + ['topLeft','topRight','bottomLeft','bottomRight']

    private MultiRoundRectangle roundRect

    def x = 0
    def y = 0
    def width = 10
    def height = 10
    def topLeft
    def topRight
    def bottomLeft
    def bottomRight

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
       def tl = topLeft != null ? topLeft : 0
       def tr = topRight != null ? topRight : 0
       def bl = bottomLeft != null ? bottomLeft : 0
       def br = bottomRight != null ? bottomRight : 0
       roundRect = new MultiRoundRectangle( x as double,
                                            y as double,
                                            width as double,
                                            height as double,
                                            tl as double,
                                            tr as double,
                                            bl as double,
                                            br as double )
    }
}