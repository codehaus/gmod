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
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.event.TreeSelectionListener
import org.kordamp.groovy.swing.jide.JideBuilder
import org.codehaus.groovy.junctions.swingx.PostPane
import javax.swing.BorderFactory as BF

import org.apache.commons.httpclient.*
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.*
import java.beans.PropertyChangeListener

class Main extends Binding {
   SwingXBuilder swing
   ObjectGraphBuilder nodeBuilder
   HttpClient httpClient
   def feedMap = [:] as ObservableMap

   private static final SERVER_END_POINT = "http://localhost:8080/junctions-domain"

   public static void main(String[] args) {
      new Main().run()
   }

   Main() {
      init()
   }

   public void run() {
      //swing.doLater { waitDialog.visible = true }
      loadData()
      swing.doLater {
          //waitDialog.visible = false
          frame.repaint()
      }
   }

   private void init() {
      initNodeBuilder()
      initUI()

      httpClient = new HttpClient()

      /*
      feedMap.addPropertyChangeListener({event->
         if( !oldValue ){
             // a new feed has been added to the map
         }
      } as PropertyChangeListener)
      */
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
         def feeds = httpGet('feed/show')
         def folders = httpGet('folder/show')

         folders.folder.each { folder ->
             def folderNode = nodeBuilder."${folder.name}"()
             folder.feeds?.feed.@id.each { feedId ->
                def feed = feeds.feed.find{ it.@id == feedId }
                folderNode.add(nodeBuilder."${feed.title}"())
                httpGet("item/showFeed?id=$feedId")
                // update feed entries using another thread
                swing.doOutside {
                   feedMap[feed.title as String] = httpPost("feed/refresh",[id:feedId as String])
                }
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
      if( !feedUrl.startsWith("http://") ) feedUrl = "http://$feedUrl"

      swing.doLater { waitDialog.visible = true }
      try{
            //records = httpPost("http://localhost:8080/Junctions/feed/create",
            processFeedUrl( records )
      }
      finally {
         swing.doLater { waitDialog.visible = false }
      }
   }
   
   private def httpPost(url, data) {
      def postData = []
      for (pair in data.keySet()) {
         def nameValuePair = new NameValuePair(pair,data[pair])
         postData += nameValuePair
      }
      try {
         def post = new PostMethod("${SERVER_END_POINT}/$url")
         post.setRequestBody(postData as NameValuePair[])
         httpClient.executeMethod(post)
         def result = post.getResponseBodyAsString().toString()
         return  new XmlSlurper().parseText(result)
      } catch (Exception e) {
         e.printStackTrace()
      }
   }

   private def httpGet( url ){
        def data = "${SERVER_END_POINT}/$url".toURL().text
        return new XmlSlurper().parseText(data)
   }

    private processFeedUrl(feed) {
        /*
        // TODO provide feedback
        //SyndFeedInput input = new SyndFeedInput()
        //SyndFeed feed = input.build(new XmlReader(feedUrl.toURL()))
        // TODO check if subscription is new

        feedNode = nodeBuilder."${feed.title}"()

        swing.doLater {
            unclassifiedNode.add(feedNode)
            
         
         def data = "http://localhost:8080/Junctions/item/showFeed/${feed.@id.text()}".toURL().getText()
         def entries = new XmlSlurper().parseText(data)
         feedMap.put(feed.title, entries)
         println feed.title
         println feedMap[feed.title]
         populatePostContainer(entries)

            frame.repaint()
        }
        */
    }

    void manageSubscriptions(EventObject evt = null) {

    }

    void refreshSubscriptions(EventObject evt = null) {

    }

    void refreshSubscription(EventObject evt = null) {
        /*
        def node = (DefaultMutableTreeNode)swing.feedContainer.getLastSelectedPathComponent()
		if (node == null)
    //Nothing is selected.	
    		return;

    def feedTitle = node?.getUserObject()
    println feedTitle
    	 feedId = httpPost("http://localhost:8080/Junctions/feed/findFeedByTitle",
    		[title:feedTitle])

            println feedId
            //
            // Retrieve items and update view
            //
            
    		// remove feed and re-add or just
    		// delete all PostPanes, you decide
    	*/
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
            swing.refreshSubscriptionAction.enabled = true
            swing.mainPanel.title = feedName
            swing.doLater() {
            	populatePostContainer(feedName)
            	frame.repaint()
            }
        } else if (path.pathCount == 2) {
            // cliked on a folder
        }
    } as TreeSelectionListener

    private void populatePostContainer( feedName ) {
		def w = (frame.size.width * 0.5 ) as int
		// TODO cache image
        def postIcon = ViewUtils.icons.postIcon

        def entries = feedMap[feedName]
		swing.postContainer.removeAll()
		swing.taskPaneContainer( swing.postContainer ){			
		   entries.item.each { entry ->
              postPane( title: entry.title, expanded: false,
                        publishedDate: entry.publishedDate.text(),
                        url: entry.url.text(),
                        icon: imageIcon(image:postIcon)){
                 def sp = scrollPane {
				    editorPane( contentType: "text/html", text: entry.content,
					            editable: false, border: BF.createEmptyBorder(),
					            background: Color.LIGHT_GRAY.brighter().brighter() )
                 }
                 def h = sp.preferredSize.height as int
                 h = h < 100 ? 100 : h
                 sp.preferredSize = new Dimension(w,h)
              }
           }
        }
	}
}
