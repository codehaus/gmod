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
 * limitations under the License.
 */

package org.kordamp.groovy.swing.jide.factory

import java.awt.Container
import groovy.util.AbstractFactory
import groovy.util.FactoryBuilderSupport
import com.jidesoft.swing.JideBoxLayout

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class JideBoxLayoutFactory extends AbstractFactory {
   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      def container = properties.remove("container")
      if( !container )
         throw new RuntimeException("Failed to create component for '" + name + "' reason: missing 'container' attribute")
      if( !(container instanceof Container) )
         throw new RuntimeException("Failed to create component for '" + name + "' reason: 'container' is not of type "+Container.class.name)

      def axis = properties.remove("axis")
      def gap = properties.remove("gap")
      if( axis == null ) axis = JideBoxLayout.X_AXIS
      if( gap == null ) gap = 0
      return new JideBoxLayout( container, axis, gap )
   }
}