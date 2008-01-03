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

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.ShapeProvider

import java.awt.Shape
import java.awt.geom.Area
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class AreaGraphicsOperation extends AbstractShapeGraphicsOperation {
    private String areaMethod
    private def shapeProviders
    private Area area

    public AreaGraphicsOperation( String name, String methodName ) {
        super( "area-"+name )
        this.areaMethod = methodName
    }

    public Shape getShape( GraphicsContext context ) {
        if( area == null ){
           calculateArea( context )
        }
        area
    }

    public void propertyChange( PropertyChangeEvent event ){
       area = null
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
       if( !(go instanceof ShapeProvider) ) go.execute( context )
    }

    private void calculateArea( GraphicsContext context ) {
        def gos = shapeProviders ? shapeProviders : operations.findAll { it instanceof ShapeProvider }
        if( !gos ) {
           // no nested shapes
           throw new IllegalArgumentException("No nested shapes on ${this}")
        }

        area = new Area( gos[0].getLocallyTransformedShape(context) )
        gos[1..-1].each {
           it.addPropertyChangeListener( this )
           area."$areaMethod"( new Area(it.getLocallyTransformedShape(context)) )
        }
    }
}