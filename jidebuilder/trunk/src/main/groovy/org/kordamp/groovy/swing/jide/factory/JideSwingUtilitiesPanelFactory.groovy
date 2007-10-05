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
import javax.swing.JPanel
import groovy.util.AbstractFactory
import groovy.util.FactoryBuilderSupport
import com.jidesoft.swing.JideSwingUtilities

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class JideSwingUtilitiesPanelFactory extends AbstractFactory {
    private def type

    JideSwingUtilitiesPanelFactory( type ){
       this.type = type
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      if( value && value instanceof Component ){
         if( !(value instanceof JPanel) ) value = new JPanel().add(value)
         switch( type ){
            case "left": return JideSwingUtilities.createLeftPanel(value);
            case "right": return JideSwingUtilities.createRightPanel(value);
            case "top": return JideSwingUtilities.createTopPanel(value);
            case "bottom": return JideSwingUtilities.createBottomPanel(value);
            default: return JideSwingUtilities.createLeftPanel(value);
         }
      }
      throw new RuntimeException("Failed to create component for '" + name + "' reason: missing value")
   }
}