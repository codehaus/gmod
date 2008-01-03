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

package groovy.swing.j2d.operations.shapes.path

import groovy.swing.j2d.GraphicsContext
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class CurveToExtPathOperation extends AbstractExtPathOperation {
    public static required = ['x1','x2','x3','y1','y2','y3']

    def x1
    def x2
    def x3
    def y1
    def y2
    def y3

    CurveToExtPathOperation(){
       super( "xcurveTo" )
    }

    public void apply( ExtendedGeneralPath path, GraphicsContext context ) {
       path.curveTo( x1 as float,
                     y1 as float,
                     x2 as float,
                     y2 as float,
                     x3 as float,
                     y3 as float )
    }
}