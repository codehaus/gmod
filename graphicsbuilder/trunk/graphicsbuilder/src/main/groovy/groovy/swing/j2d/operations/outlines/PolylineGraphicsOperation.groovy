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

package groovy.swing.j2d.operations.outlines

import java.awt.Shape
import java.awt.geom.GeneralPath
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class PolylineGraphicsOperation extends AbstractOutlineGraphicsOperation {
    public static required = ['points']

    private GeneralPath path

    def points

    public PolylineGraphicsOperation() {
        super( "polyline" )
    }

    public Shape getShape( GraphicsContext context) {
       if( path == null ){
          calculatePath()
       }
       path
    }

    protected void localPropertyChange( PropertyChangeEvent event ) {
       super.localPropertyChange( event )
       path = null
    }

    private void calculatePath() {
       if( points.size() == 0 ){
           // TODO throw exception
           return null
       }

       if( points.size() % 2 == 1 ){
           throw new IllegalStateException( "Odd number of points" )
       }

       int npoints = points.size() / 2
       if( npoints < 2 ){
           // TODO throw exception
           return null
       }

       path = new GeneralPath()
       npoints.times { i ->
           Object ox = points.get( 2 * i )
           Object oy = points.get( (2 * i) + 1 )
           def x = convertToInteger( ox, 2 * 1 )
           def y = convertToInteger( oy, (2 * i) + 1 )
           switch( i ){
              case 0:
                 path.moveTo( x, y )
                 break;
              default:
                 path.lineTo( x, y )
                 break;
           }
       }
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