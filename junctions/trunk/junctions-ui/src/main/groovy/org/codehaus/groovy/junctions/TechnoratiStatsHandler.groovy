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

import java.awt.BorderLayout as BL
import javax.swing.BorderFactory
import org.kordamp.groovy.swing.jide.JideBuilder
import com.atticlabs.zonelayout.swing.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.gui.*
import ca.odell.glazedlists.swing.*

class TechnoratyStatsHandler {
    JideBuilder jide
    HttpHelper http
    EventList linksList

    def panel
    def dialog

    private static final String API_KEY = "5130a7c7e3cc4ec657132cd7f068f5f4"
    private static final String COSMOS_URL = "http://api.technorati.com/cosmos"

    public TechnoratyStatsHandler() {
        jide = new JideBuilder()
        http = new HttpHelper()
        linksList = new SortedList(new BasicEventList(), {a, b -> b.name <=> a.name} as Comparator)

        // ZoneLayout info @ http://zonerlayout.com
        def blogInfoLayout = ZoneLayoutFactory.newZoneLayout()
        blogInfoLayout.addRow("u>u2r......-~...r")
        blogInfoLayout.addRow("6................", "infoRow")
        blogInfoLayout.addRow("a>a2b-~b3c>c2d-~d", "infoRow")

        def blogLinksLayout = ZoneLayoutFactory.newZoneLayout()
        blogLinksLayout.addRow("t+*t")

        panel = jide.panel {
            borderLayout(hgap: 20, vgap: 20)
            bannerPanel(title: 'Statistics for', constraints: BL.NORTH, id: 'banner',
                    titleIcon: imageIcon(resource: "images/technorati-powered.png", class: this))
            panel(constraints: BL.CENTER) {
                borderLayout()
                panel(id: 'blogInfo', layout: blogInfoLayout, constraints: BL.NORTH,
                        border: BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("General Info"),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5))) {
                    label('Url:', constraints: 'u')
                    textField(id: 'url', editable: false, constraints: 'r')
                    blogInfoLayout.insertTemplate("infoRow")
                    label('Inbound blogs:', constraints: 'a')
                    textField(id: 'inblogs', editable: false, constraints: 'b')
                    label('Rank:', constraints: 'c')
                    textField(id: 'rank', editable: false, constraints: 'd')
                    blogInfoLayout.insertTemplate("infoRow")
                    label('Inbound links:', constraints: 'a')
                    textField(id: 'inlinks', editable: false, constraints: 'b')
                    label('Last updated:', constraints: 'c')
                    textField(id: 'updated', editable: false, constraints: 'd')
                }
                panel(id: 'blogLinks', layout: blogLinksLayout, constraints: BL.CENTER,
                        border: BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Top links"),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5))) {
                    scrollPane(constraints: 't') {
                        table(id: 'links', model: createTableModel())
                        def tableSorter = new TableComparatorChooser(jide.links,
                                linksList, AbstractTableComparatorChooser.SINGLE_COLUMN)
                    }
                }
            }
            buttonPanel(constraints: BL.SOUTH) {
                button('Close', actionPerformed: {event ->
                    dialog.dispose()
                })
            }
        }
    }

    void displayStats(junctions, feed) {
        if (dialog == null) {
            dialog = jide.dialog(title: 'Stats from Techorati',
                    owner: junctions.frame,
                    locationRelativeTo: junctions.frame,
                    size: [600, 450], pack: false) {
                panel(panel)
            }
        }

        jide.doOutside {
            def cosmos = http.post(COSMOS_URL, [key: API_KEY, url: feed.link, limit: 20])

            if (!cosmos) {
                // TODO oops! big trouble making the call
            } else if (cosmos.document?.result?.error?.text()?.size() > 0) {
                // cosmos retured an error
                junctions.showError("Stats from Techorati", cosmos.document.result.error.text())
            } else {
                // cosmos returned info we can use
                def root = cosmos.document.result
                jide.url.text = feed.link

                // try first inside <weblog>
                jide.inblogs.text = root.weblog?.inboundblogs?.text() ?: 0
                jide.inlinks.text = root.weblog?.inboundlinks?.text() ?: 0
                jide.updated.text = root.weblog?.lastupdate?.text() ?: ''
                jide.rank.text = root.weblog?.rank?.text() ?: 0

                // try next outside <weblog>
                if (jide.inblogs.text == "0")
                    jide.inblogs.text = root?.inboundblogs?.text() ?: 0
                if (jide.inlinks.text == "0")
                    jide.inlinks.text = root?.inboundlinks?.text() ?: 0
                if (jide.rank.text == "0")
                    jide.rank.text = root.weblog?.rankingstart?.text() ?: 0

                linksList.clear()
                def list = []
                def linkNames = []
                cosmos.document.item?.each {link ->
                    def name = link?.weblog?.name?.text()?.trim()
                    if (!linkNames.find {it == name}) {
                        linkNames << name
                        list << [
                                name: name,
                                url: link?.weblog?.url?.text()?.trim() ?: "<nourl>",
                                blogs: link?.weblog?.inboundblogs?.text()?.toInteger() ?: 0,
                                links: link?.weblog?.inboundlinks?.text()?.toInteger() ?: 0
                                ]
                    }
                }

                jide.doLater {
                    linksList.addAll(list)
                    jide.banner.subtitle = feed.title
                    dialog.visible = true
                }
            }
        }
    }

    private def createTableModel() {
        def columnNames = ["Name", "Url", "Blogs", "Links"]
        return new EventTableModel(linksList, [
                getColumnCount: {4},
                getColumnName: {index -> columnNames[index]},
                getColumnValue: {object, index ->
                    object."${columnNames[index].toLowerCase()}"
                }] as TableFormat)
    }
}