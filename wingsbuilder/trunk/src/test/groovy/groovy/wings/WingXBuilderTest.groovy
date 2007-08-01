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
 * limitations under the License.
 */

 package groovy.wings

import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.ComponentOrientation
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.event.KeyEvent
import java.awt.event.InputEvent

import java.text.*
import javax.swing.text.*
import javax.swing.*
import org.wings.*
import org.wingx.*
import org.wings.text.*
import org.wings.plaf.*
import org.wings.session.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class WingXBuilderTest extends GroovyTestCase {

    private boolean isHeadless() {
        try {
            new SFrame("testing")
            return false
        } catch (java.awt.HeadlessException he) {
            return true
        }
    }

    void testWidgetCreation() {
        if (isHeadless()) return

        def widgets = [
            calendar: XCalendar.class,
            /*colorPicker: XColorPicker.class,*/
            division: XDivision.class,
            /*inplaceEditor: XInplaceEditor.class,*/
            /*xpageSCroller: XPageScroller.class,*/
            popupFrame: XPopupFrame.class,
            scrollablePanel: XScrollablePanel.class,
            /*xscrollPane: XScrollPane.class,*/
            suggest: XSuggest.class,
            /*xtable: XTable.class,*/
            treeTable: XTreeTable.class,
            yuixGrid: YUIxGrid.class
        ]
        def wings = new WingXBuilder()
        widgets.each{ name, expectedLayoutClass ->
            def frame = wings.frame(){
               "$name"(id:"${name}Id".toString())
            }
            assert wings."${name}Id".class == expectedLayoutClass
        }
    }

   private Session session

   protected void setUp() throws Exception {
      ResourceDefaults resourceDefaults = new ResourceDefaults( null, new Properties() )
      session = new Session()
      RequestURL requestURL = new RequestURL( "http://localhost:8080/wingsbuilder", "http://localhost:8080/wingsbuilder" )
      session.setProperty("request.url", requestURL )
      session.CGManager.defaults = resourceDefaults
      SessionManager.setSession( session )
   }

   protected void tearDown() throws Exception {
      SessionManager.removeSession()
   }
}