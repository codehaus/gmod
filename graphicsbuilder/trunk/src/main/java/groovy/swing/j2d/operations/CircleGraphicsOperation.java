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
import java.awt.geom.Ellipse2D;
import java.awt.image.ImageObserver;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class CircleGraphicsOperation extends DelegatingGraphicsOperation implements
      FillSupportGraphicsOperation {
   public CircleGraphicsOperation() {
      super( "circle", new String[] { "cx", "cy", "radius" },
            new StrokingAndFillingGraphicsOperation( "drawArc", new String[] { "x", "y", "width",
                  "height", "startAngle", "arcAngle" } ) );
   }

   public Shape getClip(Graphics2D g, ImageObserver observer) {
      int radius = ((Number) getParameterValue( "radius" )).intValue();
      int cx = ((Number) getParameterValue( "cx" )).intValue();
      int cy = ((Number) getParameterValue( "cy" )).intValue();
      return new Ellipse2D.Double( cx - radius, cy - radius, radius * 2, radius * 2 );
   }

   protected void setupDelegateProperties(Graphics2D g, ImageObserver observer) {
      int radius = ((Number) getParameterValue( "radius" )).intValue();
      int cx = ((Number) getParameterValue( "cx" )).intValue();
      int cy = ((Number) getParameterValue( "cy" )).intValue();

      Object delegate = getDelegate();
      InvokerHelper.setProperty( delegate, "x", new Integer( cx - radius ) );
      InvokerHelper.setProperty( delegate, "y", new Integer( cy - radius ) );
      InvokerHelper.setProperty( delegate, "width", new Integer( radius * 2 ) );
      InvokerHelper.setProperty( delegate, "height", new Integer( radius * 2 ) );
      InvokerHelper.setProperty( delegate, "startAngle", new Integer( 0 ) );
      InvokerHelper.setProperty( delegate, "arcAngle", new Integer( 360 ) );
   }
}