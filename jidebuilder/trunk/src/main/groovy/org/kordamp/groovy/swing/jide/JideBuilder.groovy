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
 * limitations under the License.
 */

package org.kordamp.groovy.swing.jide

import java.beans.PropertyChangeListener
import java.awt.Container
import javax.swing.JComponent
import javax.swing.JComboBox
import javax.swing.JTextField
import javax.swing.text.JTextComponent

import groovy.swing.SwingBuilder
import groovy.swing.factory.*
import org.kordamp.groovy.swing.jide.factory.*
import org.kordamp.groovy.swing.jide.impl.*
import java.util.logging.Level
import java.util.logging.Logger

import com.jidesoft.dialog.*
import com.jidesoft.popup.*
import com.jidesoft.spinner.*
import com.jidesoft.swing.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class JideBuilder extends SwingBuilder {
   private Logger log = Logger.getLogger(getClass().getName())

   public JideBuilder( boolean init = true ) {
      super( init )
   }

   public void registerSVGAlias( String alias, String path ) {
      SVGIconFactory.registerSVGAlias( alias, path )
   }

   def registerJideSupportNodes() {
      addAttributeDelegate(JideBuilder.&selectAllAttributeDelegate)
      registerFactory("popupMenuCustomizer", new PopupMenuCustomizerFactory())
      registerBeanFactory("splitButtonGroup", SplitButtonGroup)
   }

   def registerJideWidgets() {
      registerBeanFactory("calculator", Calculator)
      registerBeanFactory("checkBoxList", CheckBoxList)
      registerBeanFactory("checkBoxTree", CheckBoxTree)
      registerBeanFactory("dateSpinner", DateSpinner)
      registerBeanFactory("folderChooser", FolderChooser)
      registerBeanFactory("gripper", Gripper)
      registerBeanFactory("pointSpinner", PointSpinner)
      registerBeanFactory("rangeSlider", RangeSlider)

      // TODO
      // JideScrollPaneLayout ?
      // PopupWindow
      // StyleRange
      // handle 'panes' in JideSplitPane

      /* ==================================== */
      registerFactory("svgIcon", new SVGIconFactory())

      // delegates
      addAttributeDelegate(JideBuilder.&calculatorAttributeDelegate)
   }

   def registerJideMenuWidgets() {
      registerFactory("jideMenu", new JideMenuFactory())
      registerBeanFactory("jidePopup", JidePopup)
      registerBeanFactory("jidePopupMenu", JidePopupMenu)
   }

   def registerJideAutocompletionWidgets() {
      registerFactory("animator", new AnimatorFactory())
      registerFactory("autoCompletion", new AutoCompletionFactory())
      registerFactory("autoCompletionComboBox", new AutoCompletionComboBoxFactory())
   }

   def registerJideActionButtonWidgets() {
      registerFactory("jideButton", new RichActionWidgetFactory(JideButton))
      registerFactory("jideSplitButton", new JideSplitButtonFactory())
      registerFactory("jideToggleButton", new RichActionWidgetFactory(JideToggleButton))
      registerFactory("jideToggleSplitButton", new JideToggleSplitButtonFactory())
      registerFactory("nullButton", new RichActionWidgetFactory(NullButton))
      registerFactory("nullCheckBox", new RichActionWidgetFactory(NullCheckBox))
      registerFactory("nullJideButton", new RichActionWidgetFactory(NullJideButton))
      registerFactory("nullRadioButton", new RichActionWidgetFactory(NullRadioButton))
      registerFactory("nullTristateCheckBox", new NullTristateCheckBoxFactory())
      registerFactory("tristateCheckBox", new TristateCheckBoxFactory())
   }

   def registerJideTextWidgets() {
      registerFactory("autoResizingTextArea", new TextArgWidgetFactory(AutoResizingTextArea))
      registerFactory("clickThroughLabel", new TextArgWidgetFactory(ClickThroughLabel))
      registerFactory("labeledTextField", new LabeledTextFieldFactory())
      registerFactory("multilineLabel", new TextArgWidgetFactory(MultilineLabel))
      registerFactory("nullLabel", new TextArgWidgetFactory(NullLabel))
      registerFactory("tableSearchable", new TableSearchableFactory())
      registerFactory("textComponentSearchable", new TextComponentSearchableFactory())
      registerFactory("styledLabel", new TextArgWidgetFactory(StyledLabel))
   }

   def registerJideOverlayableWidgets() {
      registerFactory("overlayCheckBox", new RichActionWidgetFactory(OverlayCheckBox))
      registerFactory("overlayComboBox", new OverlayComboBoxFactory())
      registerFactory("overlayRadioButton", new RichActionWidgetFactory(OverlayRadioButton))
      registerFactory("overlayTextArea", new TextArgWidgetFactory(OverlayTextArea))
      registerFactory("overlayTextField", new TextArgWidgetFactory(OverlayTextField))
   }

   def registerJideSearchableWidgets() {
      registerBeanFactory("checkBoxListWithSelectable", CheckBoxListWithSelectable)
      registerFactory("comboBoxSearchable", new ComboBoxSearchableFactory())
      registerFactory("listSearchable", new ListSearchableFactory())
      registerFactory("treeSearchable", new TreeSearchableFactory())
      registerFactory("searchableBar", new SearchableBarFactory())
   }

   def registerJideContainers() {
      registerBeanFactory("bannerPanel", BannerPanel)
      registerBeanFactory("buttonPanel", com.jidesoft.dialog.ButtonPanel)
      registerBeanFactory("contentContainer", ContentContainer)
      registerBeanFactory("jideOptionPane", JideOptionPane)
      registerBeanFactory("jideScrollPane", JideScrollPane)
      registerBeanFactory("jideSplitPane", JideSplitPane)
      registerBeanFactory("jideTabbedPane", JideTabbedPane)
      registerBeanFactory("nullPanel", NullPanel)
      registerBeanFactory("paintPanel", PaintPanel)
      registerBeanFactory("resizablePanel", ResizablePanel)
      registerBeanFactory("simpleScrollPane", SimpleScrollPane)
   }

   def registerJideWindows() {
      registerFactory("resizableDialog", new ResizableDialogFactory())
      registerFactory("resizableFrame", new ResizableFrameFactory())
      registerFactory("resizableWindow", new ResizableWindowFactory())

      registerFactory("multiplePageDialog", new MultiplePageDialogFactory())
      registerBeanFactory("multiplePageDialogPane", MultiplePageDialogPane)
      registerBeanFactory("dialogPage", DefaultDialogPage)
      registerFactory("standardDialog", new StandardDialogFactory())
      registerFactory("dialogBannerPanel", new StandardDialogPaneFactory(DialogBannerPanel))
      registerFactory("dialogContentPanel", new StandardDialogPaneFactory(DialogContentPanel))
      registerFactory("dialogButtonPanel", new StandardDialogPaneFactory(DialogButtonPanel))
   }

   def registerJideIntelliHints() {
      registerFactory("fileIntelliHints", new FileIntelliHintsFactory())
      registerFactory("listDataIntelliHints", new ListDataIntelliHintsFactory())
   }

   def registerJideLayouts() {
      registerBeanFactory("headerBox", HeaderBox)
      registerBeanFactory("jideBorderLayout", JideBorderLayout)
      registerFactory("jideBoxLayout", new JideBoxLayoutFactory())

      // JideSwingUtilities
      registerFactory("left", new JideSwingUtilitiesPanelFactory("left"))
      registerFactory("right", new JideSwingUtilitiesPanelFactory("right"))
      registerFactory("top", new JideSwingUtilitiesPanelFactory("top"))
      registerFactory("bottom", new JideSwingUtilitiesPanelFactory("bottom"))
      registerFactory("center", new JideSwingUtilitiesPanelFactory("center"))
   }

   public static selectAllAttributeDelegate( def builder, def node, def attributes ) {
      if( node instanceof JTextComponent && attributes.remove("selectAll") ){
         SelectAllUtils.install( node )
      }
   }

   public static calculatorAttributeDelegate( def builder, def node, def attributes ) {
      if( node instanceof Calculator ){
         JTextComponent textComponent = attributes.remove("textComponent")
         if( textComponent ){
            SelectAllUtils.install( textComponent )
            textComponent.setHorizontalAlignment(JTextField.TRAILING)
            node.registerKeyboardActions(textComponent, JComponent.WHEN_FOCUSED)
            node.addPropertyChangeListener(Calculator.PROPERTY_DISPLAY_TEXT,[
               propertyChange: { event ->
                  textComponent.text = "" + event.getNewValue()
               }] as PropertyChangeListener )
         }
      }
   }
/*
   public static childWidgetAttributeDelegate( Object node, Map attributes ) {
      def factory = getCurrentFactory()
      if( factory instanceof DelegatingJideFactory ){
         if( factory.onHandleNodeAttributes( this, node, attributes ) ){
            super.handleNodeAttributes( node, attributes )
         }
      }else{
         super.handleNodeAttributes( node, attributes )
      }
   }
*/
}