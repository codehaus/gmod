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
import java.awt.BorderLayout as BL
import java.awt.Dimension
import javax.swing.tree.*
import java.awt.ComponentOrientation as CO
import javax.swing.BorderFactory as BF
import javax.swing.ScrollPaneConstants as SPC
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT

splitPane(id: 'splitPane', resizeWeight: 0.50F,
        orientation: HORIZONTAL_SPLIT) {
    titledPanel(title: 'Subscriptions', constraints: BL.WEST, id: 'subscriptionsPanel') {
        panel {
            borderLayout()
            panel(constraints: BL.NORTH) {
                gridLayout(cols: 1, rows: 1)
                button(classicSwing: true, action: addSubscriptionAction)
            }
            sp = scrollPane(constraints: BL.CENTER,
                    verticalScrollBarPolicy: SPC.VERTICAL_SCROLLBAR_ALWAYS,
                    componentOrientation: CO.RIGHT_TO_LEFT) {
                tree(id: 'feedContainer',
                        closedIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/folder_16.png")),
                        openIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/folder_rss_16.png")),
                        leafIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/RSS_16.png"))
                        )
            }
            sp.preferredSize = new Dimension(200, sp.preferredSize.height as int)
            panel(constraints: BL.SOUTH) {
                gridLayout(cols: 1, rows: 2)
                button(classicSwing: true, action: refreshSubscriptionsAction)
                button(classicSwing: true, action: manageSubscriptionsAction)
            }
        }
    }
    titledPanel(title: 'Click on a Subscription or add a new one', constraints: BL.CENTER, id: 'mainPanel') {
        scrollPane(verticalScrollBarPolicy: SPC.VERTICAL_SCROLLBAR_ALWAYS,
                border: BF.createEmptyBorder()) {
            taskPaneContainer(id: 'postContainer', border: BF.createEmptyBorder()) {
                verticalLayout(gap: 2)
            }
        }
    }
}

def size = subscriptionsPanel.preferredSize
size.width = (frame.size.width * 1 / 4)
subscriptionsPanel.preferredSize = size
size = mainPanel.preferredSize
size.width = (frame.size.width * 3 / 4)
mainPanel.preferredSize = size

def root = new DefaultMutableTreeNode("Subscriptions")
feedContainer.model = new DefaultTreeModel(root)
feedContainer.addTreeSelectionListener(controller.feedSelectionListener)