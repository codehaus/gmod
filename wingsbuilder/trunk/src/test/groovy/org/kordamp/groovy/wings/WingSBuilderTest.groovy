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

package org.kordamp.groovy.wings

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
import org.wings.text.*
import org.wings.plaf.*
import org.wings.session.*
import org.kordamp.groovy.wings.impl.*

/**
 * Based on the original SwingBuilder testcase
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class WingSBuilderTest extends GroovyTestCase {

    private boolean isHeadless() {
        try {
            new SFrame("testing")
            return false
        } catch (java.awt.HeadlessException he) {
            return true
        }
    }

    void testNamedWidgetCreation() {
        if (isHeadless()) return

        def topLevelWidgets = [
            frame: [SFrame.class, true],
            dialog: [SDialog.class, true],
            optionPane: [SOptionPane.class, false]
        ]
        def wings = new WingSBuilder()
        topLevelWidgets.each{ name, widgetInfo ->
            if (widgetInfo[1])
                wings."$name"(id:"${name}Id".toString(), title:"This is my $name")
            else
                wings."$name"(id:"${name}Id".toString())
            def widget = wings."${name}Id"
            assert widget.class == widgetInfo[0]
            if (widgetInfo[1]) assert widget.title == "This is my $name"
        }
    }

    void testLayoutCreation() {
        if (isHeadless()) return

        def layouts = [
            borderLayout: SBorderLayout.class,
            cardLayout: SCardLayout.class,
            flowLayout: SFlowLayout.class,
            flowDownLayout: SFlowDownLayout.class,
            gridBagLayout: SGridBagLayout.class,
            gridLayout: SGridLayout.class,
            nullLayout: SNullLayout.class,
            rootLayout: SRootLayout.class,
            templateLayout: STemplateLayout.class,
            boxLayout: SBoxLayout.class
        ]
        def wings = new WingSBuilder()
        layouts.each{ name, expectedLayoutClass ->
            def frame = wings.frame(){
               panel(id:"panel"){
                  "$name"()
               }
            }
            assert wings.panel.layout.class == expectedLayoutClass
        }
    }

    void testWidgetCreation() {
        if (isHeadless()) return

        def widgets = [
            button: SButton.class,
            checkBox: SCheckBox.class,
            comboBox: SComboBox.class,
            desktopPane: SDesktopPane.class,
            formattedTextField: SFormattedTextField.class,
            internalFrame: SInternalFrame.class,
            label: SLabel.class,
            list: SList.class,
            menu: SMenu.class,
            menuBar: SMenuBar.class,
            menuItem: SMenuItem.class,
            panel: SPanel.class,
            passwordField: SPasswordField.class,
            popupMenu: SPopupMenu.class,
            progressBar: SProgressBar.class,
            radioButton: SRadioButton.class,
            scrollBar: SScrollBar.class,
            scrollPane: SScrollPane.class,
            separator: SSeparator.class,
            slider: SSlider.class,
            spinner: SSpinner.class,
            tabbedPane: STabbedPane.class,
            table: STable.class,
            textArea: STextArea.class,
            textField: STextField.class,
            toggleButton: SToggleButton.class,
            toolBar: SToolBar.class,
            tree: STree.class,
            anchor: SAnchor.class,
            byteArrayIcon: SByteArrayIcon.class,
            /*downloadButton: SDownloadButton.class,*/
            fileChooser: SFileChooser.class,
            /*fileIcon: SFileIcon.class,*/
            form: SForm.class,
            /*imageIcon: SImageIcon.class,*/
            pageScroller: SPageScroller.class,
            /*popup: SPopup.class,*/
            rawText: SRawTextComponent.class,
            /*resourceIcon: SResourceIcon.class,*/
            /*spacer: SSpacer.class*/
        ]
        def wings = new WingSBuilder()
        widgets.each{ name, expectedClass ->
            def frame = wings.frame(autoForm:false){
               "$name"(id:"${name}Id".toString())
            }
            assert wings."${name}Id".class == expectedClass
        }
    }

    void testButtonGroupOnlyForButtons() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        def buttonGroup = wings.buttonGroup()
        shouldFail(MissingPropertyException) {
            wings.label(buttonGroup:buttonGroup)
        }
    }

    void testWidget() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        def label = wings.label("By Value:")
        def widgetByValue = wings.widget(label)
        assert widgetByValue != null
        def widgetByLabel = wings.widget(widget: label)
        assert widgetByLabel != null
    }

    void testTableColumn() {
        if (isHeadless()) return

        // TODO is this required?
        def wings = new WingSBuilder()
        wings.table{
            tableColumn()
        }
    }

    void testNestedWindows() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        wings.frame{
            frame()
            frame{ frame() }
        }
    }

    void testDialogs() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        wings.dialog()
        wings.frame{ dialog() }
    }

    void testNodeCreation() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        def frame = wings.frame(){
            // 4 valid parameter combinations
            button()
            button('Text')
            button(text:'Label')
            button(text:'Label', 'Text')
        }
        shouldFail(){
            frame = wings.frame(){
                // invalid parameter
                button(new Date())
            }
        }
    }

    void testFormattedTextField() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        def dummy = new Date()
        def field = wings.formattedTextField(value:dummy)
        assert field.value == dummy
        assert field.formatter.class == SDateFormatter.class
        def dummyFormatter = new SDateFormatter()
        field = wings.formattedTextField(formatter:dummyFormatter)
        assert field.formatter.class == SDateFormatter.class
        field = wings.formattedTextField()
        field.value = 3
        assert field.formatter.class == SNumberFormatter.class
    }

    void testTabbedPane() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        wings.tabbedPane{
            button()
        }
    }

    void testComboBox() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        Object[] objects = ['a','b']
        def list = ['c', 'd', 'e']
        def vector = new Vector(['f', 'g', 'h', 'i'])
        assert wings.comboBox(items:objects).itemCount == 2
        assert wings.comboBox(items:list).itemCount == 3
        assert wings.comboBox(items:vector).itemCount == 4
    }

    void testMisplacedActionsAreIgnored() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        // labels don't support actions; should be ignored
        wings.label{
            action(id:'actionId', Name:'about', mnemonic:'A', closure:{x->x})
            map()
        }
        wings.panel{
            borderLayout{
                // layouts don't support actions, will be ignored
                action(id:'actionId')
            }
        }
    }

    void testBoxLayout() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        def message = shouldFail{
            wings.boxLayout()
        }
        assert message.contains('Must be nested inside a Container')
        // default is X_AXIS
        wings.panel(id:'panel'){
            boxLayout(id:'layout1')
        }
        // can also set explicit axis
        wings.frame(id:'frame'){
            boxLayout(id:'layout2', axis:SBoxLayout.Y_AXIS)
        }
    }

    void testBorderLayoutConstraints() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        wings.internalFrame(id:'frameId',
                border:SBorderFactory.createSTitledBorder(SBorderFactory.createLoweredSBevelBorder())) {
            wings.frameId.contentPane.layout = new SBorderLayout()
            panel(id:'panel1', constraints:SBorderLayout.NORTH)
            panel(id:'panel2', constraints:SBorderLayout.WEST)
            panel(id:'panel3', constraints:SBorderLayout.EAST)
            panel(id:'panel4', constraints:SBorderLayout.SOUTH)
            scrollPane(id:'scrollId', constraints:SBorderLayout.CENTER,
                border:SBorderFactory.createRaisedSBevelBorder()) {
               panel()
            }
        }
        assert wings.panel1.parent == wings.frameId.contentPane
        assert wings.panel2.parent == wings.frameId.contentPane
        assert wings.scrollId.parent == wings.frameId.contentPane
    }

    void testPropertyColumn() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        def msg = shouldFail{
            wings.propertyColumn()
        }
        assert msg.contains('propertyColumn must be a child of a tableModel')
        msg = shouldFail{
            wings.table{
                tableModel(){
                    propertyColumn()
                }
            }
        }
        assert msg.contains("Must specify a property for a propertyColumn"): \
            "Instead found message: " + msg
        wings.table{
            tableModel(){
                propertyColumn(header:'header', propertyName:'foo')
                propertyColumn(propertyName:'bar', type:String.class)
            }
        }
    }

    void testClosureColumn() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        def msg = shouldFail{
            wings.closureColumn()
        }
        assert msg.contains('closureColumn must be a child of a tableModel')
        msg = shouldFail{
            wings.table{
                tableModel(){
                    closureColumn()
                }
            }
        }
        assert msg.contains("Must specify 'read' Closure property for a closureColumn"): \
            "Instead found message: " + msg
        def closure = { x -> x }
        def table = wings.table{
            tableModel(){
                closureColumn(read:closure, write:closure, header:'header')
            }
            tableModel(model:new groovy.model.ValueHolder('foo')){
                closureColumn(read:closure, type:String.class)
            }
            tableModel(list:['a','b']){
                closureColumn(read:closure, type:String.class)
            }
        }

        assert table.columnModel.class.name == 'org.kordamp.groovy.wings.impl.STableColumnModelAdapter'
    }

    void testSetConstraints() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        wings.panel(layout:new SBorderLayout()) {
            label(text:'Name', constraints:SBorderLayout.CENTER)
        }
    }

    void testSetToolTipText() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        wings.panel(layout:new SBorderLayout()) {
            label(id:'labelId', text:'Name', toolTipText:'This is the name field')
        }

    }

    void testWidgetPassthroughConstraints() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        def foo = wings.button('North')
        def frame = wings.frame() {
            borderLayout()
            widget(foo, constraints: SBorderLayout.NORTH)
            // a failed test throws MissingPropertyException by now
        }
    }

    void testSeparators() {
        if (isHeadless()) return

        def wings = new WingSBuilder()
        wings.frame() {
            separator(id:"sep")
        }
        assert wings.sep instanceof SSeparator
    }

    void testLabelInButton() {
       if (isHeadless()) return

       def wings = new WingSBuilder()
       wings.frame() {
           button( id:"button", label: "label" )
       }
       assert wings.button.text == "label"
    }

    void testAutoForm1(){
       if (isHeadless()) return

       def wings = new WingSBuilder()
       def frame = wings.frame {
          panel(){
             button("A")
             button("B")
          }
       }
       def child = frame.contentPane.getComponent(0)
       assert child.class == SForm.class
    }

    void testAutoForm2(){
       // no form should be inserted
       if (isHeadless()) return

       def wings = new WingSBuilder()
       def frame = wings.frame {
          form(){
             button("A")
             button("B")
          }
       }
       def child = frame.contentPane.getComponent(0)
       assert child.class == SForm.class
    }

    void testAutoForm3(){
       // no form should be inserted
       if (isHeadless()) return

       def wings = new WingSBuilder()
       def frame = wings.frame {
          panel(){
             form(){
                button("A")
                button("B")
             }
          }
       }
       def child = frame.contentPane.getComponent(0)
       assert child.class == SPanel.class
    }

    void testAutoForm4(){
       // no form should be inserted
       if (isHeadless()) return

       def wings = new WingSBuilder()
       def frame = wings.frame(autoForm:false) {
          panel(){
             button("A")
             button("B")
          }
       }
       def child = frame.contentPane.getComponent(0)
       assert child.class == SPanel.class
    }

    void testAutoForm5(){
       // no form should be inserted
       if (isHeadless()) return

       def wings = new WingSBuilder()
       def panel = wings.panel() {
          button("A")
          button("B")
       }
       def child = panel.getComponent(0)
       assert child.class == SButton.class
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