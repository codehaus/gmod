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
import groovy.swing.j2d.geom.RoundPin

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class RoundPinGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = AbstractShapeGraphicsOperation.required + ['cx','cy','radius']
    public static optional = AbstractShapeGraphicsOperation.optional + ['height','angle']

    private RoundPin pin

    def cx = 5
    def cy = 5
    def radius = 5
    def height
    def angle

    public RoundPinGraphicsOperation() {
        super( "roundPin" )
    }

    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       pin = null
    }

    public Shape getShape( GraphicsContext context ) {
       if( pin == null ){
          calculateRoundPin()
       }
       pin
    }

    private void calculateRoundPin() {
       def h = height != null ? height : radius * 2
       def a = angle != null ? angle : 0
       pin = new RoundPin( cx as double,
                           cy as double,
                           radius as double,
                           h as double,
                           a as double )
    }
}