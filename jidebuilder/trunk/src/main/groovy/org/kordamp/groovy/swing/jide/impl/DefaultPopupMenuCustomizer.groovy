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

package org.kordamp.groovy.swing.jide.impl

import groovy.lang.Closure

import javax.swing.JPopupMenu
import com.jidesoft.swing.JideMenu

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class DefaultPopupMenuCustomizer implements JideMenu.PopupMenuCustomizer {
   Closure closure

   public void customize( JPopupMenu menu ){
      if( closure == null ){
         throw new NullPointerException("No closure has been configured for this PopupMenuCustomizer")
      }
      closure.call( menu )
   }
}