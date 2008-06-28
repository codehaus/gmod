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

package groovy.swing.j2d.operations.transformations

import java.awt.geom.AffineTransform

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class MatrixTransformation extends AbstractTransformation {
    public static required = ['m00','m10','m01','m11','m02','m12']

    def m00 = 1
    def m10 = 0
    def m01 = 0
    def m11 = 1
    def m02 = 0
    def m12 = 0

    public MatrixTransformation() {
        super( "matrix" )
    }

    public AffineTransform getTransform() {
       return new AffineTransform( m00 as double,
                                   m10 as double,
                                   m01 as double,
                                   m11 as double,
                                   m02 as double,
                                   m12 as double )
    }
}