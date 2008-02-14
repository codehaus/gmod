/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.junctions.ui

import org.codehaus.groovy.junctions.ViewUtils

static handler = false
if (!handler) {
    try {
        handler = build("""
package org.codehaus.groovy.junctions.ui

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
   menu(text: 'Subscriptions', mnemonic: 'S') {
      menuItem(addSubscriptionAction)
      separator()
      menuItem(refreshSubscriptionsAction)
      menuItem(manageSubscriptionsAction)
      separator()
      //menuItem(previousSubscriptionAction)
      //menuItem(nextSubscriptionAction)
      menuItem(refreshSubscriptionAction)
      separator()
      menu(text: 'Stats from...', id: 'statsMenu',
         icon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/activity_window_16.png")) ) {
         menuItem( diggStatsAction )
         menuItem( dzoneStatsAction )
         menuItem( cosmosStatsAction )
      }
   }

   menu(text: 'Posts', mnemonic: 'P' ) {
      menuItem(previousPostAction)
      menuItem(nextPostAction)
      separator()
      menuItem(markAsFavoriteAction)
      separator()
      menuItem(markAllAsReadAction)
      menu(text: 'Send to...', id: 'bookmarksMenu',
         icon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/flag-16.png")) ) {
         menuItem(deliciousBookmarkAction)
         menuItem(diggBookmarkAction)
      }
   }

   menu(text: 'Tools', mnemonic: 'T' ){
      menuItem(preferencesAction)
   }
}
