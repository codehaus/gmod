/*
 * Copyright 2007-2008 the original author or authors.
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
import org.kordamp.jsilhouette.geom.Rays

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class RaysGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = AbstractShapeGraphicsOperation.required + ['cx','cy','radius','rays']
    public static optional = AbstractShapeGraphicsOperation.optional + ['angle','extent','rounded']

    private Rays shape

    def cx = 5
    def cy = 5
    def radius = 5
    def rays = 2
    def angle = 0
    def extent = 0.5
    def rounded = false

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

    public boolean hasCenter() {
       true
    }
    
    private void calculateRays() {
       shape = new Rays( cx as float,
                          cy as float,
                          radius as float,
                          rays as int,
                          angle as float,
                          extent as float,
                          rounded as boolean )
    }
}