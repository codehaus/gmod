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
import groovy.swing.j2d.OutlineProvider
import java.awt.Shape

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class OulineGraphicsOperation extends AbstractGraphicsOperation implements OutlineProvider {
    protected static required = ['outline']

    def outline

    OulineGraphicsOperation() {
        super( "outline" )
    }

    public void execute( GraphicsContext context ){
        // empty
    }
    
    public Shape getShape( GraphicsContext context ){
       if( shape instanceof OutlineProvider ){
          return shape.getShape(context)
       }else if( shape instanceof Shape ){
          // TODO check against Line2D, CubicCurve and QuadCurve
          return shape
       }    
       throw new IllegalArgumentException("shape.shape must be one of [java.awt.Shape,OutlineProvider]")    
    }
}