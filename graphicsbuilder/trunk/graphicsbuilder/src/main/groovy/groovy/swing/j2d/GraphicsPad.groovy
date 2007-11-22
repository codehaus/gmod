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

package groovy.swing.j2d

import java.awt.BorderLayout as BL
import java.awt.*
import javax.swing.*
import javax.swing.BorderFactory as BF
import javax.swing.border.*
import javax.swing.event.*
import javax.swing.text.DefaultStyledDocument
import groovy.swing.SwingBuilder
import groovy.swing.j2d.*
import groovy.ui.ConsoleTextEditor

import org.codehaus.groovy.control.CompilationFailedException

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsPad {
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
          def app = new GraphicsPad()
          app.run()
       }
    }

    GraphicsPad( String codeBase ){
       buildUI()
       setupGraphicsBuilder()
    }

    public void run(){
       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
       System.setProperty("apple.laf.useScreenMenuBar", "true")
       System.setProperty("com.apple.mrj.application.apple.menu.about.name", "GraphicsPad")
       frame.visible = true
    }

    private void setupGraphicsBuilder(){
       graphicsBuilder = new GraphicsBuilder()
       def helpers = ["Jdk6GraphicsBuilderHelper",
                      "SwingXGraphicsBuilderHelper",
                      "BatikGraphicsBuilderHelper"]
       helpers.each { helper ->
           try{
              Class helperClass = Class.forName("groovy.swing.j2d.${helper}")
              helperClass."registerOperations"( graphicsBuilder )
           }catch( Exception e ){
               System.err.println("Couldn't register ${helper}")
           }
       }
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

       frame = swing.frame( title: "GraphicsPad", size: [1024,800],
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

          gridLayout( cols: 1, rows: 2 )
          widget( buildViewPanel(swing) )
          widget( buildCodePanel(swing) )
       }

       frame.windowClosing = this.&exit

       runWaitDialog = swing.dialog(title: 'Groovy executing',
             owner: frame,
             modal: true ) {
          vbox(border: BF.createEmptyBorder(6, 6, 6, 6)) {
             label(text: "Groovy is now executing. Please wait.", alignmentX: 0.5f)
             vstrut()
             button(interruptAction,
                 margin: new Insets(10, 20, 10, 20),
                 alignmentX: 0.5f
             )
          }
       }
    }

    private def buildViewPanel( swing ){
       def graphicsPanel = new GraphicsPanel()
       graphicsPanel.border = BF.createEmptyBorder()
       graphicsPanel.background = Color.white
       graphicsPanel.addGraphicsErrorListener({ evt ->
           displayError( evt.cause.localizedMessage )
       } as GraphicsErrorListener )

       swing.scrollPane( border: BF.createTitledBorder(BF.createLineBorder(Color.BLACK), "View") ){
          panel( graphicsPanel, id: 'view' )
       }
    }

    private def buildCodePanel( swing ){
        def sourcePanel = swing.panel {
           borderLayout( )
           scrollPane( constraints: BL.CENTER, border: BF.createTitledBorder(BF.createLineBorder(Color.BLACK), "Source") ) {
              container( inputEditor, id: 'source', border: BF.createEmptyBorder(),
                        font: new Font( Font.MONOSPACED, Font.PLAIN, 14 ) ){
                 action(runAction)
              }
           }
           scrollPane( constraints: BL.SOUTH, border: BF.createTitledBorder(BF.createLineBorder(Color.BLACK), "Errors") ) {
              textArea( id: 'error',  rows: 4 )
           }
        }

        return sourcePanel
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
       pane.setMessage('Welcome to the Groovy GraphicsPad')
       def dialog = pane.createDialog(frame, 'About GraphicsPad')
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

    // Confirm whether to interrupt the running thread
    void confirmRunInterrupt(EventObject evt) {
        def rc = JOptionPane.showConfirmDialog(frame, "Attempt to interrupt script?",
            "GraphicsPad", JOptionPane.YES_NO_OPTION)
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
                        import javax.swing.*
                        import org.jdesktop.swingx.geom.*
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