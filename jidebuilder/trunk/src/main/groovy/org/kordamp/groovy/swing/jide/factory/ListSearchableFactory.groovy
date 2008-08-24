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

import javax.swing.JList
import com.jidesoft.swing.ListSearchable

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ListSearchableFactory extends AbstractJideComponentFactory implements DelegatingJideFactory {
   public static final String LIST_SEARCHABLE = "_LIST_SEARCHABLE_"

   public ListSearchableFactory() {
      super( ListSearchable )
   }

   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      JList list = properties.remove("list")
      if( !list ){
         list =  new JList()
      }
      def searchable = new ListSearchable( list )
      builder.context[(LIST_SEARCHABLE)] = searchable
      if( properties.id ){
         builder.setVariable( properties.id+"_searchable", searchable )
      }
      if( properties.listData ){
         def listData = properties.listData
         if( listData instanceof List && !(listData instanceof Vector) ){
            properties.listData = new Vector(listData)
         }
      }
      return list
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node,
         Map attributes ) {
     setWidgetAttributes( builder, builder.context[(LIST_SEARCHABLE)], attributes, true )
     return true
   }
}