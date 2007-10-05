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

import javax.swing.JComboBox
import javax.swing.JTextField
import javax.swing.text.JTextComponent

import groovy.util.AbstractFactory
import groovy.util.FactoryBuilderSupport
import org.kordamp.groovy.swing.jide.impl.AutoCompletionWrapper
import com.jidesoft.swing.AutoCompletionComboBox
import com.jidesoft.swing.OverlayTextField
import com.jidesoft.swing.Searchable

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class AutoCompletionFactory extends AbstractFactory {
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      JComboBox comboBox = properties.remove("comboBox")
      JTextComponent textComponent = properties.remove("textComponent")
      Searchable searchable = properties.remove("searchable")
      Boolean overlayable = properties.remove("overlayable")
      def list = properties.remove("list")

      if( comboBox ){
         return buildAutoCompletionComboBox( name, comboBox, searchable )
      }else{
         if( !textComponent ){
            if( !overlayable ){
               textComponent = new JTextField()
            }else{
               textComponent = new OverlayTextField()
            }
         }
         return buildAutoCompletionTextComponent( name, textComponent, searchable, list )
      }
   }

   private Object buildAutoCompletionComboBox( name, comboBox, searchable ){
      if( !searchable ){
         return new AutoCompletionWrapper( comboBox )
      }else{
         return new AutoCompletionWrapper( comboBox, searchable )
      }
   }

   private Object buildAutoCompletionTextComponent( name, textComponent, searchable, list ){
      if( !searchable ){
         if( !list ){
            throw new RuntimeException("Failed to create component for '" + name +
                  "' reason: specify one of ['searchable','list'] ")
         }else{
            return new AutoCompletionWrapper( textComponent, list )
         }
      }else{
         return new AutoCompletionWrapper( textComponent, searchable )
      }
   }
}