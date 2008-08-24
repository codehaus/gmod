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

import java.awt.Container
import com.jidesoft.swing.Searchable
import org.kordamp.groovy.swing.jide.impl.SearchableBarWrapper

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SearchableBarFactory extends AbstractJideComponentFactory implements DelegatingJideFactory {
    public SearchableBarFactory() {
       super( SearchableBarWrapper )
    }

   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      def searchable = properties.remove("searchable")
      if( !searchable )
         throw new RuntimeException("Failed to create component for '"
               + name + "' reason: missing 'searchable' attribute")
      if( !(searchable instanceof Searchable) )
         throw new RuntimeException("Failed to create component for '"
               + name + "' reason: 'searchable' is not of type "+Searchable.class.name)

      def initialText = properties.remove("initialText")
      def compact = properties.remove("compact")
      if( initialText == null ) initialText = ""
      if( compact == null ) compact = false
      return new SearchableBarWrapper( searchable, initialText, compact )
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node,
         Map attributes ) {
     setWidgetAttributes( builder, node.searchable, attributes, true )
     return true
   }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
      if( child.install ){
         child.installOnContainer( parent )
      }
   }
}