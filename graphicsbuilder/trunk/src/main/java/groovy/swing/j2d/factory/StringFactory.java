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

package groovy.swing.j2d.factory;

import groovy.swing.j2d.GraphicsOperation;
import groovy.swing.j2d.impl.StrokingAndFillingGraphicsOperation;
import groovy.util.FactoryBuilderSupport;

import java.util.Map;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StringFactory extends ContextualGraphicsOperationFactory {
   public GraphicsOperation getGraphicsOperation( FactoryBuilderSupport builder, Object name,
         Object value, Map properties ) throws InstantiationException, IllegalAccessException {
      return new StrokingAndFillingGraphicsOperation( "drawString",
            new String[] { "text", "x", "y" } );
   }
}