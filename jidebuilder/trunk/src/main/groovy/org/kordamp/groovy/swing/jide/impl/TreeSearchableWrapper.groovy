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

import javax.swing.JTree
import com.jidesoft.swing.TreeSearchable

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class TreeSearchableWrapper extends TreeSearchable implements SearchableWrapper {
   private JTree tree

   TreeSearchableWrapper( JTree tree ){
      super( tree )
      this.tree = tree
   }

   def getDelegateWidget(){ tree }

   public String toString(){ "${super.toString()} -> ${tree.toString()}" }

   public Object invokeMethod( String name, Object args ){
      return InvokerHelper.invokeMethod( tree, name, args )
   }
}
