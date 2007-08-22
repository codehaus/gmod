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

package groovy.swing.j2d.operations;

import groovy.swing.j2d.impl.DelegatingGraphicsOperation;
import groovy.swing.j2d.impl.FillSupportGraphicsOperation;
import groovy.swing.j2d.impl.StrokingAndFillingGraphicsOperation;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.ImageObserver;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.jdesktop.swingx.geom.Morphing2D;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class MorphGraphicsOperation extends DelegatingGraphicsOperation implements
      FillSupportGraphicsOperation {
   public MorphGraphicsOperation() {
      super( "morph", new String[] { "from", "to", "morphing" },
            new StrokingAndFillingGraphicsOperation( "draw", new String[] { "shape" } ) );
   }

   public Shape getClip(Graphics2D g, ImageObserver observer) {
      Shape from = (Shape) getParameterValue( "from" );
      Shape to = (Shape) getParameterValue( "to" );
      double morphing = ((Number) getParameterValue( "morphing" )).doubleValue();

      Morphing2D morph = new Morphing2D( from, to );
      morph.setMorphing( morphing );
      return morph;
   }

   protected void setupDelegateProperties(Graphics2D g, ImageObserver observer) {
      InvokerHelper.setProperty( getDelegate(), "shape", getClip(g, observer) );
   }
}