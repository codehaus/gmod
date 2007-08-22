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

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import groovy.swing.j2d.impl.DelegatingGraphicsOperation;
import groovy.swing.j2d.impl.FillingGraphicsOperation;
import groovy.swing.j2d.impl.MethodInvokingGraphicsOperation;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ColorGraphicsOperation extends DelegatingGraphicsOperation implements
      FillingGraphicsOperation {
   public ColorGraphicsOperation() {
      super( "color", new String[] { "color" }, new MethodInvokingGraphicsOperation( "setColor",
            new String[] { "color" } ) );
   }

   protected void setupDelegateProperties(Graphics2D g, ImageObserver observer) {
      // empty
   }
}