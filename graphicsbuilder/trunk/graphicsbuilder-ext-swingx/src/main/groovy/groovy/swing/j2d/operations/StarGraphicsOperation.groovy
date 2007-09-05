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

import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation

import java.awt.Graphics2D
import java.awt.Shape
import java.awt.image.ImageObserver

import org.jdesktop.swingx.geom.Star2D

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class StarGraphicsOperation extends AbstractShapeGraphicsOperation {
   def x
   def y
   def ir
   def or
   def count

   StarGraphicsOperation() {
      super( "star", ["x", "y", "ir", "or", "count"] as String[] )
   }

   protected Shape computeShape(Graphics2D g, ImageObserver observer) {
      double x = getParameterValue( "x" )
      double y = getParameterValue( "y" )
      double ir = getParameterValue( "ir" )
      double or = getParameterValue( "or" )
      int count = getParameterValue( "count" )
      return new Star2D( x, y, ir, or, count )
   }
}