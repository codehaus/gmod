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

package groovy.swing.j2d.demo

import java.awt.BorderLayout as BL
import java.awt.Color
import java.awt.Component
import java.awt.Font
import javax.swing.*
import javax.swing.border.*
import javax.swing.event.*
import org.jdesktop.swingx.JXTitledPanel
import org.jdesktop.swingx.border.*
import groovy.swing.SwingBuilder
import groovy.swing.j2d.*

import org.codehaus.groovy.control.CompilationFailedException

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class Main {
    private def frame
    private def graphicsBuilder
    private def swing
    private def gsh = new GroovyShell()

    Main(){
       buildUI()
       setupGraphicsBuilder()
    }

    public void setVisible( boolean visible ){
       frame.visible = visible
    }

    public static void main(String[] args) {
       SwingUtilities.invokeLater {
          def app = new Main()
          app.setVisible( true )
       }
    }

    private void setupGraphicsBuilder(){
       graphicsBuilder = new GraphicsBuilder()
       Jdk6GraphicsBuilderHelper.registerOperations( graphicsBuilder )
       SwingXGraphicsBuilderHelper.registerOperations( graphicsBuilder )
    }

    private void buildUI(){
       swing = new SwingBuilder()
       frame = swing.frame( title: "GraphicsBuilder - Demo", size: [1024,800],
             locationRelativeTo: null, defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE ){
          panel( border: BorderFactory.createEmptyBorder(5, 5, 5, 5) ){
             borderLayout()
             widget( buildListPanel(swing), constraints: BL.WEST )
             panel( constraints: BL.CENTER ){
                gridLayout( cols: 1, rows: 3 )
                widget( buildViewPanel(swing) )
                widget( buildCodePanel(swing) )
                widget( buildTextPanel(swing) )
             }
          }
       }
    }

    private def buildListPanel( swing ){
       def data = ["Shapes","Painting","Transformations","Groups","Images","Areas","Swing"]
       swing.panel( new JXTitledPanel(), title: 'Topics', border: createShadowBorder() ){
          list( listData: data as Object[], mouseClicked: this.&displayDemo,
                cellRenderer: new OptionCellRenderer() )
       }
    }

    private def buildViewPanel( swing ){
       def graphicsPanel = new GraphicsPanel()
       graphicsPanel.border = BorderFactory.createEmptyBorder()
       graphicsPanel.background = Color.white
       graphicsPanel.addGraphicsErrorListener({ event ->
           displayError( event.cause.localizedMessage )
       } as GraphicsErrorListener )

       swing.panel( new JXTitledPanel(), title: 'View', border: createShadowBorder() ){
          scrollPane( border: BorderFactory.createEmptyBorder() ) {
             widget( graphicsPanel, id: 'view' )
          }
       }
    }

    private def buildCodePanel( swing ){
       def sourcePanel = swing.panel( new JXTitledPanel(), title: 'Source - Type your own code!',
          border: createShadowBorder() ){
          panel {
             borderLayout()
             scrollPane( constraints: BL.CENTER, border: BorderFactory.createEmptyBorder() ) {
                textArea( id: 'source', border: BorderFactory.createEmptyBorder(),
                          font: new Font( Font.MONOSPACED, Font.PLAIN, 14 ) )
             }
             panel( constraints: BL.SOUTH ){
                borderLayout()
                button( constraints: BL.WEST, label: 'Clear', icon: Main.getIcon('clear'),
                        actionPerformed: this.&clearCode )
                scrollPane( constraints: BL.CENTER ) {
                   textArea( id: 'error', border: BorderFactory.createEmptyBorder(), rows: 2 )
                }
                button( constraints: BL.EAST, label: 'Eval', icon: Main.getIcon('eval'),
                        actionPerformed: this.&executeCode )
             }
          }
       }

       return sourcePanel
    }

    private def buildTextPanel( swing ){
       swing.panel( new JXTitledPanel(), title: 'Description', border: createShadowBorder() ){
          scrollPane( horizontalScrollBarPolicy: ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
                border: BorderFactory.createEmptyBorder() ) {
             editorPane( id: 'description', border: BorderFactory.createEmptyBorder(),
                   editable: false, background: Color.white, contentType: 'text/html',
                   hyperlinkUpdate: this.&onClickDemoOption )
          }
       }
    }

    private def createShadowBorder() {
       BorderFactory.createCompoundBorder( BorderFactory.createCompoundBorder(
             BorderFactory.createEmptyBorder(5, 5, 5, 5),
             new DropShadowBorder() ),
          BorderFactory.createLineBorder(Color.black, 1), )
    }

    private def descriptionCache = [:]
    private def sourceCache = [:]
    private static def iconCache = [:]
    private static def translateMap = [
        "clear":"16x16/actions/view-refresh",
        "eval":"16x16/actions/go-next",
        "option":"16x16/categories/applications-system",
    ]

    private def loadDescription( demo ){
       Thread.currentThread().contextClassLoader.getResourceAsStream("groovy/swing/j2d/demo/${demo}.html").text
    }

    private def loadSource( demo ){
       Thread.currentThread().contextClassLoader.getResourceAsStream("groovy/swing/j2d/demo/source-${demo}.txt").text
    }

    private static ImageIcon getIcon( name ){
       Icon icon = iconCache[name]
       if( !icon ){
          String fileName = "org/tango-project/tango-icon-theme/${translateMap[name]}.png"
          def url = Thread.currentThread().contextClassLoader.getResource( fileName )
          icon = new ImageIcon( url )
          iconCache[name] = icon
       }
       return icon
    }

    // ---------- ACTIONS -----------

    private def displayDemo = { event ->
       def demo = event.source.selectedValue
       swing.description.text = descriptionCache.get( demo, loadDescription(demo) )
       swing.description.caretPosition = 0
    }

    private def onClickDemoOption = { event ->
       if( event.eventType == HyperlinkEvent.EventType.ACTIVATED ){
          def demo = event.description
          swing.source.text = sourceCache.get( demo, loadSource(demo) )
          swing.source.caretPosition = 0
          executeCode()
       }
    }

    private def executeCode = {
        if( !swing.source.text.trim() ){
           displayError( "Please type some code" )
        }else{
           swing.error.text = ""
           try {
              def go = graphicsBuilder.build(gsh.evaluate("""
              import java.awt.*
              import java.awt.geom.*
              import org.jdesktop.swingx.geom.*

              go = {
                 ${swing.source.text}
              }"""))
              if( go.operations.size() == 0 ){
                 throw new RuntimeException("An operation is not recognized. Please check the code.")
              }
              swing.view.graphicsOperation = go
           }catch( Exception e ){
              displayError( e.localizedMessage )
           }
        }
    }

    private def clearCode = {
       swing.source.text = ""
       swing.error.text = ""
    }

    private def displayError = { text ->
       swing.error.text = text
       swing.error.caretPosition = 0
    }
}

class OptionCellRenderer extends DefaultListCellRenderer {
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
      Component renderer = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus)
      renderer.setIcon( Main.getIcon('option') )
      return renderer
   }
}