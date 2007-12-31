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

package groovy.swing.j2d.svg

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
import java.util.prefs.Preferences
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.*

import org.codehaus.groovy.control.CompilationFailedException

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class Svg2Groovy {
    private def graphicsBuilder
    private def swing
    private def gsh = new GroovyShell()
    private def inputEditor
    private def runThread = null
    private def runWaitDialog
    private def frame

    private def scriptFile
    private File currentFileChooserDir = new File(Preferences.userNodeForPackage(Svg2Groovy).get('currentFileChooserDir', '.'))
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
          def app = new Svg2Groovy()
          app.run()
       }
    }

    Svg2Groovy(){
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
       def helpers = ["Jdk6GraphicsBuilderHelper",
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
       System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Svg2Groovy")

       swing.actions {
           action(id: 'newWindowAction',
               name: 'New Window',
               closure: this.&fileNewWindow,
               mnemonic: 'W',
               accelerator: shortcut('shift N')
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
           action(id: 'exitAction',
               name: 'Exit',
               closure: this.&exit,
               mnemonic: 'X'
           )
           // whether or not application exit should have an
           // accellerator is debatable in usability circles
           // at the very least a confirm dialog should dhow up
           //accelerator: shortcut('Q')
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
           action(id: 'convert',
               name: 'Convert',
               closure: this.&convertSvg )
       }

       frame = swing.frame( title: "Svg2Groovy", size: [800,600],
             locationRelativeTo: null,
             iconImage: swing.imageIcon(ICON_PATH).image,
             defaultCloseOperation: WindowConstants.DO_NOTHING_ON_CLOSE ){
           menuBar {
               menu(text: 'File', mnemonic: 'F') {
                   menuItem(newWindowAction)
                   separator()
                   menuItem(saveAction)
                   menuItem(saveAsAction)
                   menuItem(exportAction)
                   separator()
                   menuItem(exitAction)
               }

               menu(text: 'View', mnemonic: 'V') {
                  menuItem(largerFontAction)
                  menuItem(smallerFontAction)
               }

               menu(text: 'Help', mnemonic: 'H') {
                  menuItem(aboutAction)
               }
           }

           borderLayout()

           panel( constraints: BL.NORTH ){
              borderLayout( hgap:10, vgap: 10)
              label( "Url: ", constraints: BL.WEST)
              textField( id: 'svgurl', constraints: BL.CENTER )
              button( constraints: BL.EAST, action: convert )
           }

           panel( constraints: BL.CENTER ) {
              tabbedPane(tabPlacement: JTabbedPane.TOP) {
                 widget( buildViewPanel(swing), title: 'View' )
                 widget( buildGroovyPanel(swing), title: 'Groovy' )
                 widget( buildSvgPanel(swing), title: 'SVG' )
              }
           }
       }

       frame.windowClosing = this.&exit

       runWaitDialog = swing.dialog(title: 'Groovy executing',
             owner: frame,
             modal: true ) {
          vbox(border: BF.createEmptyBorder(6, 6, 6, 6)) {
             panel {
                gridLayout( cols: 1, rows: 3 )
                label(text: "Groovy is now executing. Please wait.", alignmentX: 0.5f)
                label(id:'update',text:'',horizontalAlignment:SwingConstants.CENTER)
                progressBar(indeterminate:true)
             }
             vstrut()
             button(interruptAction,
                 margin: new Insets(10, 20, 10, 20),
                 alignmentX: 0.5f
             )
          }
       }
       frame.pack()

       rootElement = inputEditor.textEditor.document.defaultRootElement
    }

    private def buildViewPanel( swing ){
       Toolkit toolkit = Toolkit.getDefaultToolkit()
       Dimension screen = toolkit.getScreenSize()
       //int width = screen.width as int
       //int height = screen.height as int

       def graphicsPanel = new GraphicsPanel()
       graphicsPanel.preferredSize = [800,800]
       graphicsPanel.border = BF.createEmptyBorder()
       graphicsPanel.background = Color.white
       graphicsPanel.addGraphicsErrorListener({ evt ->
           displayError( evt.cause.localizedMessage )
       } as GraphicsErrorListener )

       def rowHeader = swing.widget( new ScrollPaneRuler(ScrollPaneRuler.VERTICAL), opaque: true,
             preferredSize: [20,screen.width as int] )
       def columnHeader = swing.widget( new ScrollPaneRuler(ScrollPaneRuler.HORIZONTAL), opaque: true,
             preferredSize: [screen.height as int,20] )
       def scrollPane = swing.scrollPane( rowHeaderView: rowHeader, columnHeaderView: columnHeader,
             preferredSize: [800,600] ){
          panel( graphicsPanel, id: 'view' )
       }

       graphicsPanel.addMouseListener( rowHeader )
       graphicsPanel.addMouseMotionListener( rowHeader )
       graphicsPanel.addMouseListener( columnHeader )
       graphicsPanel.addMouseMotionListener( columnHeader )

       return scrollPane
    }

    private def buildSvgPanel( swing ){
       def scrollPane = swing.scrollPane( preferredSize: [800,600], border: BF.createLineBorder(Color.BLACK) ) {
          textArea( id: 'svgSource', font: new Font( 'Monospaced', Font.PLAIN, 14 ), editable: false,
                lineWrap: true, wrapStyleWord: true )
       }

       return scrollPane
    }

    private def buildGroovyPanel( swing ){
        def scrollPane = swing.scrollPane( ) {
           container( inputEditor, id: 'groovySource', preferredSize: [800,600],
                      font: new Font( 'Monospaced', Font.PLAIN, 14 ) )
        }
        inputEditor.textEditor.editable = false

        return scrollPane
    }

    // ---------- ACTIONS -----------

    void exit(EventObject evt = null) {
        if (askToSaveFile()) {
            frame.hide()
            frame.dispose()
        }
    }

    void fileNewWindow(EventObject evt = null) {
       SwingUtilities.invokeLater {
          def app = new Svg2Groovy()
          app.run()
       }
    }

    // Save file - return false if user cancelled save
    boolean fileSave(EventObject evt = null) {
        if (scriptFile == null) {
            return fileSaveAs(evt)
        } else {
            scriptFile.write(inputEditor.textEditor.text)
            return true
        }
    }

    boolean fileSaveAs(EventObject evt = null) {
        scriptFile = selectFilename("Save")
        if (scriptFile != null) {
            scriptFile.write(inputEditor.textEditor.text)
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
            return true
        } else {
            return false
        }
    }

    void largerFont(EventObject evt = null) {
       if (inputEditor.textEditor.font.size > 40) return
       def newFont = new Font('Monospaced', Font.PLAIN, inputEditor.textEditor.font.size + 2)
       inputEditor.textEditor.font = newFont
       swing.svgSource.font = newFont
    }

    void smallerFont(EventObject evt = null){
       if (inputEditor.textEditor.font.size < 5) return
       def newFont = new Font('Monospaced', Font.PLAIN, inputEditor.textEditor.font.size - 2)
       inputEditor.textEditor.font = newFont
       swing.svgSource.font = newFont
    }

    def selectFilename(name = "Open") {
        def fc = new JFileChooser(currentFileChooserDir)
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.acceptAllFileFilterUsed = true
        if (fc.showDialog(frame, name) == JFileChooser.APPROVE_OPTION) {
            currentFileChooserDir = fc.currentDirectory
            Preferences.userNodeForPackage(Svg2Groovy).put('currentFileChooserDir', currentFileChooserDir.path)
            return fc.selectedFile
        } else {
            return null
        }
    }

    void showAbout(EventObject evt = null) {
       def pane = swing.optionPane()
        // work around GROOVY-1048
       pane.setMessage('Welcome to Svg2Groovy')
       def dialog = pane.createDialog(frame, 'About Svg2Groovy')
       dialog.show()
    }

    void convertSvg( EventObject evt = null ){
       def svgurl = swing.svgurl.text
       if( svgurl ){
          runThread = Thread.start {
             try{
                swing.doLater {
                   showRunWaitDialog()
                   swing.view.removeAll()
                }
                def svg = loadSvg( svgurl.toURL() )
                def groovy = convertSvg( svg )
                executeGroovy( groovy )
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

    def loadSvg = { url ->
       swing.doLater{ swing.update.text = "Loading SVG. .." }
       return url.text
    }

    def reader = SAXParserFactory.newInstance().newSAXParser().xMLReader

    def convertSvg = { svg ->
       swing.doLater{
          swing.svgSource.text = svg
          swing.update.text = "Converting SVG ..."
       }
	   def writer = new StringWriter()
	   def handler = new Svg2GroovyHandler(writer)
	   reader.contentHandler = handler
	   reader.parse( new InputSource(new StringReader(svg)) )
       return writer.toString()
    }

    def executeGroovy = { groovy ->
       swing.doLater{
          inputEditor.textEditor.text = groovy
          swing.update.text = "Drawing Groovy ..."
       }
       def binding = [source:groovy]
       def template = templateEngine.createTemplate(simple_script_source).make(binding)
       def script = template.toString()
       def go = graphicsBuilder.group( gsh.evaluate(script) )
       if( go.operations.size() == 0 ){
          throw new RuntimeException("An operation is not recognized. Please check the code.")
       }
       swing.doLater {
          if( go instanceof GraphicsOperation ){
             swing.update.text = "Drawing Groovy ..."
             swing.view.@graphicsOperation = null
             swing.view.graphicsOperation = go
             //swing.status.text = 'Execution complete.'
          }
       }
    }

    def finishException(Throwable t) {
       //swing.status.text = 'Execution terminated with exception.'
       t.printStackTrace()
       displayError( t.message )
    }

    void showRunWaitDialog() {
       swing.update.text = ""
       runWaitDialog.pack()
       int x = frame.x + (frame.width - runWaitDialog.width) / 2
       int y = frame.y + (frame.height - runWaitDialog.height) / 2
       runWaitDialog.setLocation(x, y)
       runWaitDialog.show()
    }

    private def displayError = { text ->

    }

    //-----------------

    // Return false if use elected to cancel
    boolean askToSaveFile() {
        if (scriptFile == null ) {
            return true
        }
        switch (JOptionPane.showConfirmDialog(frame,
            "Save changes to " + scriptFile.name + "?",
            "Svg2Groovy", JOptionPane.YES_NO_CANCEL_OPTION))
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
            "Svg2Groovy", JOptionPane.YES_NO_OPTION)
        if (rc == JOptionPane.YES_OPTION && runThread != null) {
            runThread.interrupt()
        }
    }
}