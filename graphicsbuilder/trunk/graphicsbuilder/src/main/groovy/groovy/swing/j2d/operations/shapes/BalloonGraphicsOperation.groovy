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
import java.awt.geom.GeneralPath
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.geom.Balloon
import groovy.swing.j2d.geom.Triangle

import static java.lang.Math.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class BalloonGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = AbstractShapeGraphicsOperation.required + ['x','y','width','height','arc','tabWidth']
    public static optional = AbstractShapeGraphicsOperation.optional + ['tabHeight','tabLocation','tabDisplacement','anglePosition']

    private Balloon balloon

    def x = 0
    def y = 0
    def width = 20
    def height = 20
    def arc = 5
    def tabWidth = 5
    def tabHeight
    def tabLocation = Balloon.TAB_AT_BOTTOM
    def tabDisplacement = 0.5
    def anglePosition

    public BalloonGraphicsOperation() {
        super( "balloon" )
    }

    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       balloon = null
    }

    public Shape getShape( GraphicsContext context ) {
       if( balloon == null ){
          calculateBalloon()
       }
       balloon
    }

    public boolean hasXY() {
       true
    }
    
    private void calculateBalloon() {
       def tl = getTabLocation()
       def ap = getAnglePosition()
       def th = tabHeight == null ? tabWidth/2 : tabHeight
       balloon = new Balloon( x as double,
                              y as double,
                              width as double,
                              height as double,
                              arc as double,
                              tabWidth as double,
                              th as double,
                              tl as int,
                              tabDisplacement as double,
                              ap as int )
    }

    private def getTabLocation(){
       if( tabLocation == null ) return Balloon.TAB_AT_BOTTOM
       if( tabLocation instanceof Number ){
          return tabLocation.intValue()
       }
       if( tabLocation instanceof String ){
          switch( tabLocation ){
             case "bottom": return Balloon.TAB_AT_BOTTOM
             case "left": return Balloon.TAB_AT_LEFT
             case "right": return Balloon.TAB_AT_RIGHT
             case "top": return Balloon.TAB_AT_TOP
          }
       }
       return Balloon.TAB_AT_BOTTOM
    }

    private def getAnglePosition(){
       if( anglePosition == null ) return -1
       if( anglePosition instanceof Number ){
          return anglePosition.intValue()
       }
       if( anglePosition instanceof String ){
          switch( anglePosition ){
             case "start": return Triangle.ANGLE_AT_START
             case "end": return Triangle.ANGLE_AT_END
             default: return -1
          }
       }
       return -1
    }
}