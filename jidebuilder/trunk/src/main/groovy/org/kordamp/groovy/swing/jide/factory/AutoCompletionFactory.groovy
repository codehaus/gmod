/*
 * Copyright 2007-2008 the original author or authors.
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

import com.jidesoft.swing.AutoCompletion
import com.jidesoft.swing.AutoCompletionComboBox
import com.jidesoft.swing.OverlayTextField
import com.jidesoft.swing.Searchable

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class AutoCompletionFactory extends AbstractJideComponentFactory implements DelegatingJideFactory {
    public static final String AUTOCOMPLETION = "_AUTOCOMPLETION_"

    public AutoCompletionFactory() {
         super( AutoCompletion )
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      JComboBox comboBox = properties.remove("comboBox")
      JTextComponent textComponent = properties.remove("textComponent")
      Searchable searchable = properties.remove("searchable")
      Boolean overlayable = properties.remove("overlayable")
      def list = properties.remove("list")

      if( comboBox ){
         def autocompletion = buildAutoCompletionComboBox( name, comboBox, searchable )
         builder.context[(AUTOCOMPLETION)] = autocompletion
         if( properties.id ){
            builder.setVariable( properties.id+"_autocompletion", autocompletion )
         }
         return comboBox
      }else{
         if( !textComponent ){
            if( !overlayable ){
               textComponent = new JTextField()
            }else{
               textComponent = new OverlayTextField()
            }
         }
         def autocompletion = buildAutoCompletionTextComponent( name, textComponent, searchable, list )
         builder.context[(AUTOCOMPLETION)] = autocompletion
         if( properties.id ){
            builder.setVariable( properties.id+"_autocompletion", autocompletion )
         }
         return textComponent
      }
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node,
          Map attributes ) {
      setWidgetAttributes( builder, builder.context[(AUTOCOMPLETION)], attributes, true )
      return true
   }

   private Object buildAutoCompletionComboBox( name, comboBox, searchable ){
      if( !searchable ){
         return new AutoCompletion( comboBox )
      }else{
         return new AutoCompletion( comboBox, searchable )
      }
   }

   private Object buildAutoCompletionTextComponent( name, textComponent, searchable, list ){
      if( !searchable ){
         if( !list ){
            throw new RuntimeException("Failed to create component for '" + name +
                  "' reason: specify one of ['searchable','list'] ")
         }else{
            return new AutoCompletion( textComponent, list )
         }
      }else{
         return new AutoCompletion( textComponent, searchable )
      }
   }
}