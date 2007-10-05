/*
 * Copyright 2007 the original author or authors.
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

package org.kordamp.groovy.swing.jide

import java.awt.Component
import javax.swing.*
import org.kordamp.groovy.swing.jide.JideBuilder
import org.kordamp.groovy.swing.jide.impl.*
import com.jidesoft.dialog.*
import com.jidesoft.popup.*
import com.jidesoft.spinner.*
import com.jidesoft.swing.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ComponentsTest extends GroovyTestCase {

   private boolean isHeadless() {
      try {
         new JFrame("testing")
         return false
      } catch (java.awt.HeadlessException he) {
         return true
      }
   }

   void testNamedWidgetCreation() {
      if (isHeadless()) return

      def topLevelWidgets = [
         resizableDialog: [ResizableDialog, true],
         resizableFrame: [ResizableFrame, true],
         resizableWindow: [ResizableWindow, false],
         folderChooser: [FolderChooser, false],
         jideOptionPane: [JideOptionPane, false],
         multiplePageDialog: [MultiplePageDialog, true],
         standardDialog: [DefaultStandardDialog, true]
      ]
      def jide = new JideBuilder()
      topLevelWidgets.each{ name, widgetInfo ->
         if (widgetInfo[1])
            jide."$name"(id:"${name}Id".toString(), title:"This is my $name")
         else
            jide."$name"(id:"${name}Id".toString())
         def widget = jide."${name}Id"
         assert widget.class == widgetInfo[0]
         if (widgetInfo[1]) assert widget.title == "This is my $name"
      }
   }

   void testWidgetCreation() {
      if (isHeadless()) return

      def widgets = [
         autoCompletionComboBox: AutoCompletionComboBox,
         autoResizingTextArea: AutoResizingTextArea,
         bannerPanel: BannerPanel,
         buttonPanel: ButtonPanel,
         calculator: Calculator,
         checkBoxList: CheckBoxList,
         checkBoxListWithSelectable: CheckBoxListWithSelectable,
         checkBoxTree: CheckBoxTree,
         clickThroughLabel: ClickThroughLabel,
         contentContainer: ContentContainer,
         dateSpinner: DateSpinner,
         dialogBannerPanel: DialogBannerPanel,
         dialogButtonPanel: DialogButtonPanel,
         dialogContentPanel: DialogContentPanel,
         dialogPage: DefaultDialogPage,
         gripper: Gripper,
         headerBox: HeaderBox,
         jideButton: JideButton,
         jideMenu: JideMenu,
         jidePopup: JidePopup,
         jidePopupMenu: JidePopupMenu,
         jideScrollPane: JideScrollPane,
         jideSplitButton: JideSplitButton,
         jideSplitPane: JideSplitPane,
         jideTabbedPane: JideTabbedPane,
         jideToggleButton: JideToggleButton,
         jideToggleSplitButton: JideToggleSplitButton,
         labeledTextField: LabeledTextField,
         multilineLabel: MultilineLabel,
         multiplePageDialogPane: MultiplePageDialogPane,
         nullButton: NullButton,
         nullCheckBox: NullCheckBox,
         nullJideButton: NullJideButton,
         nullLabel: NullLabel,
         nullPanel: NullPanel,
         nullRadioButton: NullRadioButton,
         nullTristateCheckBox: NullTristateCheckBox,
         paintPanel: PaintPanel,
         pointSpinner: PointSpinner,
         rangeSlider: RangeSlider,
         simpleScrollPane: SimpleScrollPane,
         styledLabel: StyledLabel,
         tristateCheckBox: TristateCheckBox,
         overlayCheckBox: OverlayCheckBox,
         overlayComboBox: OverlayComboBox,
         overlayRadioButton: OverlayRadioButton,
         overlayTextField: OverlayTextField,
         overlayTextArea: OverlayTextArea,
      ]
      def jide = new JideBuilder()
      widgets.each{ name, expectedClass ->
         def frame = jide.frame(){
            "$name"(id:"${name}Id".toString())
         }
         assert jide."${name}Id".class == expectedClass
      }
   }

   void testComboBoxSearchable(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         comboBoxSearchable( id: "searchable", items: [1,2,3] )
      }
      assertNotNull jide.searchable
      assertNotNull jide.searchable_comboBox
      assert jide.searchable_comboBox.class == JComboBox
   }

   void testComboBoxSearchable_withOverlayableProperty(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         comboBoxSearchable( id: "searchable", items: [1,2,3], overlayable: true )
      }
      assertNotNull jide.searchable
      assertNotNull jide.searchable_comboBox
      assert jide.searchable_comboBox.class == OverlayComboBox
   }

   void testComboBoxSearchableWithComboBox(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         comboBox( id: "combo", items: [1,2,3] )
         comboBoxSearchable( id: "searchable", comboBox: jide.combo )
      }
      assertNotNull jide.combo
      assertNotNull jide.searchable
      assert jide.combo == jide.searchable_comboBox
   }

   void testListSearchable(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         listSearchable( id: "searchable", listData: [1,2,3] )
      }
      assertNotNull jide.searchable
      assertNotNull jide.searchable_list
      assert jide.searchable_list.class == JList
   }

   void testListSearchableWithList(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         list( id: "list", listData: [1,2,3] )
         listSearchable( id: "searchable", list: jide.list )
      }
      assertNotNull jide.list
      assertNotNull jide.searchable
      assert jide.list == jide.searchable_list
   }

   void testTextComponentSearchable(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textComponentSearchable( id: "searchable", text: "text" )
      }
      assertNotNull jide.searchable
      assertNotNull jide.searchable_textComponent
      assert jide.searchable_textComponent.class == JTextField
      assert jide.searchable_textComponent.text == "text"
   }

   void testTextComponentSearchable_withOverlayableProperty(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textComponentSearchable( id: "searchable", text: "text", overlayable: true )
      }
      assertNotNull jide.searchable
      assertNotNull jide.searchable_textComponent
      assert jide.searchable_textComponent.class == OverlayTextField
      assert jide.searchable_textComponent.text == "text"
   }

   void testTextComponentSearchableWithTextComponent(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "textComponent", text: "text" )
         textComponentSearchable( id: "searchable", textComponent: jide.textComponent )
      }
      assertNotNull jide.textComponent
      assertNotNull jide.searchable
      assert jide.textComponent == jide.searchable_textComponent
   }

   void testSearchableBar(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         comboBoxSearchable( id: "searchable", items: [1,2,3] )
         searchableBar( id: "searchBar", searchable: jide.searchable )
      }
      assertNotNull jide.searchable
      assertNotNull jide.searchBar
      assert jide.searchBar.searchable == jide.searchable
   }

   void testFileIntelliHintsWithTextComponent(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "textComponent", text: "text" )
         fileIntelliHints( id: "hints", textComponent: jide.textComponent, folderOnly: true )
      }
      assertNotNull jide.textComponent
      assertNotNull jide.hints
      assert jide.textComponent == jide.hints_textComponent
      assertTrue jide.hints.showFullPath
      assertTrue jide.hints.folderOnly
   }

   void testListDataIntelliHintsWithTextComponent(){
      if (isHeadless()) return

      def completionList = [1,2,3]
      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "textComponent", text: "text" )
         listDataIntelliHints( id: "hints", textComponent: jide.textComponent, completionList: completionList )
      }
      assertNotNull jide.textComponent
      assertNotNull jide.hints
      assert jide.textComponent == jide.hints_textComponent
      assert jide.hints.completionList == completionList
   }

   void testJideMenuWithAutomaticPopupMenuCustomizer(){
      def customize = { m ->
         m.add("1")
         m.add("2")
         m.add("3")
      }
      def jide = new JideBuilder()
      def menu = jide.jideMenu( id: "menu", customize: customize )
      assert menu.popupMenuCustomizer.class == DefaultPopupMenuCustomizer
      assert menu.popupMenuCustomizer.closure == customize
   }

   void testJideSplitButtonWithAutomaticPopupMenuCustomizer(){
      def customize = { m ->
         m.add("1")
         m.add("2")
         m.add("3")
      }
      def jide = new JideBuilder()
      def menu = jide.jideSplitButton( id: "menu", customize: customize )
      assert menu.popupMenuCustomizer.class == DefaultPopupMenuCustomizer
      assert menu.popupMenuCustomizer.closure == customize
   }

   void testJideToggleSplitButtonWithAutomaticPopupMenuCustomizer(){
      def customize = { m ->
         m.add("1")
         m.add("2")
         m.add("3")
      }
      def jide = new JideBuilder()
      def menu = jide.jideToggleSplitButton( id: "menu", customize: customize )
      assert menu.popupMenuCustomizer.class == DefaultPopupMenuCustomizer
      assert menu.popupMenuCustomizer.closure == customize
   }

   void testAutoCompletion_TextComponentAndList(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         autoCompletion( id: "auto", list: ["a","b","c"], text: "text" )
      }
      assertNotNull jide.auto
      assertNotNull jide.auto_textComponent
      assert jide.auto_textComponent.class == JTextField
      assert jide.auto_textComponent.text == "a"
   }

   void testAutoCompletion_TextComponentAndList_withOverlayableProperty(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         autoCompletion( id: "auto", list: ["a","b","c"], text: "text", overlayable: true )
      }
      assertNotNull jide.auto
      assertNotNull jide.auto_textComponent
      assert jide.auto_textComponent.class == OverlayTextField
      assert jide.auto_textComponent.text == "a"
   }

   void testAutoCompletion_TextComponentAndSearchable(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         listSearchable( id: "searchable", listData: [1,2,3] )
         autoCompletion( id: "auto", searchable: jide.searchable, text: "3" )
      }
      assertNotNull jide.auto
      assertNotNull jide.auto_textComponent
      assert jide.auto.searchable == jide.searchable
      assert jide.auto_textComponent.class == JTextField
      assert jide.auto_textComponent.text == "3"
   }

   void testAutoCompletionWithTextComponentAndList(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "textComponent", text: "text" )
         autoCompletion( id: "auto", textComponent: jide.textComponent, list: ["a","b","c"] )
      }
      assertNotNull jide.auto
      assertNotNull jide.auto_textComponent
      assert jide.auto_textComponent == jide.textComponent
      assert jide.auto_textComponent.text == "a"
   }

   void testAutoCompletionWithTextComponentAndSearchable(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "textComponent", text: "text" )
         listSearchable( id: "searchable", listData: [1,2,3] )
         autoCompletion( id: "auto", searchable: jide.searchable, textComponent: jide.textComponent )
      }
      assertNotNull jide.auto
      assertNotNull jide.auto_textComponent
      assert jide.auto.searchable == jide.searchable
      assert jide.auto_textComponent == jide.textComponent
      assert jide.auto_textComponent.text == "1"
   }

   /*
   void testAnimator(){
      def count = 0
      def animatorListener = [
         animationStarts: { component ->
            count = 0
         },
         animationFrame: { component, totalStep, step ->
            count += 1
         },
         animationEnds: { component ->
         }
      ] as AnimatorListener

      def jide = new JideBuilder()
      jide.panel(){
         button(id: "button")
         animator(id: "animator", source: jide.button,
                 totalSteps: 9, animatorListener: animatorListener )
      }
      jide.animator.start()
      Thread.sleep( 300 )
      assert count == 10
   }
   */
}