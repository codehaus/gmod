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
import groovy.swing.j2d.geom.Cross

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class CrossGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = super.required + ['cx','cy','radius']
    public static optional = super.optional + ['width','angle']

    private Cross cross

    def cx = 5
    def cy = 5
    def radius = 5
    def width
    def angle

    public CrossGraphicsOperation() {
        super( "cross" )
    }

    public void propertyChange( PropertyChangeEvent event ){
       cross = null
       super.propertyChange( event )
    }

    public Shape getShape( GraphicsContext context ) {
       if( cross == null ){
          calculateCross()
       }
       cross
    }

    private void calculateCross() {
       def w = width != null ? width : radius * 3 / 5
       def a = angle != null ? angle : 0
       cross = new Cross( cx as double,
                           cy as double,
                           radius as double,
                           w as double,
                           a as double )
    }
}