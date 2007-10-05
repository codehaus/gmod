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

import java.awt.BorderLayout
import java.awt.Container
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.KeyStroke
import com.jidesoft.swing.Searchable
import com.jidesoft.swing.SearchableBar

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SearchableBarWrapper extends SearchableBar {
   Map install

   SearchableBarWrapper( Searchable searchable ){
      super( searchable )
   }

   SearchableBarWrapper( Searchable searchable, String initialText ){
      super( searchable, initialText, false )
   }

   SearchableBarWrapper( Searchable searchable, boolean compact ){
      super( searchable, compact )
   }

   SearchableBarWrapper( Searchable searchable, String initialText, boolean compact ){
      super( searchable, initialText, compact )
   }

   public void installOnContainer( Container container ){
      if( install ){
         def constraints = install.get("constraints", BorderLayout.PAGE_END)
         setInstaller([
            openSearchBar: { searchableBar ->
               container.add(searchableBar, constraints)
               container.invalidate()
               container.revalidate()
            },
            closeSearchBar: { searchableBar ->
               container.remove(searchableBar)
               container.invalidate()
               container.revalidate()
            }
         ] as SearchableBar.Installer)
         def keyStroke = install.get( "keyStroke", KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK) )
         searchable.component.registerKeyboardAction(
               new OpenSearchBarAction(searchableBar:this),
               keyStroke,
               JComponent.WHEN_FOCUSED )
      }
   }
}

class OpenSearchBarAction extends AbstractAction {
   private SearchableBar searchableBar

   public void actionPerformed( ActionEvent e ){
      searchableBar.getInstaller().openSearchBar(searchableBar)
      searchableBar.focusSearchField()
  }
}