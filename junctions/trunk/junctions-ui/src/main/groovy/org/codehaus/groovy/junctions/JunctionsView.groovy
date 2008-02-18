/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.junctions

import org.codehaus.groovy.junctions.ui.*
import javax.swing.UIManager
import static javax.swing.SwingConstants.*
import static javax.swing.WindowConstants.EXIT_ON_CLOSE

String APP_ICON = '/groovy/ui/ConsoleIcon.png'

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

frame(title: 'Junctions',
        locationRelativeTo: null,
        iconImage: imageIcon(APP_ICON).image,
        defaultCloseOperation: EXIT_ON_CLOSE,
        size: [800, 600],
        location: [0, 0],
        show: true,
        pack: true,
        id: 'frame') {
    build(menuBarClass)
    build(contentPaneClass)
    build(statusBarClass)
}

dialog(id: 'waitDialog', owner: frame, modal: true,
        size: [240, 120], locationRelativeTo: frame,
        classicSwing: true, pack: false) {
    gridLayout(cols: 1, rows: 2)
    label("Processing", horizontalAlignment: CENTER, verticalAlignment: CENTER)
    busyLabel(busy: true, horizontalAlignment: CENTER, verticalAlignment: CENTER)
}

popupMenu(id: 'subscriptionPopup') {
    menuItem(markAllAsReadAction)
    separator()
    menuItem(refreshSubscriptionAction)
    separator()
    menu(text: 'Stats from...', id: 'statsMenu',
            icon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/activity_window_16.png"))) {
        //menuItem(diggStatsAction)
        //menuItem(dzoneStatsAction)
        menuItem(cosmosStatsAction)
    }
}

// add the window close handler
frame.windowClosing = controller.&exit

// link in references to the controller
controller.frame = frame
controller.waitDialog = waitDialog
controller.subscriptionPopup = subscriptionPopup

// don't send any return value from the view, all items should be referenced via the bindings
return null