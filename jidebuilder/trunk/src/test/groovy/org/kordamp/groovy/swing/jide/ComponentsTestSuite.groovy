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
import com.jidesoft.hints.*
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
         comboBoxSearchable( id: "comboBox", items: [1,2,3] )
      }
      assertNotNull jide.comboBox
      assertNotNull jide.comboBox_searchable
      assert jide.comboBox.class == JComboBox
      assert jide.comboBox_searchable.class == ComboBoxSearchable
   }

   void testComboBoxSearchable_withOverlayableProperty(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         comboBoxSearchable( id: "comboBox", items: [1,2,3], overlayable: true )
      }
      assertNotNull jide.comboBox
      assertNotNull jide.comboBox_searchable
      assert jide.comboBox.class == OverlayComboBox
      assert jide.comboBox_searchable.class == ComboBoxSearchable
   }

   void testComboBoxSearchableWithComboBox(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         comboBox( id: "combo", items: [1,2,3] )
         comboBoxSearchable( id: "comboBox", comboBox: combo )
      }
      assertNotNull jide.comboBox
      assertNotNull jide.comboBox_searchable
      assert jide.comboBox.class == JComboBox
      assert jide.comboBox == jide.combo
      assert jide.comboBox_searchable.class == ComboBoxSearchable
   }

   void testListSearchable(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         listSearchable( id: "list", listData: [1,2,3] )
      }
      assertNotNull jide.list
      assertNotNull jide.list_searchable
      assert jide.list.class == JList
      assert jide.list_searchable.class == ListSearchable
   }

   void testListSearchableWithList(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         list( id: "srclist", listData: [1,2,3] as Object[] )
         listSearchable( id: "list", list: srclist )
      }
      assertNotNull jide.list
      assertNotNull jide.list_searchable
      assert jide.list.class == JList
      assert jide.list == jide.srclist
      assert jide.list_searchable.class == ListSearchable
   }

   void testTextComponentSearchable(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textComponentSearchable( id: "text", text: "text" )
      }
      assertNotNull jide.text
      assertNotNull jide.text_searchable
      assert jide.text.class == JTextField
      assert jide.text.text == "text"
      assert jide.text_searchable.class == TextComponentSearchable
   }

   void testTextComponentSearchable_withOverlayableProperty(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textComponentSearchable( id: "text", text: "text", overlayable: true )
      }
      assertNotNull jide.text
      assertNotNull jide.text_searchable
      assert jide.text.class == OverlayTextField
      assert jide.text.text == "text"
      assert jide.text_searchable.class == TextComponentSearchable
   }

   void testTextComponentSearchableWithTextComponent(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "srctext", text: "text" )
         textComponentSearchable( id: "text", textComponent: srctext )
      }
      assertNotNull jide.text
      assertNotNull jide.text_searchable
      assert jide.text.class == JTextField
      assert jide.text.text == "text"
      assert jide.text == jide.srctext
      assert jide.text_searchable.class == TextComponentSearchable
   }

   void testSearchableBar(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         comboBoxSearchable( id: "combo", items: [1,2,3] )
         searchableBar( id: "searchBar", searchable: combo_searchable )
      }
      assertNotNull jide.combo_searchable
      assertNotNull jide.searchBar
      assert jide.searchBar.searchable == jide.combo_searchable
   }

   void testFileIntelliHintsWithTextComponent(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "textComponent", text: "text" )
         fileIntelliHints( id: "hints", textComponent: textComponent, folderOnly: true )
      }
      assertNotNull jide.textComponent
      assertNotNull jide.hints
      assert jide.hints.class == FileIntelliHints
      assert jide.hints.textComponent == jide.textComponent
      assertTrue jide.hints.showFullPath
      assertTrue jide.hints.folderOnly
   }

   void testListDataIntelliHintsWithTextComponent(){
      if (isHeadless()) return

      def completionList = [1,2,3]
      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "textComponent", text: "text" )
         listDataIntelliHints( id: "hints", textComponent: textComponent, completionList: completionList )
      }
      assertNotNull jide.textComponent
      assertNotNull jide.hints
      assert jide.hints.class == ListDataIntelliHints
      assert jide.hints.textComponent == jide.textComponent
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
      assertNotNull jide.auto_autocompletion
      assert jide.auto.class == JTextField
      assert jide.auto.text == "a"
   }

   void testAutoCompletion_TextComponentAndList_withOverlayableProperty(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         autoCompletion( id: "auto", list: ["a","b","c"], text: "text", overlayable: true )
      }
      assertNotNull jide.auto
      assertNotNull jide.auto_autocompletion
      assert jide.auto.class == OverlayTextField
      assert jide.auto.text == "a"
   }

   void testAutoCompletion_TextComponentAndSearchable(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         listSearchable( id: "list", listData: [1,2,3] )
         autoCompletion( id: "auto", searchable: list_searchable, text: "3" )
      }
      assertNotNull jide.auto
      assertNotNull jide.auto_autocompletion
      assert jide.auto_autocompletion.searchable == jide.list_searchable
      assert jide.auto.class == JTextField
      assert jide.auto.text == "3"
   }

   void testAutoCompletionWithTextComponentAndList(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "textComponent", text: "text" )
         autoCompletion( id: "auto", textComponent: textComponent, list: ["a","b","c"] )
      }
      assertNotNull jide.auto
      assertNotNull jide.auto_autocompletion
      assert jide.auto == jide.textComponent
      assert jide.auto.text == "a"
   }

   void testAutoCompletionWithTextComponentAndSearchable(){
      if (isHeadless()) return

      def jide = new JideBuilder()
      jide.panel(){
         textField( id: "textComponent", text: "text" )
         listSearchable( id: "list", listData: [1,2,3] )
         autoCompletion( id: "auto", searchable: list_searchable, textComponent: textComponent )
      }
      assertNotNull jide.auto
      assertNotNull jide.auto_autocompletion
      assert jide.auto_autocompletion.searchable == jide.list_searchable
      assert jide.auto == jide.textComponent
      assert jide.auto.text == "1"
   }

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
         animator(id: "animator", source: button,
                 totalSteps: 9, animatorListener: animatorListener )
      }
      jide.animator.start()
      Thread.sleep( 300 )
      assert count == 10
   }
}