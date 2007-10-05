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

import java.awt.Component
import groovy.util.AbstractFactory
import groovy.util.FactoryBuilderSupport
import com.jidesoft.swing.Animator

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class AnimatorFactory extends AbstractFactory {
   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      Component source = properties.remove("source")
      if( !source ){
         throw new RuntimeException("Failed to create component for '" + name + "' reason: missing 'source' attribute")
      }
      def initDelay = properties.remove("initDelay")
      def delay = properties.remove("delay")
      def totalSteps = properties.remove("totalSteps")
      if( initDelay == null ) initDelay = 50
      if( delay == null ) delay = 10
      if( totalSteps == null ) totalSteps = 10
      return new Animator( source, initDelay, delay, totalSteps )
   }
}