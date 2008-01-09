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
import groovy.swing.j2d.geom.Rays

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class RaysGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = super.required + ['cx','cy','radius','rays']
    public static optional = super.optional + ['angle','extent']

    private Rays shape

    def cx = 5
    def cy = 5
    def radius = 5
    def rays = 2
    def angle = 0
    def extent = 0.5

    public RaysGraphicsOperation() {
        super( "rays" )
    }

    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       shape = null
    }

    public Shape getShape( GraphicsContext context ) {
       if( shape == null ){
          calculateRays()
       }
       shape
    }

    private void calculateRays() {
       shape = new Rays( cx as double,
                          cy as double,
                          radius as double,
                          rays as int,
                          angle as double,
                          extent as double )
    }
}