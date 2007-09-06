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
import java.awt.*
import java.security.*
import javax.swing.*
import javax.swing.border.*
import javax.swing.event.*
import javax.swing.text.DefaultStyledDocument
import org.jdesktop.swingx.JXTitledPanel
import org.jdesktop.swingx.border.*
import groovy.swing.SwingBuilder
import groovy.swing.j2d.*
import groovy.ui.ConsoleTextEditor

import org.codehaus.groovy.control.CompilationFailedException

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class Main {
    private def graphicsBuilder
    private def swing
    private def gsh = new GroovyShell()
    private def inputEditor
    private def runThread = null
    private def runWaitDialog
    private def frame
    private String codeBase

    public static void main(String[] args) {
       SwingUtilities.invokeLater {
          def app = new Main(args && args.length == 1 ? args[0]: null)
          app.run()
       }
    }

    Main( String codeBase ){
       this.codeBase = codeBase
       buildUI()
       setupGraphicsBuilder()
       Policy.setPolicy( new DemoPolicy() )
    }

    public void run(){
       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
       System.setProperty("apple.laf.useScreenMenuBar", "true")
       System.setProperty("com.apple.mrj.application.apple.menu.about.name", "GraphicsBuilderDemo")
       frame.visible = true
    }

    private void setupGraphicsBuilder(){
       graphicsBuilder = new GraphicsBuilder()
       Jdk6GraphicsBuilderHelper.registerOperations( graphicsBuilder )
       SwingXGraphicsBuilderHelper.registerOperations( graphicsBuilder )
       BatikGraphicsBuilderHelper.registerOperations( graphicsBuilder )
    }

    private void buildUI(){
       inputEditor = new ConsoleTextEditor()
       swing = new SwingBuilder()

       swing.actions {
          action(id: 'exitAction',
             name: 'Exit',
             closure: this.&exit,
             mnemonic: 'X'
          )
          action(inputEditor.undoAction,
             id: 'undoAction',
             name: 'Undo',
             mnemonic: 'U',
             accelerator: shortcut('Z')
          )
          action(inputEditor.redoAction,
             id: 'redoAction',
             name: 'Redo',
             closure: this.&redo,
             mnemonic: 'Y',
             accelerator: shortcut('Y')
          )
          action(id: 'cutAction',
             name: 'Cut',
             closure: this.&cut,
             mnemonic: 't',
             accelerator: shortcut('X')
          )
          action(id: 'copyAction',
             name: 'Copy',
             closure: this.&copy,
             mnemonic: 'C',
             accelerator: shortcut('C')
          )
          action(id: 'pasteAction',
             name: 'Paste',
             closure: this.&paste,
             mnemonic: 'P',
             accelerator: shortcut('V')
          )
          action(id: 'selectAllAction',
             name: 'Select All',
             closure: this.&selectAll,
             mnemonic: 'A',
             accelerator: shortcut('A')
          )
          action(id: 'runAction',
             name: 'Run',
             closure: this.&executeCode,
             mnemonic: 'R',
             keyStroke: 'ctrl ENTER',
             accelerator: shortcut('R')
          )
          action(id: 'largerFontAction',
             name: 'Larger Font',
             closure: this.&largerFont,
             mnemonic: 'L',
             accelerator: shortcut('shift L')
          )
          action(id: 'smallerFontAction',
             name: 'Smaller Font',
             closure: this.&smallerFont,
             mnemonic: 'S',
             accelerator: shortcut('shift S')
          )
          action(id: 'aboutAction',
             name: 'About',
             closure: this.&showAbout,
             mnemonic: 'A'
          )
          action(id: 'interruptAction',
             name: 'Interrupt',
             closure: this.&confirmRunInterrupt
          )
          action(id: 'clearAction',
             name: 'Clear',
             closure: this.&clearCode,
             mnemonic: 'L',
             accelerator: shortcut('L')
          )
       }

       frame = swing.frame( title: "GraphicsBuilder - Demo", size: [1024,800],
             locationRelativeTo: null ){
           menuBar {
              menu(text: 'File', mnemonic: 'F') {
                  menuItem(exitAction)
              }

              menu(text: 'Edit', mnemonic: 'E') {
                  menuItem(undoAction)
                  menuItem(redoAction)
                  separator()
                  menuItem(cutAction)
                  menuItem(copyAction)
                  menuItem(pasteAction)
                  separator()
                  menuItem(selectAllAction)
                  menuItem(clearAction)
              }

              menu(text: 'View', mnemonic: 'V') {
                 menuItem(largerFontAction)
                 menuItem(smallerFontAction)
             }

             menu(text: 'Script', mnemonic: 'S') {
                 menuItem(runAction)
             }

             menu(text: 'Help', mnemonic: 'H') {
                 menuItem(aboutAction)
             }
          }

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

       frame.windowClosing = this.&exit

       runWaitDialog = swing.dialog(title: 'Groovy executing',
             owner: frame,
             modal: true ) {
          vbox(border: BorderFactory.createEmptyBorder(6, 6, 6, 6)) {
             label(text: "Groovy is now executing. Please wait.", alignmentX: 0.5f)
             vstrut()
             button(interruptAction,
                 margin: new Insets(10, 20, 10, 20),
                 alignmentX: 0.5f
             )
          }
       }
    }

    private def buildListPanel( swing ){
       def data = ["Shapes","Painting","Transformations","Groups",
                   "Images","Areas","Binding","Swing","Miscellaneous"]
       swing.panel( new JXTitledPanel(), title: 'Topics', border: createShadowBorder() ){
          list( listData: data as Object[], mouseClicked: this.&displayDemo,
                cellRenderer: new OptionCellRenderer() )
       }
    }

    private def buildViewPanel( swing ){
       def graphicsPanel = new GraphicsPanel()
       graphicsPanel.border = BorderFactory.createEmptyBorder()
       graphicsPanel.background = Color.white
       graphicsPanel.addGraphicsErrorListener({ evt ->
           displayError( evt.cause.localizedMessage )
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
                widget( inputEditor, id: 'source', border: BorderFactory.createEmptyBorder(),
                          font: new Font( Font.MONOSPACED, Font.PLAIN, 14 ) ){
                   action(runAction)
                }
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

    void exit(EventObject evt = null) {
        frame.hide()
        frame.dispose()
    }

    void largerFont(EventObject evt = null) {
       if (inputEditor.textEditor.font.size > 40) return
       def newFont = new Font('Monospaced', Font.PLAIN, inputEditor.textEditor.font.size + 2)
       inputEditor.textEditor.font = newFont
    }

    void smallerFont(EventObject evt = null){
       if (inputEditor.textEditor.font.size < 5) return
       def newFont = new Font('Monospaced', Font.PLAIN, inputEditor.textEditor.font.size - 2)
       inputEditor.textEditor.font = newFont
    }

    void showAbout(EventObject evt = null) {
       def pane = swing.optionPane()
        // work around GROOVY-1048
       pane.setMessage('Welcome to the Groovy GraphicsBuilder Demo')
       def dialog = pane.createDialog(frame, 'About GraphicsBuilder - Demo')
       dialog.show()
    }

    void invokeTextAction(evt, closure) {
       def source = evt.getSource()
       if (source != null) {
           closure(inputEditor.textEditor)
       }
    }

    void cut(EventObject evt = null) {
       invokeTextAction(evt, { source -> source.cut() })
    }

    void copy(EventObject evt = null) {
       invokeTextAction(evt, { source -> source.copy() })
    }

    void paste(EventObject evt = null) {
       invokeTextAction(evt, { source -> source.paste() })
    }

    void selectAll(EventObject evt = null) {
       invokeTextAction(evt, { source -> source.selectAll() })
    }

    void displayDemo(EventObject evt = null) {
       def demo = evt.source.selectedValue
       swing.description.text = descriptionCache.get( demo, loadDescription(demo) )
       swing.description.caretPosition = 0
    }

    void onClickDemoOption(EventObject evt = null) {
       if( evt.eventType == HyperlinkEvent.EventType.ACTIVATED ){
          def demo = evt.description
          inputEditor.textEditor.text = sourceCache.get( demo, loadSource(demo) )
          inputEditor.textEditor.caretPosition = 0
          executeCode()
       }
    }

    // Confirm whether to interrupt the running thread
    void confirmRunInterrupt(EventObject evt) {
        def rc = JOptionPane.showConfirmDialog(frame, "Attempt to interrupt script?",
            "GroovyConsole", JOptionPane.YES_NO_OPTION)
        if (rc == JOptionPane.YES_OPTION && runThread != null) {
            runThread.interrupt()
        }
    }

    void executeCode( EventObject evt = null ){
        if( !inputEditor.textEditor.text.trim() ){
           displayError( "Please type some code" )
        }else{
           runThread = Thread.start {
              try {
                  SwingUtilities.invokeLater { showRunWaitDialog() }
                  swing.error.text = ""
                  swing.view.removeAll()
                  def script = """
                        import java.awt.*
                        import java.awt.geom.*
                        import org.jdesktop.swingx.geom.*
                        import groovy.swing.j2d.demo.BoundBean
                        import groovy.swing.j2d.operations.*
                        import groovy.swing.SwingBuilder

                        go = {
                           ${inputEditor.textEditor.text}
                        }"""
                   def go = graphicsBuilder.build( !codeBase ? gsh.evaluate(script) :
                      gsh.evaluate(script,"RestrictedScript",codeBase) )
                  if( go.operations.size() == 0 ){
                     throw new RuntimeException("An operation is not recognized. Please check the code.")
                  }
                  SwingUtilities.invokeLater { finishNormal(go) }
              } catch (Throwable t) {
                  SwingUtilities.invokeLater { finishException(t) }
              } finally {
                  SwingUtilities.invokeLater {
                      runWaitDialog.hide()
                      runThread = null
                  }
              }
           }
        }
    }

    def finishException(Throwable t) {
       t.printStackTrace()
       displayError( t.localizedMessage )
    }

    def finishNormal(Object go) {
       if( go instanceof GraphicsOperation ){
          swing.view.@graphicsOperation = null
          swing.view.graphicsOperation = go
       }
    }

    void showRunWaitDialog() {
       runWaitDialog.pack()
       int x = frame.x + (frame.width - runWaitDialog.width) / 2
       int y = frame.y + (frame.height - runWaitDialog.height) / 2
       runWaitDialog.setLocation(x, y)
       runWaitDialog.show()
    }

    void clearCode( EventObject evt = null ){
       inputEditor.textEditor.text = ""
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