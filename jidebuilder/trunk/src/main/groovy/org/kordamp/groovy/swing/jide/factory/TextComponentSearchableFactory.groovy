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

import javax.swing.JTextField
import javax.swing.text.JTextComponent
import com.jidesoft.swing.TextComponentSearchable
import com.jidesoft.swing.OverlayTextField

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class TextComponentSearchableFactory extends AbstractJideComponentFactory implements DelegatingJideFactory {
   public static final String TEXT_SEARCHABLE = "_TEXT_SEARCHABLE_"

   public TextComponentSearchableFactory() {
      super( TextComponentSearchable )
   }

   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      JTextComponent textComponent = properties.remove("textComponent")
      if( !textComponent ){
         Boolean overlayable = properties.remove("overlayable")
         if( !overlayable ){
            textComponent = new JTextField()
         }else{
            textComponent = new OverlayTextField()
         }
      }
      def searchable = new TextComponentSearchable( textComponent )
      builder.context[(TEXT_SEARCHABLE)] = searchable
      if( properties.id ){
         builder.setVariable( properties.id+"_searchable", searchable )
      }
      return textComponent
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node,
         Map attributes ) {
     setWidgetAttributes( builder, builder.context[(TEXT_SEARCHABLE)], attributes, true )
     return true
   }
}