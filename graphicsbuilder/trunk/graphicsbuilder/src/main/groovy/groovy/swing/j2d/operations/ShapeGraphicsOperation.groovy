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
import groovy.swing.j2d.ShapeProvider
import java.awt.Shape

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShapeGraphicsOperation extends AbstractGraphicsOperation implements ShapeProvider {
    protected static required = ['shape']

    def shape

    ShapeGraphicsOperation() {
        super( "shape" )
    }

    public void execute( GraphicsContext context ){
        // empty
    }
    
    public Shape getShape( GraphicsContext context ){
       if( shape instanceof ShapeProvider ){
          return shape.getShape(context)
       }else if( shape instanceof Shape ){
          return shape
       }    
       throw new IllegalArgumentException("shape.shape must be one of [java.awt.Shape,ShapeProvider]")    
    }
}