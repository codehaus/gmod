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

import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.impl.ContextualGraphicsOperation
import groovy.swing.j2d.impl.ShapeProviderGraphicsOperation
import groovy.swing.j2d.impl.StrokingAndFillingGraphicsOperation

import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Component
import java.awt.geom.*

/**
 * Applies an Area operation (any of [add,subtract,intersect,xor])
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class AreaGraphicsOperation extends ContextualGraphicsOperation {
    private def drawDelegate
    private String areaMethod
    private String name
    private List shapeProviderOperations = []
    private Shape shape

    public AreaGraphicsOperation( String name, String areaMethod ){
        super( new StrokingAndFillingGraphicsOperation(
                new ShapeProviderGraphicsOperation(new DrawGraphicsOperation())) )
        this.areaMethod = areaMethod
        this.name = name
        drawDelegate = this.delegate
    }

    public String getName(){
        return name
    }

    public boolean isDirty(){
        // shortcut
        if( super.isDirty() ){
           return true
        }
        findShapeProviderOperations()
        for( go in shapeProviderOperations ){
            if( go.isDirty() ){
                return true
            }
        }
        return false
    }

    public Shape getClip( Graphics2D g, Component target ) {
        if( shape == null || isDirty() ){
            shape = computeShape( g, target );
            drawDelegate.shape = computeShape(g, target)
            setDirty( false );
        }
        return shape;
    }

    public void verify(){
        List operations = getOperations()
        operations.each { go ->
            go.verify()
        }
    }

    protected void executeDelegate( Graphics2D g, Component target ){
        if( !drawDelegate.shape ){
            drawDelegate.shape = computeShape(g, target)
        }
        super.executeDelegate( g, target )
    }

    protected void executeChildOperation( Graphics2D g, Component target, GraphicsOperation go ) {
        if( !hasShape(go) ){
           go.execute( g, target )
        }
    }

    private boolean hasShape( GraphicsOperation go ){
        try{
            return go.hasShape
        }catch( MissingPropertyException mpe ){
            /// ignore
        }
        return false
    }

    protected Shape computeShape( Graphics2D g, Component target ){
        Area area = new Area()
        findShapeProviderOperations()
        def size = shapeProviderOperations.size()
        if( size ){
            area = new Area( shapeProviderOperations[0].getClip(g,target) )
        }
        shapeProviderOperations[1..<size].each { go ->
            Shape shape = go.getClip(g, target)
            if( hasShape(go) && shape ){
                area."${areaMethod}"( new Area(shape) )
            }
        }
        return area
    }

    private void findShapeProviderOperations(){
        if( shapeProviderOperations.isEmpty() ){
           List operations = getOperations()
           // find all operations with hasShape
           shapeProviderOperations = operations.grep{ go -> hasShape(go) }
        }
    }
}