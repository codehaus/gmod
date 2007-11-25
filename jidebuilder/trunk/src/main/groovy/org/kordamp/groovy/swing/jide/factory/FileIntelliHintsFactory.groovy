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

import javax.swing.text.JTextComponent
import groovy.util.FactoryBuilderSupport
import com.jidesoft.hints.FileIntelliHints

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class FileIntelliHintsFactory extends AbstractJideComponentFactory implements DelegatingJideFactory {
   public FileIntelliHintsFactory() {
      super( FileIntelliHints )
   }

   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      JTextComponent textComponent = properties.remove("textComponent")
      if( !textComponent ){
         throw new RuntimeException("Failed to create component for '${name}' reason: missing 'textComponent' attribute")
      }
      return new FileIntelliHints( textComponent )
   }
}