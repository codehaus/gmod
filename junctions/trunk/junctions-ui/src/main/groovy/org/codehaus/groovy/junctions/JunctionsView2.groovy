/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.junctions

import java.awt.BorderLayout as BL

frame = controller.frame
dialog( title: 'About Junctions', id: 'aboutDialog', pack: true,
      owner: frame, size: [320,240], locationRelativeTo: frame ){
   borderLayout()
   bannerPanel(title: 'Junctions', subtitle: 'A Groovy RSS reader',
         constraints: BL.NORTH,
         titleIcon: imageIcon(ViewUtils.loadImage("zeusboxstudio-feedicons2/RSS_32.png")) )
   multilineLabel( "Sample application combining Swing & Grails" )
   buttonPanel( constraints: BL.SOUTH ) {
      button( 'Close', actionPerformed: { event -> aboutDialog.dispose() } )
   }
}

dialog( title: 'Add Subscription', id: 'addSubscriptionDialog',
      owner: frame, locationRelativeTo: frame, size: [320,150], pack:true ){
   borderLayout( hgap: 20, vgap: 20 )
   bannerPanel(title: 'Add Subscription', constraints: BL.NORTH,
         titleIcon: imageIcon(ViewUtils.loadImage("zeusboxstudio-feedicons2/subscribe_32.png")) )
   panel( constraints: BL.CENTER ) {
      gridLayout( cols: 1, rows: 3, hgap: 20 )
      label( "Paste a site or feed url" )
      textField( id: 'feedUrl', columns: 30 )
      label( "e.g., groovyblogs.org" )
   }
   buttonPanel( constraints: BL.SOUTH ) {
      button( 'Cancel', actionPerformed: { event ->
          feedUrl.text = ""
          addSubscriptionDialog.dispose()
      } )
      button( 'Add', actionPerformed: { event ->
         def url = feedUrl.text
         if( !url ) return
         feedUrl.text = ""
         addSubscriptionDialog.dispose()
         doOutside { controller.addSubscription( url ) }
      } )
   }
}

// link in references to the controller
controller.aboutDialog = aboutDialog
controller.addSubscriptionDialog = addSubscriptionDialog

// don't send any return value from the view, all items should be referenced via the bindings
return null