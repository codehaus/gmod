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
import groovy.swing.j2d.geom.Arrow

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ArrowGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = super.required + ['x','y','width','height']
    public static optional = super.optional + ['depth','rise','angle']

    private Arrow arrow

    def x = 0
    def y = 0
    def width = 10
    def height = 6
    def depth
    def rise
    def angle

    public ArrowGraphicsOperation() {
        super( "arrow" )
    }

    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       arrow = null
    }

    public Shape getShape( GraphicsContext context ) {
       if( arrow == null ){
          calculateArrow()
       }
       arrow
    }

    private void calculateArrow() {
       def r = rise != null ? rise : 0.5
       def d = depth != null ? depth : 0.5
       def a = angle != null ? angle : 0
       arrow = new Arrow( x as double,
                          y as double,
                          width as double,
                          height as double,
                          r as double,
                          d as double,
                          a as double )
    }
}