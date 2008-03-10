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

import groovy.ui.ConsoleTextEditor
import java.awt.*
import javax.swing.text.Style
import javax.swing.text.StyleContext
import javax.swing.text.StyledDocument

import java.awt.BorderLayout as BL
import java.awt.event.*
import javax.swing.*
import javax.swing.BorderFactory as BF
import javax.swing.border.*
import javax.swing.event.*
import static javax.swing.JSplitPane.VERTICAL_SPLIT
import groovy.swing.j2d.GraphicsPanel
import groovy.swing.j2d.event.*
import groovy.swing.j2d.app.ScrollPaneRuler

rowHeader = new ScrollPaneRuler(ScrollPaneRuler.VERTICAL)
columnHeader = new ScrollPaneRuler(ScrollPaneRuler.HORIZONTAL)

splitPane(id: 'splitPane', resizeWeight: 0.50F,
      orientation: VERTICAL_SPLIT) {
   scrollPane( border: BF.createLineBorder(Color.BLACK), id: 'viewScroller',
               constraints:BorderLayout.CENTER,
               rowHeaderView: rowHeader,
               columnHeaderView: columnHeader ){
      panel( new GraphicsPanel(), id: 'view', border: BF.createEmptyBorder(),
            background: Color.white )
   }
   panel {
      borderLayout( )
      scrollPane( constraints: BL.CENTER,
                  border: BF.createTitledBorder(BF.createLineBorder(Color.BLACK), "Source") ) {
         container( new ConsoleTextEditor(), id: 'inputEditor', border: BF.createEmptyBorder(),
                   font: new Font( Font.MONOSPACED, Font.PLAIN, 14 ) ){
            action(runAction)
         }
      }
      scrollPane( constraints: BL.SOUTH, border: BF.createTitledBorder(BF.createLineBorder(Color.BLACK), "Errors") ) {
         textArea( id: 'error',  rows: 2 )
      }
   }
}

view.addGraphicsErrorListener({ evt ->
   controller.displayError( evt.cause.localizedMessage )
} as GraphicsErrorListener )

Toolkit toolkit = Toolkit.getDefaultToolkit()
Dimension screen = toolkit.getScreenSize()
rowHeader.opaque = true
rowHeader.preferredSize = [20,screen.width as int]
columnHeader.opaque = true
columnHeader.preferredSize = [screen.height as int,20]

view.addMouseListener( rowHeader )
view.addMouseMotionListener( rowHeader )
view.addMouseListener( columnHeader )
view.addMouseMotionListener( columnHeader )

inputArea = inputEditor.textEditor
// attach ctrl-enter to input area
// need to wrap in actions to keep it from being added as a component
actions {
    container(inputArea, font:new Font("Monospaced", Font.PLAIN, 12), border:emptyBorder(4)) {
        action(runAction)
    }
}

Style defStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

def applyStyle = {Style style, values -> values.each{k, v -> style.addAttribute(k, v)}}

// redo styles for editor
doc = inputArea.getStyledDocument()
StyleContext styleContext = StyleContext.getDefaultStyleContext()
styles.each {styleName, defs ->
    Style style = styleContext.getStyle(styleName)
    if (style) {
        applyStyle(style, defs)
    }
}