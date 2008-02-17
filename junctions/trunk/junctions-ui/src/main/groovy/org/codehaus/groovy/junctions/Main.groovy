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

package org.codehaus.groovy.junctions

import groovy.swing.SwingXBuilder
import java.awt.Color
import java.awt.Dimension
import javax.swing.event.TreeSelectionListener
import org.kordamp.groovy.swing.jide.JideBuilder
import org.codehaus.groovy.junctions.swingx.PostPane
import javax.swing.BorderFactory as BF
import javax.swing.JOptionPane
import static org.jdesktop.swingx.JXTaskPane.EXPANDED_CHANGED_KEY

import java.beans.PropertyChangeListener

class Main extends Binding {
   SwingXBuilder swing
   ObjectGraphBuilder nodeBuilder
   HttpHelper httpHelper = new HttpHelper()

   def feedMap = [:]
   def currentFeed
   def currentEntry

   private static final SERVER_END_POINT = "http://localhost:8080/junctions-domain"

   public static void main(String[] args) {
      new Main().run()
   }

   Main() {
      init()
   }

   public void run() {
      loadData()
      swing.doLater {
          frame.repaint()
      }
   }

   private void init() {
      initNodeBuilder()
      initUI()
   }

   private void initNodeBuilder(){
      nodeBuilder = new ObjectGraphBuilder()
      nodeBuilder.classNameResolver = { name ->
         "javax.swing.tree.DefaultMutableTreeNode"
      }
      nodeBuilder.newInstanceResolver = { klass, attributes ->
         def node = klass.newInstance()
         node.userObject = nodeBuilder.currentName
         return node
      }
      nodeBuilder.childPropertySetter = { parent, child, parentName, childName ->
         parent.add( child )
      }
   }

   private void initUI(){
      swing = new SwingXBuilder()
      swing.registerBeanFactory( "postPane", PostPane )
      swing.registerLayouts()
      swing.lookAndFeel('system')
      swing.controller = this

      swing.doLater {
         // create the actions 
         build(JunctionsActions)
         // create the view
         build(JunctionsView)
      }

      def jide = new JideBuilder()
      jide.controller = this
      jide.doLater {
         jide.build(JunctionsView2)
      }
   }

   private loadData(){
      swing.doOutside {
         def feeds = serverGet('feed/show')
         def folders = serverGet('folder/show')

         folders.folder.each { folder ->
             def folderNode = nodeBuilder."${folder.name}"()
             folder.feeds?.feed.@id.each { feedId ->
                def feed = feeds.feed.find{ it.@id == feedId }
                folderNode.add(nodeBuilder."${feed.title}"())
                serverGet("item/showFeed?id=$feedId")
                feedMap[feed.title as String] = [
                    id: feedId as String,
                    url: feed.url as String,
                    entries: parseEntries(serverPost("feed/refresh",[id:feedId as String]))
                ]
             }
             swing.doLater { feedContainer.model.root.add( folderNode ) }
         }
      }
   }

   // --------------

   void exit( EventObject evt = null ) {
      System.exit(0)
   }

   void showAbout( EventObject evt = null ) {
      aboutDialog.visible = true
   }

   void showAddSubscription( EventObject evt = null ) {
      addSubscriptionDialog.visible = true
   }

   void addSubscription( feedUrl ){
      // processing is done outside EDT

      if( !feedUrl.startsWith("http://") ) feedUrl = "http://$feedUrl"
      swing.doLater { waitDialog.visible = true }
      try{
            def response = serverPost("feed/add",[url:feedUrl])
            if( response?.code.text() != "ERROR" ){
               currentFeed = response.title as String
               feedMap[currentFeed] = [
                  id: response.@id as String,
                  url: feedUrl,
                  entries: parseEntries(serverPost("feed/refresh",[id:response.@id as String]))
               ]
               swing.doLater {
                  mainPanel.title = currentFeed
                  populatePostContainer( response.title as String )
               }
            }else{
                JOptionPane.showMessageDialog( frame, response.cause.text(),
                        "Add Subscription", JOptionPane.ERROR_MESSAGE )
            }
      }
      finally {
         swing.doLater { waitDialog.visible = false }
      }
   }
   
    private def serverPost(url, data, parse = true) {
        return httpHelper.post("${SERVER_END_POINT}/$url", data, parse )
    }

    private def serverGet( url ){
        return httpHelper.get("${SERVER_END_POINT}/$url")
    }

    void manageSubscriptions(EventObject evt = null) {

    }

    void refreshSubscriptions(EventObject evt = null) {
       def answer = JOptionPane.showConfirmDialog(frame,
            "You are about to refresh all your subscriptions,\n"+
            "this may take sometime time.\n\n"+
            "Do you want to continue?",
            "Refresh Subscriptions",
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)
       switch( answer ){
          case JOptionPane.CLOSED_OPTION:
          case JOptionPane.NO_OPTION:
             break
          case JOptionPane.YES_OPTION:
             swing.doOutside {
                 feedMap.each { key, feed ->
                     feedMap[key].entries = parseEntries(serverPost("feed/refresh",
                              [id:feedMap[key].id] ))
                     if( key == currentFeed ){
                         swing.doLater { populatePostContainer( currentFeed ) }
                     }
                 }
             }
             break
       }
    }

    void refreshSubscription(EventObject evt = null) {
        swing.doOutside {
           if( currentFeed ){
              feedMap[currentFeed].entries = parseEntries(serverPost("feed/refresh",
                              [id:feedMap[currentFeed].id] ))
              swing.doLater {
                 populatePostContainer( currentFeed )
              }
           }
        }
    }

    void nextPost(EventObject evt = null) {

    }

    void previousPost(EventObject evt = null) {

    }

    void markAllAsRead(EventObject evt = null) {

    }

    void markAsFavorite(EventObject evt = null) {

    }

    void subscriptionStatsFrom(EventObject evt = null, String serviceId) {

    }

    void bookmarkTo(EventObject evt = null, String serviceId) {

    }

    void showPreferences(EventObject evt = null) {

    }

    // -------

    def feedSelectionListener = {event ->
        def path = event.path
        if (path.pathCount == 3) {
            // clicked on a feed
            def feedName = path.lastPathComponent as String
            if( feedName == currentFeed ) return
            currentFeed = feedName
            swing.doLater() {
                refreshSubscriptionAction.enabled = true
                mainPanel.title = currentFeed
                populatePostContainer(currentFeed)
            	frame.repaint()
            }
        } else if (path.pathCount == 2) {
            // cliked on a folder
        }
    } as TreeSelectionListener

    private void populatePostContainer( feedName ) {
        // processing is done inside EDT

        def w = (frame.size.width * 0.5) as int

        def entries = feedMap[feedName].entries
		swing.postContainer.removeAll()
		currentEntry = null

		swing.taskPaneContainer( swing.postContainer ){
           entries.each { url, entry ->
              def pp = postPane( title: entry.title, expanded: false,
                        publishedDate: entry.publishedDate,
                        url: entry.url,
                        icon: imageIcon(image: entry.read ? ViewUtils.icons.readEntryIcon : ViewUtils.icons.unreadEntryIcon)){
                 def sp = scrollPane {
				    editorPane( contentType: "text/html", text: entry.content,
					            editable: false, border: BF.createEmptyBorder(),
					            background: Color.LIGHT_GRAY )
                 }
                 def h = sp.preferredSize.height as int
                 h = h < 100 ? 100 : h
                 sp.preferredSize = new Dimension(w,h)
              }
              pp.addPropertyChangeListener( EXPANDED_CHANGED_KEY, {
                  currentEntry = url
                  if( !entry.read ){
                      swing.doOutside {
                          serverPost("item/update",[id:entry.id,read:'true'],false)
                      }
                      pp.icon = imageIcon(image:ViewUtils.icons.readEntryIcon)
                      entry.read = true
                  }
              } as PropertyChangeListener)
           }
        }
	}

	private def parseEntries( items ){
        def entries = [:] as LinkedHashMap
		items?.item.each { item ->
		   entries[item.url as String] = [
              id: item.@id as String,
              title: item.title as String,
              author: item.author as String,
              content: item.content as String,
              publishedDate: item.publishedDate as String,
              read: item.read.text().toBoolean(),
              // tags
           ]
        }
        return entries
    }
}
