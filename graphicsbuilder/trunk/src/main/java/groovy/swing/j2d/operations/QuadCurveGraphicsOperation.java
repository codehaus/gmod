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
import groovy.swing.j2d.impl.StrokingGraphicsOperation;

import java.awt.Graphics2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.ImageObserver;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class QuadCurveGraphicsOperation extends DelegatingGraphicsOperation {
   public QuadCurveGraphicsOperation() {
      super( "quadCurve", new String[] { "x1", "x2", "y1", "y2", "ctrlx", "ctrly" },
            new StrokingGraphicsOperation( "draw", new String[] { "shape" } ) );
   }

   protected void setupDelegateProperties(Graphics2D g, ImageObserver observer) {
      double x1 = ((Number) getParameterValue( "x1" )).doubleValue();
      double x2 = ((Number) getParameterValue( "x2" )).doubleValue();
      double y1 = ((Number) getParameterValue( "y1" )).doubleValue();
      double y2 = ((Number) getParameterValue( "y2" )).doubleValue();
      double ctrlx = ((Number) getParameterValue( "ctrlx" )).doubleValue();
      double ctrly = ((Number) getParameterValue( "ctrly" )).doubleValue();

      InvokerHelper.setProperty( getDelegate(), "shape", new QuadCurve2D.Double( x1, y1, ctrlx,
            ctrly, x2, y2 ) );
   }
}