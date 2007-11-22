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
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation

import java.awt.Shape

import org.jdesktop.swingx.geom.Morphing2D

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class MorphGraphicsOperation extends AbstractShapeGraphicsOperation {
   def start
   def end
   def morph = 0

   MorphGraphicsOperation() {
      super( "morph", ["start", "end", "morph"] as String[] )
   }

   public boolean isDirty() {
      def start = getParameterValue( "start" )
      def end = getParameterValue( "end" )
      boolean startIsDirty = start instanceof GraphicsOperation ? start?.isDirty() : false;
      boolean endIsDirty = end instanceof GraphicsOperation ? end?.isDirty() : false;
      return startIsDirty || endIsDirty || super.isDirty()
   }

   protected Shape computeShape( GraphicsContext context ) {
      def start = getParameterValue( "start" )
      def end = getParameterValue( "end" )
      double morphing = getParameterValue( "morph" )

      if( start instanceof GraphicsOperation && start.parameterHasValue("asShape") &&
            start.getParameterValue("asShape") ){
         start = start.getClip(context)
      }
      if( end instanceof GraphicsOperation && end.parameterHasValue("asShape") &&
            end.getParameterValue("asShape") ){
         end = end.getClip(context)
      }

      Morphing2D morph = new Morphing2D( start, end )
      morph.setMorphing( morphing )
      return morph
   }
}