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

package groovy.swing.j2d;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.ImageObserver;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public interface GraphicsOperation {
   void execute( Graphics2D g, ImageObserver observer );

   Shape getClip( Graphics2D g, ImageObserver observer );

   String[] getParameterNames();

   Object getParameterValue( String name );

   boolean parameterHasValue( String name );

   void setParameterValue( String name, Object value );
}