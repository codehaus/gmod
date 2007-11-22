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

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.AbstractGraphicsOperation

import java.awt.Shape

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class PolylineGraphicsOperation extends AbstractGraphicsOperation {
    List points

    static strokable = true
    static contextual = true
    static hasShape = true

    PolylineGraphicsOperation() {
        super( "polyline", ["points"] as String[] )
    }

    protected void doExecute( GraphicsContext context ){
        List points = getParameterValue( "points" )
        if( points.size() == 0 ){
            return null
        }

        if( points.size() % 2 == 1 ){
            throw new IllegalStateException( "Odd number of points" )
        }

        int npoints = points.size() / 2
        int[] xpoints = new int[npoints]
        int[] ypoints = new int[npoints]
        npoints.times { i ->
            Object ox = points.get( 2 * i )
            Object oy = points.get( (2 * i) + 1 )
            xpoints[i] = convertToInteger( ox, 2 * 1 )
            ypoints[i] = convertToInteger( oy, (2 * i) + 1 )
        }
        context.g.drawPolyline( xpoints, ypoints, npoints )
    }

    private int convertToInteger( Object o, int index ) {
        int p = 0
        if( o == null ){
            throw new IllegalStateException( ((index % 2 == 0) ? "x" : "y") + "[" + index
                    + "] is null" )
        }
        if( o instanceof Closure ){
            o = o.call()
        }
        if( o instanceof Number ){
            p = o
        }else{
            throw new IllegalStateException( ((index % 2 == 0) ? "x" : "y") + "[" + index
                    + "] is not a number" )
        }
        return p
    }
}