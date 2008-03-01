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

package groovy.swing.j2d.app

import groovy.swing.j2d.app.view.*
import java.awt.*
import javax.swing.*
import javax.swing.BorderFactory as BF
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE
import javax.swing.event.DocumentListener

switch (UIManager.getSystemLookAndFeelClassName()) {
    case 'com.sun.java.swing.plaf.windows.WindowsLookAndFeel':
    case 'com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel':
        build(WindowsDefaults)
        break

    case 'apple.laf.AquaLookAndFeel':
        build(MacOSXDefaults)
        break

    case 'com.sun.java.swing.plaf.gtk.GTKLookAndFeel':
        build(GTKDefaults)
        break

    default:
        build(Defaults)
        break
}

frame( title: "GraphicsPad", size: [1024,800],
      locationRelativeTo: null, id: 'graphicsFrame', show: true,
      iconImage: imageIcon("/groovy/ui/ConsoleIcon.png").image,
      defaultCloseOperation: DO_NOTHING_ON_CLOSE ){

   build(menuBarClass)
   build(contentPaneClass)
   build(toolBarClass)
   build(statusBarClass)

   dialog(title: 'Groovy executing', id: 'runWaitDialog', modal: true ) {
          vbox(border: BF.createEmptyBorder(6, 6, 6, 6)) {
             label(text: "Groovy is now executing. Please wait.", alignmentX: 0.5f)
             vstrut()
             button(interruptAction,
                 margin: new Insets(10, 20, 10, 20),
                 alignmentX: 0.5f
             )
          }
       }

   dialog(title: 'Save as Image', id: 'saveAsImageDialog', modal: true ) {
       panel {
          borderLayout()
          panel( constraints: BorderLayout.CENTER ){
             vbox {
                hbox {
                   label( 'File:' )
                   textField( id: 'imageFile', columns: 40 )
                   button( 'Browse...', actionPerformed: {e->
                      def filename = controller.selectFilename()
                      if( filename ) imageFile.text = filename
                   })
                }
                vstrut()
                hbox {
                   label( 'Width:' )
                   textField( id: 'imageWidth', columns: 10 )
                   hstrut()
                   label( 'Height:' )
                   textField( id: 'imageHeight', columns: 10 )
                }
             }
          }
          panel( constraints: BorderLayout.SOUTH ) {
             button( 'Cancel', actionPerformed: controller.&cancelSaveAsImage )
             button( 'Ok', actionPerformed: controller.&okSaveAsImage )
          }
       }
   }
}

// add the window close handler
graphicsFrame.windowClosing = controller.&exit

// link in references to the controller
controller.inputEditor = inputEditor
controller.inputArea = inputEditor.textEditor
controller.status = status
controller.view = view
controller.frame = graphicsFrame
controller.runWaitDialog = runWaitDialog
controller.saveAsImageDialog = saveAsImageDialog
controller.rowNumAndColNum = rowNumAndColNum
controller.toolbar = toolbar
controller.error = error

// link actions
controller.saveAction = saveAction

// some more UI linkage
controller.inputArea.addCaretListener(controller)
controller.inputArea.document.addDocumentListener({ controller.setDirty(true) } as DocumentListener)
controller.rootElement = inputArea.document.defaultRootElement

// don't send any return value from the view, all items should be referenced via the bindings
return null