/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.junctions.ui

import java.awt.Color
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext

menuBarClass     = org.codehaus.groovy.junctions.ui.BasicMenuBar
contentPaneClass = org.codehaus.groovy.junctions.ui.BasicContentPane
statusBarClass   = org.codehaus.groovy.junctions.ui.BasicStatusBar

styles = [
    // output window styles
    regular: [
            (StyleConstants.FontFamily): 'Monospaced',
        ],
    prompt: [
            (StyleConstants.Foreground): new Color(0, 128, 0),
        ],
    command: [
            (StyleConstants.Foreground): Color.BLUE,
        ],
    output: [:],
    result: [
            (StyleConstants.Foreground): Color.BLUE,
            (StyleConstants.Background): Color.YELLOW,
        ],

    // syntax highlighting styles
    (StyleContext.DEFAULT_STYLE) : [
            (StyleConstants.FontFamily): 'Monospaced',
        ],
]