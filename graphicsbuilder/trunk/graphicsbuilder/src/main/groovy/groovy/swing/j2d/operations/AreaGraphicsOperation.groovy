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
import groovy.swing.j2d.impl.StrokingAndFillingGraphicsOperation

import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Shape
import java.awt.image.ImageObserver
import java.awt.geom.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class AreaGraphicsOperation extends ContextualGraphicsOperation {
    private def drawDelegate
    private String areaMethod
    private String name

    public AreaGraphicsOperation( String name, String areaMethod ){
        super( new StrokingAndFillingGraphicsOperation(new DrawGraphicsOperation()) )
        this.areaMethod = areaMethod
        this.name = name
        drawDelegate = this.delegate
    }

    public String getName(){
        return name
    }

    public Shape getClip( Graphics2D g, ImageObserver observer ){
        drawDelegate.shape = computeShape(g, observer)
        return drawDelegate.getClip( g, observer )
    }

    public void verify(){
        List operations = getOperations()
        operations.each { go ->
            go.verify()
        }
    }

    protected void executeDelegate( Graphics2D g, ImageObserver observer ){
        if( !drawDelegate.shape ){
            drawDelegate.shape = computeShape(g, observer)
        }
        super.executeDelegate( g, observer )
    }

    protected void executeChildOperation( Graphics2D g, ImageObserver observer, GraphicsOperation go ) {
        if( !hasShape(go) ){
           go.execute( g, observer )
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

    private Shape computeShape( Graphics2D g, ImageObserver observer ){
        Area area = new Area()
        List operations = getOperations()
        // find all operations with hasShape
        List ops = operations.grep{ go -> hasShape(go) }

        def size = ops.size()
        if( size ){
            area = new Area( ops[0].getClip(g,observer) )
        }
        ops[1..(size-1)].each { go ->
            Shape shape = go.getClip(g, observer)
            if( hasShape(go) && shape ){
                area."${areaMethod}"( new Area(shape) )
            }
        }
        return area
    }
}