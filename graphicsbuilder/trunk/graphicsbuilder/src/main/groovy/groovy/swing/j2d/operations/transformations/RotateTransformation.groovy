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

package groovy.swing.j2d.operations.transformations

import java.awt.geom.AffineTransform

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class RotateTransformation extends AbstractTransformation {
    public static required = ['angle']
    public static optional = ['x','y']

    def angle = 0
    def x
    def y

    public RotateTransformation() {
        super( "rotate" )
    }

    public AffineTransform getTransform() {
       if( angle == 0 ) {
          return new AffineTransform()
       }else if( x != null && y != null ){
          return AffineTransform.getRotateInstance( Math.toRadians(angle as double), x as double, y as double )
       }else{
          return AffineTransform.getRotateInstance( Math.toRadians(angle as double) as double )
       }
    }
}