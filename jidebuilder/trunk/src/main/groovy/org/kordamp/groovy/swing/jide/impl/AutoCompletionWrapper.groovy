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

package org.kordamp.groovy.swing.jide.impl

import javax.swing.JComboBox
import javax.swing.text.JTextComponent
import com.jidesoft.swing.AutoCompletion
import com.jidesoft.swing.Searchable

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class AutoCompletionWrapper extends AutoCompletion {
   private def delegate

   AutoCompletionWrapper( JComboBox comboBox ){
      super( comboBox )
      this.delegate = comboBox
   }

   AutoCompletionWrapper( JComboBox comboBox, Searchable searchable ){
      super( comboBox, searchable )
      this.delegate = comboBox
   }

   AutoCompletionWrapper( JTextComponent textComponent, Searchable searchable ){
      super( textComponent, searchable )
      this.delegate = textComponent
   }

   AutoCompletionWrapper( JTextComponent textComponent, List list ){
      super( textComponent, list )
      this.delegate = textComponent
   }

   AutoCompletionWrapper( JTextComponent textComponent, Object[] array ){
      super( textComponent, array )
      this.delegate = textComponent
   }

   def getDelegateWidget(){ delegate }

   public String toString(){ "${super.toString()} -> ${delegate.toString()}" }

   def methodMissing( String name, args ) {
      return InvokeHelper.invokeMethod( delegate, name, args )
   }

   def propertyMissing( String name, value ){ delegate[name] = value }

   def propertyMissing( String name ){ delegate[name] }
}