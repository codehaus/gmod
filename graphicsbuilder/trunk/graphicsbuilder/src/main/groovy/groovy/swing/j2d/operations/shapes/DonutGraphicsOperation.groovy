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
import org.kordamp.jsilhouette.geom.Donut

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class DonutGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = AbstractShapeGraphicsOperation.required + ['cx','cy','or','ir']
    public static optional = AbstractShapeGraphicsOperation.optional + ['angle','sides']

    private Donut star

    def cx = 5
    def cy = 5
    def or = 8
    def ir = 3
    def sides = 0
    def angle = 0

    public DonutGraphicsOperation() {
        super( "star" )
    }

    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       star = null
    }

    public Shape getShape( GraphicsContext context ) {
       if( star == null ){
          calculateDonut()
       }
       return star
    }

    public boolean hasCenter() {
       true
    }
    
    private void calculateDonut() {
       star = new Donut( cx as float,
                        cy as float,
                        or as float,
                        ir as float,
                        sides as int,
                        angle as float )
    }
}