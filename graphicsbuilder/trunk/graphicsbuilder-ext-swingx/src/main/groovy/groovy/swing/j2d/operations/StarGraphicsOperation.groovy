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
import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation

import java.awt.Shape
import org.jdesktop.swingx.geom.Star2D

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class StarGraphicsOperation extends AbstractShapeGraphicsOperation {
   public static required = ["x", "y", "ir", "or", "count"]

   def x = 0
   def y = 0
   def ir = 5
   def or = 15
   def count = 5

   StarGraphicsOperation() {
      super( "star" )
   }

   public Shape getShape( GraphicsContext context ) {
      return new Star2D( x as double,
                         y as double,
                         ir as double,
                         or as double,
                         count as int )
   }
}