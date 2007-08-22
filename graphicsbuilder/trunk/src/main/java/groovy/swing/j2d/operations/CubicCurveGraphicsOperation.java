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
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.image.ImageObserver;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class CubicCurveGraphicsOperation extends DelegatingGraphicsOperation {
   public CubicCurveGraphicsOperation() {
      super( "cubicCurve", new String[] { "x1", "x2", "y1", "y2", "ctrlx1", "ctrlx2", "ctrly1",
            "ctrly2" }, new StrokingGraphicsOperation( "draw", new String[] { "shape" } ) );
   }

   public Shape getClip( Graphics2D g, ImageObserver observer ) {
      double x1 = ((Number) getParameterValue( "x1" )).doubleValue();
      double x2 = ((Number) getParameterValue( "x2" )).doubleValue();
      double y1 = ((Number) getParameterValue( "y1" )).doubleValue();
      double y2 = ((Number) getParameterValue( "y2" )).doubleValue();
      double ctrlx1 = ((Number) getParameterValue( "ctrlx1" )).doubleValue();
      double ctrlx2 = ((Number) getParameterValue( "ctrlx2" )).doubleValue();
      double ctrly1 = ((Number) getParameterValue( "ctrly1" )).doubleValue();
      double ctrly2 = ((Number) getParameterValue( "ctrly2" )).doubleValue();
      return new CubicCurve2D.Double( x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2 );
   }

   protected void setupDelegateProperties( Graphics2D g, ImageObserver observer ) {
      InvokerHelper.setProperty( getDelegate(), "shape", getClip( g, observer ) );
   }
}