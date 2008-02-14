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

import javax.swing.SwingUtilities
import groovy.swing.SwingXBuilder
import java.awt.Dimension
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.event.TreeSelectionListener
import org.kordamp.groovy.swing.jide.JideBuilder

import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader

class Main extends Binding {
   SwingXBuilder swing
   ObjectGraphBuilder nodeBuilder

   public static void main(String[] args) {
      new Main().run()
   }

   Main() {
      buildApp()
   }

   public void run() {
      loadFeeds()
   }

   private void buildApp() {
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

      swing = new SwingXBuilder()
      swing.lookAndFeel('system')
      swing.controller = this
      // create the actions
      swing.doLater {
         build(JunctionsActions)
         // create the view
         build(JunctionsView)
         unclassifiedNode = swing.feedContainer.model.root.lastChild
      }

      def jide = new JideBuilder()
      jide.controller = this
      jide.doLater {
         jide.build(JunctionsView2)
      }
   }

   private loadFeeds(){
      /*
      swing.doOutside {
         def root = seedFeeds()

         swing.doLater {
            swing.feedContainer.model = new DefaultTreeModel(root)
            swing.taskPaneContainer( swing.postContainer ){
               (1..5).each {
                  taskPane( title: "Post $it", expanded: false,
                    icon: imageIcon(image: Main.loadImage("zeusboxstudio-feedicons2/RSS_file_16.png")) )
               }
            }
         }
      }
      */
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
         processFeedUrl( feedUrl )
      }
      finally {
         swing.doLater { waitDialog.visible = false }
      }
   }

   private processFeedUrl( feedUrl ){
      // TODO provide feedback
      SyndFeedInput input = new SyndFeedInput()
      SyndFeed feed = input.build(new XmlReader(feedUrl.toURL()))
      // TODO check if subscription is new

      feedNode = nodeBuilder."${feed.title}"()
      feedMap[(feed.title)] = feed

      swing.doLater {
         unclassifiedNode.add( feedNode )
         def w = (frame.size.width*2/4) as int

         // TODO cache image
         def postIcon = ViewUtils.icons.postIcon
         swing.postContainer.removeAll()
         swing.taskPaneContainer( swing.postContainer ){
            feed.entries.each { entry ->
               taskPane( title: entry.title, expanded: false,
                         icon: imageIcon(image:postIcon) ){
                  def sp = scrollPane {
                     editorPane( contentType: "text/html", text: entry.description.value,
                                 editable: false )
                  }
                  sp.preferredSize = new Dimension(w,sp.preferredSize.height as int)
               }
            }
         }

         frame.repaint()
      }
   }

   def feedMap = [:]

   void manageSubscriptions( EventObject evt = null ) {

   }

   void refreshSubscriptions( EventObject evt = null ) {

   }

   void refreshSubscription( EventObject evt = null ) {

   }

   void nextPost( EventObject evt = null ) {

   }

   void previousPost( EventObject evt = null ) {

   }

   void markAllAsRead( EventObject evt = null ) {

   }

   void markAsFavorite( EventObject evt = null ) {

   }

   void subscriptionStatsFrom( EventObject evt = null, String serviceId ) {

   }

   void bookmarkTo( EventObject evt = null, String serviceId ) {

   }

   void showPreferences( EventObject evt = null ) {

   }

   // -------

   def feedSelectionListener = { event ->
      def path = event.path
      if( path.pathCount == 3 ){
         // clicked on a feed
         def feedName = path.lastPathComponent
         swing.refreshSubscriptionAction.enabled = true
         swing.mainPanel.title = feedName
      }else if( path.pathCount == 2 ){
         // cliked on a folder
      }
   } as TreeSelectionListener
}