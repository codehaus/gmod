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
import java.awt.event.*
import javax.swing.*
import javax.swing.BorderFactory as BF
import javax.swing.border.*
import javax.swing.event.*
import javax.swing.text.DefaultStyledDocument
import groovy.swing.SwingBuilder
import groovy.swing.j2d.*
import groovy.swing.j2d.event.*
import groovy.text.SimpleTemplateEngine
import groovy.ui.Console
import groovy.ui.ConsoleTextEditor
import groovy.ui.text.FindReplaceUtility
import java.util.prefs.Preferences

import org.codehaus.groovy.control.CompilationFailedException

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsPad implements CaretListener {
    private def graphicsBuilder
    private def swing
    private def gsh = new GroovyShell()
    private def inputEditor
    private def runThread = null
    private def runWaitDialog
    private def frame

    private boolean dirty
    private def scriptFile
    private File currentFileChooserDir = new File(Preferences.userNodeForPackage(GraphicsPad).get('currentFileChooserDir', '.'))
    private static String ICON_PATH = '/groovy/ui/ConsoleIcon.png' // used by ObjectBrowser too

        // row info
    private def rootElement
    private def cursorPos
    private def rowNum
    private def colNum

    private int scriptNameCounter = 0
    private def templateEngine = new SimpleTemplateEngine()
    private static def simple_script_source
    private static def export_script_source

    public static void main(String[] args) {
       Thread.start {
           simple_script_source = Thread.currentThread().contextClassLoader.
                   getResourceAsStream("groovy/swing/j2d/simple-script.txt").text
           export_script_source = Thread.currentThread().contextClassLoader.
                   getResourceAsStream("groovy/swing/j2d/export-script.txt").text
       }
       SwingUtilities.invokeLater {
          def app = new GraphicsPad()
          app.run()
       }
    }

    GraphicsPad(){
       buildUI()
       setupGraphicsBuilder()
    }

    public void run(){
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

       swing.lookAndFeel('system')
       System.setProperty("apple.laf.useScreenMenuBar", "true")
       System.setProperty("com.apple.mrj.application.apple.menu.about.name", "GraphicsPad")

       swing.actions {
           action(id: 'newFileAction',
               name: 'New File',
               closure: this.&fileNewFile,
               mnemonic: 'N',
               accelerator: shortcut('N'),
               smallIcon: imageIcon(resource:"icons/page.png", class:Console),
               shortDescription: 'New Groovy Script'
           )
           action(id: 'newWindowAction',
               name: 'New Window',
               closure: this.&fileNewWindow,
               mnemonic: 'W',
               accelerator: shortcut('shift N')
           )
           action(id: 'openAction',
               name: 'Open',
               closure: this.&fileOpen,
               mnemonic: 'O',
               accelerator: shortcut('O'),
               smallIcon: imageIcon(resource:"icons/folder_page.png", class:Console),
               shortDescription: 'Open Groovy Script'
           )
           action(id: 'saveAction',
               name: 'Save',
               closure: this.&fileSave,
               mnemonic: 'S',
               accelerator: shortcut('S'),
               smallIcon: imageIcon(resource:"icons/disk.png", class:Console),
               shortDescription: 'Save Groovy Script'
           )
           action(id: 'saveAsAction',
               name: 'Save As...',
               closure: this.&fileSaveAs,
               mnemonic: 'A',
           )
           action(id: 'exportAction',
               name: 'Export...',
               closure: this.&fileExport
           )
           action(inputEditor.printAction,
               id: 'printAction',
               name: 'Print...',
               mnemonic: 'P',
               accelerator: shortcut('P')
           )
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
       }

       frame = swing.frame( title: "GraphicsPad", size: [1024,800],
             locationRelativeTo: null,
             iconImage: swing.imageIcon(ICON_PATH).image,
             defaultCloseOperation: WindowConstants.DO_NOTHING_ON_CLOSE ){
           menuBar {
               menu(text: 'File', mnemonic: 'F') {
                   menuItem(newFileAction)
                   menuItem(newWindowAction)
                   menuItem(openAction)
                   separator()
                   menuItem(saveAction)
                   menuItem(saveAsAction)
                   menuItem(exportAction)
                   separator()
                   menuItem(printAction)
                   separator()
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
                   menuItem(findAction)
                   menuItem(findNextAction)
                   menuItem(findPreviousAction)
                   menuItem(replaceAction)
                   separator()
                   menuItem(selectAllAction)
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

          borderLayout()

           toolBar(rollover:true, constraints:BL.NORTH) {
               button(newFileAction, text:null)
               button(openAction, text:null)
               button(saveAction, text:null)
               separator(orientation:SwingConstants.VERTICAL)
               button(undoAction, text:null)
               button(redoAction, text:null)
               separator(orientation:SwingConstants.VERTICAL)
               button(cutAction, text:null)
               button(copyAction, text:null)
               button(pasteAction, text:null)
               separator(orientation:SwingConstants.VERTICAL)
               button(findAction, text:null)
               button(replaceAction, text:null)
               separator(orientation:SwingConstants.VERTICAL)
               button(runAction, text:null)
           }

          panel {
             gridLayout( cols: 1, rows: 2 )
             widget( buildViewPanel(swing) )
             widget( buildCodePanel(swing) )
          }

          panel(id: 'statusPanel', constraints: BL.SOUTH) {
              gridBagLayout()
              separator(constraints:gbc(gridwidth:GridBagConstraints.REMAINDER, fill:GridBagConstraints.HORIZONTAL))
              label('Welcome to Groovy GraphicsPad.',
                  id: 'status',
                  constraints:gbc(weightx:1.0,
                      anchor:GridBagConstraints.WEST,
                      fill:GridBagConstraints.HORIZONTAL,
                      insets: [1,3,1,3])
              )
              separator(orientation:SwingConstants.VERTICAL, constraints:gbc(fill:GridBagConstraints.VERTICAL))
              label('1:1',
                  id: 'rowNumAndColNum',
                  constraints:gbc(insets: [1,3,1,3])
              )
          }
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

       inputEditor.textEditor.addCaretListener(this)
       rootElement = inputEditor.textEditor.document.defaultRootElement
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

        inputEditor.textEditor.document.addDocumentListener({ setDirty(true) } as DocumentListener)

        return sourcePanel
     }

    // ---------- ACTIONS -----------

    void exit(EventObject evt = null) {
        if (askToSaveFile()) {
            frame.hide()
            frame.dispose()
            FindReplaceUtility.dispose()
        }
    }

    void fileNewFile(EventObject evt = null) {
        if (askToSaveFile()) {
            scriptFile = null
            setDirty(false)
            inputEditor.textEditor.text = ''
        }
    }

    void fileNewWindow(EventObject evt = null) {
       SwingUtilities.invokeLater {
          def app = new GraphicsPad()
          app.run()
       }
    }

    void fileOpen(EventObject evt = null) {
        scriptFile = selectFilename()
        if (scriptFile != null) {
            inputEditor.textEditor.text = scriptFile.readLines().join('\n')
            setDirty(false)
            inputEditor.textEditor.caretPosition = 0
        }
    }

    // Save file - return false if user cancelled save
    boolean fileSave(EventObject evt = null) {
        if (scriptFile == null) {
            return fileSaveAs(evt)
        } else {
            scriptFile.write(inputEditor.textEditor.text)
            setDirty(false)
            return true
        }
    }

    boolean fileSaveAs(EventObject evt = null) {
        scriptFile = selectFilename("Save")
        if (scriptFile != null) {
            scriptFile.write(inputEditor.textEditor.text)
            setDirty(false)
            return true
        } else {
            return false
        }
    }

    boolean fileExport(EventObject evt = null) {
        scriptFile = selectFilename("Export")
        if (scriptFile != null) {
            def binding = [source:inputEditor.textEditor.text, title: scriptFile.name - ".groovy"]
            def template = templateEngine.createTemplate(export_script_source).make(binding)
            def script = template.toString()
            scriptFile.write(script)
            setDirty(false)
            return true
        } else {
            return false
        }
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

    def selectFilename(name = "Open") {
        def fc = new JFileChooser(currentFileChooserDir)
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.acceptAllFileFilterUsed = true
        if (fc.showDialog(frame, name) == JFileChooser.APPROVE_OPTION) {
            currentFileChooserDir = fc.currentDirectory
            Preferences.userNodeForPackage(GraphicsPad).put('currentFileChooserDir', currentFileChooserDir.path)
            return fc.selectedFile
        } else {
            return null
        }
    }

    void setDirty(boolean newDirty) {
        dirty = newDirty
        updateTitle()
    }

    void showAbout(EventObject evt = null) {
       def pane = swing.optionPane()
        // work around GROOVY-1048
       pane.setMessage('Welcome to the Groovy GraphicsPad')
       def dialog = pane.createDialog(frame, 'About GraphicsPad')
       dialog.show()
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

    void updateTitle() {
        if (scriptFile != null) {
            frame.title = scriptFile.name + (dirty?" * ":"") + " - GraphicsPad"
        } else {
            frame.title = "GraphicsPad"
        }
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

    void executeCode( EventObject evt = null ){
        if( !inputEditor.textEditor.text.trim() ){
           displayError( "Please type some code" )
        }else{
           swing.status.text = 'Running Script...'
           runThread = Thread.start {
              try {
                  SwingUtilities.invokeLater {
                     showRunWaitDialog()
                     swing.error.text = ""
                     swing.view.removeAll()
                  }
                  def binding = [source:inputEditor.textEditor.text]
                  def template = templateEngine.createTemplate(simple_script_source).make(binding)
                  def script = template.toString()
                  def go = graphicsBuilder.group( gsh.evaluate(script) )
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
       swing.status.text = 'Execution terminated with exception.'
       t.printStackTrace()
       displayError( t.localizedMessage )
    }

    def finishNormal(Object go) {
       if( go instanceof GraphicsOperation ){
          swing.view.@graphicsOperation = null
          swing.view.graphicsOperation = go
          swing.status.text = 'Execution complete.'
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

    //-----------------

    // Return false if use elected to cancel
    boolean askToSaveFile() {
        if (scriptFile == null || !dirty) {
            return true
        }
        switch (JOptionPane.showConfirmDialog(frame,
            "Save changes to " + scriptFile.name + "?",
            "GraphicsPad", JOptionPane.YES_NO_CANCEL_OPTION))
        {
            case JOptionPane.YES_OPTION:
                return fileSave()
            case JOptionPane.NO_OPTION:
                return true
            default:
                return false
        }
    }

    // Confirm whether to interrupt the running thread
    void confirmRunInterrupt(EventObject evt) {
        def rc = JOptionPane.showConfirmDialog(frame, "Attempt to interrupt script?",
            "GraphicsPad", JOptionPane.YES_NO_OPTION)
        if (rc == JOptionPane.YES_OPTION && runThread != null) {
            runThread.interrupt()
        }
    }

    void caretUpdate(CaretEvent e){
        cursorPos = inputEditor.textEditor.getCaretPosition()
        rowNum = rootElement.getElementIndex(cursorPos) + 1

        def rowElement = rootElement.getElement(rowNum - 1)
        colNum = cursorPos - rowElement.getStartOffset() + 1

        swing.rowNumAndColNum.setText("$rowNum:$colNum")
    }
}
