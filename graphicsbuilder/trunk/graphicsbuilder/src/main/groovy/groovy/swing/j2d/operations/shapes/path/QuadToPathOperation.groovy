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

package groovy.swing.j2d.operations.shapes.path

import groovy.swing.j2d.GraphicsContext
import java.awt.geom.GeneralPath

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class QuadToPathOperation extends AbstractPathOperation {
    public static required = ['x1','x2','y1','y2']

    def x1
    def x2
    def y1
    def y2

    QuadToPathOperation(){
       super("quadTo")
    }

    public void apply( GeneralPath path, GraphicsContext context ) {
       path.quadTo( x1 as double,
                    y1 as double,
                    x2 as double,
                    y2 as double )
    }
}