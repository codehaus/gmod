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

import java.awt.geom.AffineTransform
import groovy.swing.j2d.impl.AbstractTransformation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class MatrixTransformation extends AbstractTransformation {
    public static required = ['m00','m10','m01','m11','m02','m12']

    def m00
    def m10
    def m01
    def m11
    def m02
    def m12

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