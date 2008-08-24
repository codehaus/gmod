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

import com.jidesoft.dialog.StandardDialog
import org.kordamp.groovy.swing.jide.impl.DialogBannerPanel
import org.kordamp.groovy.swing.jide.impl.DialogContentPanel
import org.kordamp.groovy.swing.jide.impl.DialogButtonPanel
import org.kordamp.groovy.swing.jide.impl.DefaultStandardDialog

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class StandardDialogPaneFactory extends AbstractJideComponentFactory {
    public StandardDialogPaneFactory( Class paneClass ) {
       super( paneClass )
    }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
      if( parent instanceof DefaultStandardDialog ) {
         if( child instanceof DialogBannerPanel ) parent.bannerPanel = child
         if( child instanceof DialogContentPanel ) parent.contentPanel = child
         if( child instanceof DialogButtonPanel ) parent.buttonPanel = child
      }
   }
}