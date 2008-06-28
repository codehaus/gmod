/*
 * Copyright 2007-2008 the original author or authors.
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

package groovy.swing.j2d.app

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
class GraphicsPad extends Binding implements CaretListener {
    private prefs = Preferences.userNodeForPackage(Console)

    private def swing
    private def gb
    private def gr
    private def runThread = null

    private boolean dirty
    private def scriptFile
    private File currentFileChooserDir = new File(prefs.get('currentFileChooserDir', '.'))
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
                   getResourceAsStream("groovy/swing/j2d/app/simple-script.txt").text
           export_script_source = Thread.currentThread().contextClassLoader.
                   getResourceAsStream("groovy/swing/j2d/app/export-script.txt").text
       }

       new GraphicsPad().run()
    }

    GraphicsPad(){
       buildUI()
       gr = new GraphicsRenderer()
       gb = gr.gb
    }

    public void run(){
       swing.doLater {
          inputArea.requestFocus()
       }
    }

    private void buildUI(){
       swing = new SwingBuilder()

       swing.lookAndFeel('system')
       swing.controller = this
       swing.doLater {
          build(GraphicsActions)
          build(GraphicsView)

          swing.bind(source:swing.inputEditor.undoAction, sourceProperty:'enabled',
                  target:undoAction, targetProperty:'enabled')
          swing.bind(source:swing.inputEditor.redoAction, sourceProperty:'enabled',
                  target:swing.redoAction, targetProperty:'enabled')
       }
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
            inputArea.text = ''
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
            inputArea.text = scriptFile.readLines().join('\n')
            setDirty(false)
            inputArea.caretPosition = 0
        }
    }

    // Save file - return false if user cancelled save
    boolean fileSave(EventObject evt = null) {
        if (scriptFile == null) {
            return fileSaveAs(evt)
        } else {
            scriptFile.write(inputArea.text)
            setDirty(false)
            return true
        }
    }

    boolean fileSaveAs(EventObject evt = null) {
        scriptFile = selectFilename("Save")
        if (scriptFile != null) {
            scriptFile.write(inputArea.text)
            setDirty(false)
            return true
        } else {
            return false
        }
    }

    void showSaveAsImageDialog(EventObject evt = null) {
       if( !inputArea.text ) return
       saveAsImageDialog.pack()
       int x = frame.x + (frame.width - runWaitDialog.width) / 2
       int y = frame.y + (frame.height - runWaitDialog.height) / 2
       saveAsImageDialog.setLocation(x, y)
       saveAsImageDialog.show()
    }

    void cancelSaveAsImage(EventObject evt = null) {
       swing.imageWidth.text = ""
       swing.imageHeight.text = ""
       swing.imageFile.text = ""
       swing.saveAsImageDialog.dispose()
    }

    void okSaveAsImage(EventObject evt = null) {
       def iw = swing.imageWidth.text
       def ih = swing.imageHeight.text
       def ifn = swing.imageFile.text

       if( !iw ){
          showAlert("Save as Image","Please type a valid width")
          return
       }
       if( !ih ){
          showAlert("Save as Image","Please type a valid height")
          return
       }
       if( !ifn ){
          showAlert("Save as Image","Please select a file")
          return
       }

       cancelSaveAsImage(evt)
       swing.doOutside {
          def binding = [source:inputArea.text]
          def template = templateEngine.createTemplate(simple_script_source).make(binding)
          def script = template.toString()
          def go = gb.group( [bc:'black'], new GroovyShell().evaluate(script) )
          if( go.operations.size() ){
             gr.renderToFile( ifn, iw.toInteger(), ih.toInteger(), go )
             showMessage("Save as Image", "Succesfully saved image to\n$ifn")
          }
       }
    }

    def showAlert(title, message) {
       swing.doLater {
           JOptionPane.showMessageDialog(frame, message,
                   title, JOptionPane.WARNING_MESSAGE)
       }
    }

    def showMessage(title, message) {
       swing.doLater {
           JOptionPane.showMessageDialog(frame, message,
                   title, JOptionPane.INFORMATION_MESSAGE)
       }
    }


    boolean fileExport(EventObject evt = null) {
        scriptFile = selectFilename("Export")
        if (scriptFile != null) {
            def binding = [source:inputArea.text, title: scriptFile.name - ".groovy"]
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
       if (inputArea.font.size > 40) return
       def newFont = new Font('Monospaced', Font.PLAIN, inputArea.font.size + 2)
       inputArea.font = newFont
    }

    void smallerFont(EventObject evt = null){
       if (inputArea.font.size < 5) return
       def newFont = new Font('Monospaced', Font.PLAIN, inputArea.font.size - 2)
       inputArea.font = newFont
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
       //TODO when @BoundProperty is live, this should be handled via listeners
       dirty = newDirty
       swing.saveAction.enabled = newDirty
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
           closure(inputArea)
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
        if( !inputArea.text.trim() ){
           displayError( "Please type some code" )
        }else{
           runThread = Thread.start {
              try {
                  SwingUtilities.invokeLater {
                     status.text = 'Running Script...'
                     showRunWaitDialog()
                     error.text = ""
                     view.removeAll()
                  }
                  def binding = [source:inputArea.text]
                  def template = templateEngine.createTemplate(simple_script_source).make(binding)
                  def script = template.toString()
                  def go = gb.group( new GroovyShell().evaluate(script) )
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
       displayError( t.message )
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
       inputArea.text = ""
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
            "GraphicsPad", JOptionPane.YES_NO_CANCEL_OPTION)){
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
        cursorPos = inputArea.getCaretPosition()
        rowNum = rootElement.getElementIndex(cursorPos) + 1

        def rowElement = rootElement.getElement(rowNum - 1)
        colNum = cursorPos - rowElement.getStartOffset() + 1

        rowNumAndColNum.setText("$rowNum:$colNum")
    }
}