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

import groovy.swing.j2d.impl.AbstractGraphicsOperation

import java.awt.Graphics2D
import java.awt.Shape
import java.awt.image.ImageObserver

import org.jdesktop.swingx.geom.Morphing2D

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class MorphGraphicsOperation extends AbstractGraphicsOperation {
   def from
   def to
   def morphing

   static fillable = true
   static contextual = true
   static hasShape = true

   MorphGraphicsOperation() {
      super( "morph", ["from", "to", "morphing"] as String[] )
   }

   public Shape getClip(Graphics2D g, ImageObserver observer) {
      Shape from = getParameterValue( "from" )
      Shape to = getParameterValue( "to" )
      double morphing = getParameterValue( "morphing" )

      Morphing2D morph = new Morphing2D( from, to )
      morph.setMorphing( morphing )
      return morph
   }

   protected void doExecute( Graphics2D g, ImageObserver observer ){
      g.draw( getClip( g, observer ) )
   }
}