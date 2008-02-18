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
import java.awt.BorderLayout as BL
import javax.swing.BorderFactory as BF
import javax.swing.SwingConstants as SC
import javax.swing.JOptionPane
import static org.jdesktop.swingx.JXTaskPane.EXPANDED_CHANGED_KEY

import java.beans.PropertyChangeListener
import javax.swing.tree.TreePath

class Main extends Binding {
    SwingXBuilder swing
    JideBuilder jide
    ObjectGraphBuilder nodeBuilder
    HttpHelper httpHelper = new HttpHelper()

    def feedMap = [:]
    def currentFeed
    def currentEntry
    def currentEntryPane
    def bookmarkMap = [:]
    def unclassifiedNode

    private static final SERVER_END_POINT = "http://localhost:8080/junctions-domain"

    public static void main(String[] args) {
        new Main().run()
    }

    Main() {
        init()
    }

    public void run() {
        loadData()
    }

    private void init() {
        initNodeBuilder()
        initUI()
    }

    private void initNodeBuilder() {
        nodeBuilder = new ObjectGraphBuilder()
        nodeBuilder.classNameResolver = {name ->
            "javax.swing.tree.DefaultMutableTreeNode"
        }
        nodeBuilder.newInstanceResolver = {klass, attributes ->
            def node = klass.newInstance()
            node.userObject = nodeBuilder.currentName
            return node
        }
        nodeBuilder.childPropertySetter = {parent, child, parentName, childName ->
            parent.add(child)
        }
    }

    private void initUI() {
        swing = new SwingXBuilder()
        swing.registerBeanFactory("postPane", PostPane)
        swing.registerLayouts()
        swing.lookAndFeel('system')
        swing.controller = this

        swing.doLater {
            // create the actions
            build(JunctionsActions)
            // create the view
            build(JunctionsView)
        }

        jide = new JideBuilder()
        jide.controller = this
        jide.doLater {
            jide.build(JunctionsView2)
        }
    }

    private def serverPost(url, data, parse = true) {
        return httpHelper.post("${SERVER_END_POINT}/$url", data, parse)
    }

    private def serverGet(url) {
        return httpHelper.get("${SERVER_END_POINT}/$url")
    }

    private loadData() {
        swing.doLater {waitDialog.visible = true}
        loadFeeds()
        loadBookmarks()
    }

    private loadFeeds() {
        swing.doOutside {
            def feeds = serverGet('feed/show')
            def folders = serverGet('folder/show')

            folders.folder.each {folder ->
                def folderName = folder.name as String
                def folderNode = nodeBuilder."$folderName"() {
                    folder.feeds?.feed.@id.each {feedId ->
                        def feed = feeds.feed.find {it.@id == feedId}
                        nodeBuilder."${feed.title}"()
                        serverGet("item/showFeed?id=$feedId")
                        feedMap[feed.title as String] = [
                                id: feedId as String,
                                url: feed.url as String,
                                link: feed.link as String,
                                title: feed.title as String,
                                entries: parseEntries(serverPost("feed/refresh", [id: feedId as String]))
                                ]
                    }
                }
                if (folderName == "unclassified") unclassifiedNode = folderNode
                swing.feedContainer.model.root.add(folderNode)
            }
            swing.doLater {
                feedContainer.expandAll()
                frame.repaint()
                waitDialog.visible = false
            }
        }
    }

    private loadBookmarks() {
        swing.doOutside {
            def bookmarks = serverGet('bookmark/show')

            bookmarks.bookmark.each {bookmark ->
                bookmarkMap[bookmark.@id as String] = bookmark.name as String
            }
        }
    }

    def showError(title, message) {
        swing.doLater {
            // hide wait dialog if visible
            waitDialog.visible = false
            // display error message
            JOptionPane.showMessageDialog(frame, message,
                    title, JOptionPane.ERROR_MESSAGE)
        }
    }

    def showMessage(title, message, hideWaitDialog = true) {
        swing.doLater {
            // hide wait dialog if visible and requested to
            if (hideWaitDialog) waitDialog.visible = false
            // display error message
            JOptionPane.showMessageDialog(frame, message,
                    title, JOptionPane.INFORMATION_MESSAGE)
        }
    }

    def showAlert(title, message, hideWaitDialog = true) {
        swing.doLater {
            // hide wait dialog if visible and requested to
            if (hideWaitDialog) waitDialog.visible = false
            // display error message
            JOptionPane.showMessageDialog(frame, message,
                    title, JOptionPane.WARNING_MESSAGE)
        }
    }

    // --------------

    void exit(EventObject evt = null) {
        System.exit(0)
    }

    void showAbout(EventObject evt = null) {
        aboutDialog.visible = true
    }

    void showAddSubscription(EventObject evt = null) {
        addSubscriptionDialog.visible = true
    }

    void addSubscription(feedUrl) {
        // processing is done outside EDT

        if (!feedUrl.startsWith("http://")) feedUrl = "http://$feedUrl"
        swing.doLater {waitDialog.visible = true}
        try {
            def response = serverPost("feed/add", [url: feedUrl])
            if (response?.code.text() != "ERROR") {
                currentFeed = response.title as String
                feedMap[currentFeed] = [
                        id: response.@id as String,
                        url: feedUrl,
                        link: response.link as String,
                        title: response.title as String,
                        entries: parseEntries(serverPost("feed/refresh", [id: response.@id as String]))
                        ]
                swing.doLater {
                    def feedNode = nodeBuilder."$currentFeed"()
                    swing.feedContainer.model.insertNodeInto(feedNode, unclassifiedNode, unclassifiedNode.childCount)
                    swing.feedContainer.expandPath(new TreePath(unclassifiedNode.getPath() as Object[]))
                    mainPanel.title = currentFeed
                    populatePostContainer(response.title as String)
                    frame.repaint()
                }
            } else {
                showError("Add Subscription", response.cause.text())
            }
        }
        finally {
            swing.doLater {waitDialog.visible = false}
        }
    }

    void manageSubscriptions(EventObject evt = null) {

    }

    void refreshSubscriptions(EventObject evt = null) {
        def answer = JOptionPane.showConfirmDialog(frame,
                "You are about to refresh all your subscriptions,\n" +
                        "this may take sometime time.\n\n" +
                        "Do you want to continue?",
                "Refresh Subscriptions",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)
        switch (answer) {
            case JOptionPane.CLOSED_OPTION:
            case JOptionPane.NO_OPTION:
                break
            case JOptionPane.YES_OPTION:
                swing.doOutside {
                    feedMap.each {key, feed ->
                        feedMap[key].entries = parseEntries(serverPost("feed/refresh",
                                        [id: feedMap[key].id]))
                        if (key == currentFeed) {
                            swing.doLater {populatePostContainer(currentFeed)}
                        }
                    }
                }
                break
        }
    }

    void refreshSubscription(EventObject evt = null) {
        swing.doOutside {
            if (currentFeed) {
                feedMap[currentFeed].entries = parseEntries(serverPost("feed/refresh",
                                [id: feedMap[currentFeed].id]))
                swing.doLater {
                    populatePostContainer(currentFeed)
                }
            }
        }
    }

    void nextPost(EventObject evt = null) {

    }

    void previousPost(EventObject evt = null) {

    }

    void markAllAsRead(EventObject evt = null) {
        // call is on EDT
        if (currentFeed) {
            swing.doOutside {
                feedMap[currentFeed].entries.each {key, entry ->
                    if (!entry.read) {
                        markEntryAsRead(entry)
                    }
                }
            }
        }
    }

    void markAsFavorite(EventObject evt = null) {

    }

    private def technoratiStatsHandler
    void subscriptionStatsFrom(EventObject evt = null, String serviceId) {
        if (!currentFeed) return
        // TODO refactor this in proper service handlers
        switch (serviceId) {
            case "Technorati":
                swing.doLater {waitDialog.visible = true}
                if (!technoratiStatsHandler) technoratiStatsHandler = new TechnoratyStatsHandler()
                technoratiStatsHandler.displayStats(this, feedMap[currentFeed])
                swing.doLater {waitDialog.visible = false}
        }
    }

    void bookmarkTo(EventObject evt = null, String serviceId) {
        // call is inside EDT

        if (!currentEntry) return
        if (!currentEntry.bookmarks.find {it == serviceId}) {
            swing.doLater {waitDialog.visible = true}
            swing.doOutside {
                def response = serverPost("item/bookmark", [id: currentEntry.id, service: serviceId])
                if (response?.code.text() != "ERROR") {
                    currentEntry.bookmarks << serviceId
                    swing.doLater {
                        panel(currentEntry.bookmarkPanel) {
                            label(icon: imageIcon(image: ViewUtils.icons[serviceId]))
                        }
                        frame.repaint()
                    }
                    showMessage("Bookmark To", "Successfuly bookmarked\n" +
                            "'${currentEntry.title}'\n" +
                            "to $serviceId")
                } else {
                    showError("Bookmark To", "Couldn't bookmark entry\n" +
                            "'${currentEntry.title}'\n" +
                            "to $serviceId\n\n" +
                            "cause: ${response.cause.text()}")
                }
            }
        } else {
            showAlert("Bookmark to", "'${currentEntry.title}'\n" +
                    "is already bookmarked to $serviceId")
        }
    }

    void showPreferences(EventObject evt = null) {

    }

    // -------

    def feedSelectionListener = {event ->
        def path = event.path
        if (path.pathCount == 3) {
            // clicked on a feed
            def feedName = path.lastPathComponent as String
            if (feedName == currentFeed) return
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

    private void populatePostContainer(feedName) {
        // processing is done inside EDT

        def w = (frame.size.width * 0.5) as int

        def entries = feedMap[feedName].entries
        if (entries) {
            swing.markAllAsReadAction.enabled = true
            swing.deliciousBookmarkAction.enabled = true
        }

        swing.postContainer.removeAll()
        currentEntry = null

        entries.each {url, entry ->
            def pane = makeEntryPane(entry, w)
            registerClickListener(pane, entry)
            swing.postContainer.add(pane)
        }
    }

    def makeEntryPane(entry, w) {
        def bookmarkButton = jide.jideSplitButton('Bookmarks', customize: {m ->
            m.removeAll()
            m.add(swing.deliciousBookmarkAction)
        })
        swing.postPane(title: entry.title, expanded: false,
                publishedDate: entry.publishedDate,
                url: entry.url,
                icon: swing.imageIcon(image: entry.read ? ViewUtils.icons.readEntryIcon : ViewUtils.icons.unreadEntryIcon)) {
            borderLayout(hgap: 0, vgap: 0)
            def sp = scrollPane(constraints: BL.CENTER, opaque: false) {
                editorPane(contentType: "text/html", text: entry.content,
                        editable: false, border: BF.createEmptyBorder(),
                        background: Color.LIGHT_GRAY.brighter(), constraints: BL.CENTER)

            }
            entry.bookmarkPanel = panel(constraints: BL.SOUTH, opaque: false) {
                widget(bookmarkButton, horizontalAlignment: SC.LEFT)
                entry.bookmarks.each {v ->
                    label(icon: imageIcon(image: ViewUtils.icons[v]))
                }
            }
            def h = sp.preferredSize.height as int
            h = h < 100 ? 100 : h
            sp.preferredSize = new Dimension(w, h)
        }
    }

    def registerClickListener(pane, entry) {
        entry.pane = pane
        pane.addPropertyChangeListener(EXPANDED_CHANGED_KEY, {event ->
            // do nothing if collpasing
            if (!event.newValue) return

            // pane is expanding
            if (event.newValue && currentEntryPane != event.source) {
                // need to hide previous entryPane if any
                def tmpPane = currentEntryPane
                currentEntryPane = event.source
                swing.doOutside {
                    sleep(240) // min wait time
                    tmpPane?.expanded = false
                }
            }
            currentEntry = entry
            swing.doOutside {markEntryAsRead(entry)}
        } as PropertyChangeListener)
    }

    /*
    swing.taskPaneContainer(swing.postContainer) {
        entries.each {url, entry ->
            def pp = postPane(title: entry.title, expanded: false,
                    publishedDate: entry.publishedDate,
                    url: entry.url,
                    icon: imageIcon(image: entry.read ? ViewUtils.icons.readEntryIcon : ViewUtils.icons.unreadEntryIcon)) {
                def sp = scrollPane {
                    panel {
                        borderLayout()
                        editorPane(contentType: "text/html", text: entry.content,
                                editable: false, border: BF.createEmptyBorder(),
                                background: Color.LIGHT_GRAY.brighter(), constraints: BL.CENTER)
                        panel(constraints: BL.SOUTH) {
                            widget(bookmarkButton)
                        }
                    }
                }
                def h = sp.preferredSize.height as int
                h = h < 100 ? 100 : h
                sp.preferredSize = new Dimension(w, h)
            }
            entry.pane = pp
            pp.addPropertyChangeListener(EXPANDED_CHANGED_KEY, {event ->
                // do nothing if collpasing
                if (!event.newValue) return

                // pane is expanding
                if (event.newValue && currentEntryPane != event.source) {
                    // need to hide previous entryPane if any
                    def tmpPane = currentEntryPane
                    currentEntryPane = event.source
                    swing.doOutside {
                        sleep(240) // min wait time
                        tmpPane?.expanded = false
                    }
                }
                currentEntry = entry
                swing.doOutside {markEntryAsRead(entry)}
            } as PropertyChangeListener)
        }
    }
    */


    private def parseEntries(items) {
        def entries = [:] as LinkedHashMap
        items?.item.each {item ->
            entries[item.url as String] = [
                    id: item.@id as String,
                    title: item.title as String,
                    author: item.author as String,
                    content: item.content as String,
                    publishedDate: item.publishedDate as String,
                    read: item.read.text().toBoolean(),
                    // TODO tags
                    bookmarks: []
                    ]
            if (item.bookmarks?.bookmark) {
                item.bookmarks?.bookmark.each {bookmark ->
                    entries[item.url as String].bookmarks << bookmarkMap[bookmark.@id as String]
                }
            }
        }
        return entries
    }

    private def markEntryAsRead(entry) {
        if (!entry.read) {
            serverPost("item/update", [id: entry.id, read: 'true'], false)
            entry.pane.icon = swing.imageIcon(image: ViewUtils.icons.readEntryIcon)
            entry.read = true
        }
    }
}
