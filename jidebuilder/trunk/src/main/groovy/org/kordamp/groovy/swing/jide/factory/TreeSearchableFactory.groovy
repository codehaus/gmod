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

import javax.swing.JTree
import groovy.util.FactoryBuilderSupport
import com.jidesoft.swing.TreeSearchable

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class TreeSearchableFactory extends AbstractJideComponentFactory implements DelegatingJideFactory {
   public static final String TREE_SEARCHABLE = "_TREE_SEARCHABLE_"

   public TreeSearchableFactory() {
      super( TreeSearchable )
   }

   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      JTree tree = properties.remove("tree")
      if( !tree ){
         tree =  new JTree()
      }
      def searchable = new TreeSearchable( tree )
      builder.context[(TREE_SEARCHABLE)] = searchable
      if( properties.id ){
         builder.setVariable( properties.id+"_searchable", searchable )
      }
      return table
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node,
         Map attributes ) {
     setWidgetAttributes( builder, builder.context[(TREE_SEARCHABLE)], attributes, true )
     return true
   }
}