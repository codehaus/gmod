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
 */

package groovy.swing.j2d.app.view

static handler = false
if (!handler) {
    try {
        handler = build("""
package groovy.swing.j2d.app.view

import com.apple.mrj.*

class ConsoleMacOsSupport implements MRJQuitHandler, MRJAboutHandler {

	def quitHandler
	def aboutHandler

	public void handleAbout() {
		aboutHandler()
	}

	public void handleQuit() {
		quitHandler()
	}

}

def handler = new ConsoleMacOsSupport(quitHandler:controller.&exit, aboutHandler:controller.&showAbout)
MRJApplicationUtils.registerAboutHandler(handler)
MRJApplicationUtils.registerQuitHandler(handler)

return handler
""", new GroovyClassLoader(this.class.classLoader))
    } catch (Exception se) {
        // usually an AccessControlException, sometimes applets and JNLP won't let
        // you access MRJ classes.
        // However, in any exceptional case back out and use the BasicMenuBar
        se.printStackTrace()
        build(BasicMenuBar)
        return
    }
}

menuBar {
   menu(text: 'File', mnemonic: 'F') {
       menuItem(newFileAction)
       menuItem(newWindowAction)
       menuItem(openAction)
       separator()
       menuItem(saveAction)
       menuItem(saveAsAction)
       menuItem(exportAction)
   }

   menu(text: 'Edit', mnemonic: 'E') {
       menuItem(undoAction)
       menuItem(redoAction)
       separator()
       menuItem(cutAction)
       menuItem(copyAction)
       menuItem(pasteAction)
       separator()
       menuItem(findAction)
       menuItem(findNextAction)
       menuItem(findPreviousAction)
       menuItem(replaceAction)
       separator()
       menuItem(selectAllAction)
   }

   menu(text: 'View', mnemonic: 'V') {
      menuItem(largerFontAction)
      menuItem(smallerFontAction)
   }

   menu(text: 'Script', mnemonic: 'S') {
      menuItem(runAction)
   }
}