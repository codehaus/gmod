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
import groovy.util.FactoryBuilderSupport
import com.jidesoft.swing.ComboBoxSearchable
import com.jidesoft.swing.OverlayComboBox

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ComboBoxSearchableFactory extends AbstractJideComponentFactory implements DelegatingJideFactory {
   public static final String COMBOBOX_SEARCHABLE = "_COMBOBOX_SEARCHABLE_"

   public ComboBoxSearchableFactory() {
      super( ComboBoxSearchable )
   }

   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      Object items = properties.remove("items")
      JComboBox comboBox = properties.remove("comboBox")
      Boolean overlayable = properties.remove("overlayable")
      if( !comboBox ){
         if( items instanceof Vector){
            if( !overlayable ){
               comboBox = new JComboBox((Vector) items)
            }else{
               comboBox = new OverlayComboBox((Vector) items)
            }
         }else if( items instanceof List ){
            List list = (List) items
            if( !overlayable ){
               comboBox =  new JComboBox(list.toArray())
            }else{
               comboBox =  new OverlayComboBox(list.toArray())
            }
         }else if( items instanceof Object[] ){
            if( !overlayable ){
               comboBox =  new JComboBox((Object[]) item )
            }else{
               comboBox =  new OverlayComboBox((Object[]) item )
            }
         }else{
            if( !overlayable ){
               comboBox =  new JComboBox()
            }else{
               comboBox =  new OverlayComboBox()
            }
         }
      }
      def searchable = new ComboBoxSearchable( comboBox )
      builder.context[(COMBOBOX_SEARCHABLE)] = searchable
      if( properties.id ){
         builder.setVariable( properties.id+"_searchable", searchable )
      }

      return comboBox
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node,
         Map attributes ) {
     setWidgetAttributes( builder, builder.context[(COMBOBOX_SEARCHABLE)], attributes, true )
     return true
   }
}