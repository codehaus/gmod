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
import java.awt.event.*
import java.security.*
import javax.swing.*
import javax.swing.border.*
import javax.swing.event.*
import javax.swing.text.DefaultStyledDocument
import org.jdesktop.swingx.JXTitledPanel
import org.jdesktop.swingx.border.*
import groovy.swing.SwingBuilder
import groovy.swing.j2d.*
import groovy.swing.j2d.event.*
import groovy.text.SimpleTemplateEngine
import groovy.ui.Console
import groovy.ui.ConsoleTextEditor
import groovy.ui.text.FindReplaceUtility

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

    private static String ICON_PATH = '/groovy/ui/ConsoleIcon.png' // used by ObjectBrowser too

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
       swing.doLater {
          frame.visible = true
          inputEditor.textEditor.requestFocus()
       }
    }

    private void setupGraphicsBuilder(){
       graphicsBuilder = new GraphicsBuilder()
       def helpers = [/*"Jdk6GraphicsBuilderHelper",*/
                      "SwingXGraphicsBuilderHelper",
                      "BatikGraphicsBuilderHelper"]
       helpers.each { helper ->
           try{
              Class helperClass = Class.forName("groovy.swing.j2d.${helper}")
              helperClass.registerOperations( graphicsBuilder )
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
          // whether or not application exit should have an
          // accellerator is debatable in usability circles
          // at the very least a confirm dialog should dhow up
          //accelerator: shortcut('Q')
          action(inputEditor.undoAction,
              id: 'undoAction',
              name: 'Undo',
              mnemonic: 'U',
              accelerator: shortcut('Z'),
              smallIcon: imageIcon(resource:"icons/arrow_undo.png", class:Console),
              shortDescription: 'Undo'
          )
          action(inputEditor.redoAction,
              id: 'redoAction',
              name: 'Redo',
              mnemonic: 'R',
              accelerator: shortcut('shift Z'), // is control-shift-Z or control-Y more common?
              smallIcon: imageIcon(resource:"icons/arrow_redo.png", class:Console),
              shortDescription: 'Redo'
          )
          action(id: 'findAction',
              name: 'Find...',
              closure: this.&find,
              mnemonic: 'F',
              accelerator: shortcut('F'),
              smallIcon: imageIcon(resource:"icons/find.png", class:Console),
              shortDescription: 'Find'
          )
          action(id: 'findNextAction',
              name: 'Find Next',
              closure: this.&findNext,
              mnemonic: 'N',
              accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0)
          )
          action(id: 'findPreviousAction',
              name: 'Find Previous',
              closure: this.&findPrevious,
              mnemonic: 'V',
              accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK)
          )
          action(id: 'replaceAction',
              name: 'Replace...',
              closure: this.&replace,
              mnemonic: 'E',
              accelerator: shortcut('H'),
              smallIcon: imageIcon(resource:"icons/text_replace.png", class:Console),
              shortDescription: 'Replace'
          )
          action(id: 'cutAction',
              name: 'Cut',
              closure: this.&cut,
              mnemonic: 'T',
              accelerator: shortcut('X'),
              smallIcon: imageIcon(resource:"icons/cut.png", class:Console),
              shortDescription: 'Cut'
          )
          action(id: 'copyAction',
              name: 'Copy',
              closure: this.&copy,
              mnemonic: 'C',
              accelerator: shortcut('C'),
              smallIcon: imageIcon(resource:"icons/page_copy.png", class:Console),
              shortDescription: 'Copy'
          )
          action(id: 'pasteAction',
              name: 'Paste',
              closure: this.&paste,
              mnemonic: 'P',
              accelerator: shortcut('V'),
              smallIcon: imageIcon(resource:"icons/page_paste.png", class:Console),
              shortDescription: 'Paste'
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
              keyStroke: shortcut('ENTER'),
              accelerator: shortcut('R'),
              smallIcon: imageIcon(resource:"icons/script_go.png", class:Console),
              shortDescription: 'Execute Groovy Script'
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
             locationRelativeTo: null, iconImage: swing.imageIcon(ICON_PATH).image, ){
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
             panel( buildListPanel(swing), constraints: BL.WEST )
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
                container( inputEditor, id: 'source', border: BorderFactory.createEmptyBorder(),
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
       swing.doOutside {
          Thread.currentThread().contextClassLoader.getResourceAsStream("groovy/swing/j2d/demo/${demo}.html").text
       }
    }

    private def loadSource( demo ){
       swing.doOutside {
          Thread.currentThread().contextClassLoader.getResourceAsStream("groovy/swing/j2d/demo/source-${demo}.txt").text
       }
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
       def text = descriptionCache[(demo)]
       if( !text ){
          swing.doOutside {
             text = Thread.currentThread().contextClassLoader.getResourceAsStream("groovy/swing/j2d/demo/${demo}.html").text
             descriptionCache[(demo)] = text
             setDescriptionText( text )
          }
       }else{
          setDescriptionText( text )
       }
    }

    private void setDescriptionText( text ) {
       swing.doLater {
          description.text = text
          description.caretPosition = 0
       }
    }

    void onClickDemoOption(EventObject evt = null) {
       if( evt.eventType == HyperlinkEvent.EventType.ACTIVATED ){
          def demo = evt.description
          def text = sourceCache[(demo)]
          if( !text ){
             swing.doOutside {
                text = Thread.currentThread().contextClassLoader.getResourceAsStream("groovy/swing/j2d/demo/source-${demo}.txt").text
                sourceCache[(demo)] = text
                setSourceAndExecute( text )
             }
          }else{
             setSourceAndExecute( text )
          }
       }
    }

    private void setSourceAndExecute( source ) {
       swing.doLater {
          inputEditor.textEditor.text = source
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
                  swing.doLater { showRunWaitDialog() }
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
                   def go = graphicsBuilder.group( !codeBase ? gsh.evaluate(script) :
                      gsh.evaluate(script,"RestrictedScript",codeBase) )
                  if( go.operations.size() == 0 ){
                     throw new RuntimeException("An operation is not recognized. Please check the code.")
                  }
                  swing.doLater { finishNormal(go) }
              } catch (Throwable t) {
                  swing.doLater { finishException(t) }
              } finally {
                  swing.doLater {
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

    void find(EventObject evt = null) {
       FindReplaceUtility.showDialog()
    }

    void findNext(EventObject evt = null) {
       FindReplaceUtility.FIND_ACTION.actionPerformed(evt)
    }

    void findPrevious(EventObject evt = null) {
       def reverseEvt = new ActionEvent(
           evt.getSource(), evt.getID(),
           evt.getActionCommand(), evt.getWhen(),
           ActionEvent.SHIFT_MASK) //reverse
       FindReplaceUtility.FIND_ACTION.actionPerformed(reverseEvt)
    }

    void replace(EventObject evt = null) {
       FindReplaceUtility.showDialog(true)
    }
}

class OptionCellRenderer extends DefaultListCellRenderer {
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
      Component renderer = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus)
      renderer.setIcon( Main.getIcon('option') )
      return renderer
   }
}