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

build(Defaults)

// menu bar tweaks
System.setProperty("apple.laf.useScreenMenuBar", "true")
System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Junctions")

// redo output styles
styles = [
        // output window styles
        regular: [
                (StyleConstants.FontFamily): "Monaco",
                ],
        prompt: [
                (StyleConstants.Foreground): Color.LIGHT_GRAY,
                ],
        command: [
                (StyleConstants.Foreground): Color.GRAY,
                ],
        output: [:],
        result: [
                (StyleConstants.Foreground): Color.WHITE,
                (StyleConstants.Background): Color.BLACK,
                ],

        // syntax highlighting styles
        (GroovyFilter.COMMENT): [
                (StyleConstants.Foreground): Color.LIGHT_GRAY.darker().darker(),
                (StyleConstants.Italic): true,
                ],
        ]

menuBarClass = MacOSXMenuBar