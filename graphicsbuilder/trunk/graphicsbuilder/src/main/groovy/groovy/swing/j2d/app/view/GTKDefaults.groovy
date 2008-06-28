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

package groovy.swing.j2d.app.view

import javax.swing.JComponent
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext
import org.codehaus.groovy.runtime.InvokerHelper

build(Defaults)

// change font to DejaVu Sans Mono, much clearer
styles.regular[StyleConstants.FontFamily] = 'DejaVu Sans Mono'
styles[StyleContext.DEFAULT_STYLE][StyleConstants.FontFamily] = 'DejaVu Sans Mono'

// possibly change look and feel
if (System.properties['java.version'] =~ /^1\.5/) {
    // GTK wasn't where it needed to be in 1.5, especially with toolbars
    // use metal instead
    lookAndFeel('metal', boldFonts:false)

    // we also need to turn on anti-alising ourselves
    key = InvokerHelper.getProperty('com.sun.java.swing.SwingUtilities2' as Class,
        'AA_TEXT_PROPERTY_KEY')
    addAttributeDelegate {builder, node, attributes ->
        if (node instanceof JComponent) {
            node.putClientProperty(key, new Boolean(true));
        }
    }
}