/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package groovy.swing.j2d.app

import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.KeyStroke
import groovy.ui.Console

action(id: 'newFileAction',
   name: 'New File',
   closure: controller.&fileNewFile,
   mnemonic: 'N',
   accelerator: shortcut('N'),
   smallIcon: imageIcon(resource:"icons/page.png", class:Console),
   shortDescription: 'New Groovy Script'
)

action(id: 'newWindowAction',
   name: 'New Window',
   closure: controller.&fileNewWindow,
   mnemonic: 'W',
   accelerator: shortcut('shift N')
)

action(id: 'openAction',
   name: 'Open',
   closure: controller.&fileOpen,
   mnemonic: 'O',
   accelerator: shortcut('O'),
   smallIcon: imageIcon(resource:"icons/folder_page.png", class:Console),
   shortDescription: 'Open Groovy Script'
)

action(id: 'saveAction',
   name: 'Save',
   closure: controller.&fileSave,
   mnemonic: 'S',
   accelerator: shortcut('S'),
   smallIcon: imageIcon(resource:"icons/disk.png", class:Console),
   shortDescription: 'Save Groovy Script',
   enabled: false
)

action(id: 'saveAsAction',
   name: 'Save as...',
   closure: controller.&fileSaveAs,
   mnemonic: 'A',
)

action(id: 'saveAsImageAction',
   name: 'Save as Image...',
   closure: controller.&showSaveAsImageDialog,
   mnemonic: 'I',
   accelerator: shortcut('I'),
   shortDescription: 'Save as Image'
)

action(id: 'exportAction',
   name: 'Export...',
   closure: controller.&fileExport
)

action(id: 'exitAction',
   name: 'Exit',
   closure: controller.&exit,
   mnemonic: 'X'
)

action(id: 'undoAction',
   name: 'Undo',
   mnemonic: 'U',
   accelerator: shortcut('Z'),
   smallIcon: imageIcon(resource:"icons/arrow_undo.png", class:Console),
   shortDescription: 'Undo'
)

action(id: 'redoAction',
   name: 'Redo',
   mnemonic: 'R',
   accelerator: shortcut('shift Z'), // is control-shift-Z or control-Y more common?
   smallIcon: imageIcon(resource:"icons/arrow_redo.png", class:Console),
   shortDescription: 'Redo'
)

action(id: 'findAction',
   name: 'Find...',
   closure: controller.&find,
   mnemonic: 'F',
   accelerator: shortcut('F'),
   smallIcon: imageIcon(resource:"icons/find.png", class:Console),
   shortDescription: 'Find'
)

action(id: 'findNextAction',
   name: 'Find Next',
   closure: controller.&findNext,
   mnemonic: 'N',
   accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0)
)

action(id: 'findPreviousAction',
   name: 'Find Previous',
   closure: controller.&findPrevious,
   mnemonic: 'V',
   accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK)
)

action(id: 'replaceAction',
   name: 'Replace...',
   closure: controller.&replace,
   mnemonic: 'E',
   accelerator: shortcut('H'),
   smallIcon: imageIcon(resource:"icons/text_replace.png", class:Console),
   shortDescription: 'Replace'
)
action(id: 'cutAction',
   name: 'Cut',
   closure: controller.&cut,
   mnemonic: 'T',
   accelerator: shortcut('X'),
   smallIcon: imageIcon(resource:"icons/cut.png", class:Console),
   shortDescription: 'Cut'
)

action(id: 'copyAction',
   name: 'Copy',
   closure: controller.&copy,
   mnemonic: 'C',
   accelerator: shortcut('C'),
   smallIcon: imageIcon(resource:"icons/page_copy.png", class:Console),
   shortDescription: 'Copy'
)

action(id: 'pasteAction',
   name: 'Paste',
   closure: controller.&paste,
   mnemonic: 'P',
   accelerator: shortcut('V'),
   smallIcon: imageIcon(resource:"icons/page_paste.png", class:Console),
   shortDescription: 'Paste'
)
action(id: 'selectAllAction',
   name: 'Select All',
   closure: controller.&selectAll,
   mnemonic: 'A',
   accelerator: shortcut('A')
)

action(id: 'runAction',
   name: 'Run',
   closure: controller.&executeCode,
   mnemonic: 'R',
   keyStroke: shortcut('ENTER'),
   accelerator: shortcut('R'),
   smallIcon: imageIcon(resource:"icons/script_go.png", class:Console),
   shortDescription: 'Execute Groovy Script'
)

action(id: 'largerFontAction',
   name: 'Larger Font',
   closure: controller.&largerFont,
   mnemonic: 'L',
   accelerator: shortcut('shift L')
)

action(id: 'smallerFontAction',
   name: 'Smaller Font',
   closure: controller.&smallerFont,
   mnemonic: 'S',
   accelerator: shortcut('shift S')
)

action(id: 'aboutAction',
   name: 'About',
   closure: controller.&showAbout,
   mnemonic: 'A'
)

action(id: 'interruptAction',
   name: 'Interrupt',
   closure: controller.&confirmRunInterrupt
)