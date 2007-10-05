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

package org.kordamp.groovy.swing.jide

import java.beans.PropertyChangeListener
import java.awt.Container
import javax.swing.JComboBox
import javax.swing.text.JTextComponent

import groovy.swing.*
import groovy.swing.factory.*
import org.kordamp.groovy.swing.jide.factory.*
import org.kordamp.groovy.swing.jide.impl.*
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jidesoft.dialog.*
import com.jidesoft.popup.*
import com.jidesoft.spinner.*
import com.jidesoft.swing.*

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class JideBuilder extends SwingBuilder {
   private Logger log = Logger.getLogger(getClass().getName())
   private Map jideWidgets = [:]
   private SwingBuilder parentBuilder
   private static Object constraintsCopy

   public JideBuilder( SwingBuilder parentBuilder = null ){
      super()
      this.parentBuilder = parentBuilder
      registerJideComponents()
   }

   public Object getProperty( String name ){
      Object widget = jideWidgets.get( name )
      return widget ? widget : super.getProperty( name )
   }

   public void registerSVGAlias( String alias, String path ){
      SVGIconFactory.registerSVGAlias( alias, path )
   }

   protected void handleWidgetAttributes( Object widget, Map attributes ){
      // first, short circuit
      if( attributes.isEmpty() || !widget ){
         return
      }

      if( widget instanceof JTextComponent ){
         if( attributes.remove("selectAll") ){
            SelectAllUtils.install( widget )
         }
      }

      // handle JIDE components
      if( widget instanceof ListSearchableWrapper ){
         setAttributes( widget.delegateWidget, attributes )
         return
      }

      if( widget instanceof ComboBoxSearchableWrapper ||
          widget instanceof TableSearchableWrapper ||
          widget instanceof TextComponentSearchableWrapper ||
          widget instanceof TreeSearchableWrapper ||
          widget instanceof FileIntelliHintsWrapper ||
          widget instanceof ListDataIntelliHintsWrapper ||
          widget instanceof AutoCompletionWrapper ){
         setWrapperAttributes( widget, attributes )
         setAttributes( widget.delegateWidget, attributes )
         return
      }

      if( widget instanceof SearchableBarWrapper ){
         setWrapperAttributes( widget, attributes )
         def searchable = widget.searchable
         if( searchable instanceof SearchableWrapper ){
            setAttributes( searchable.delegateWidget, attributes )
         }else{
            setAttributes( searchable, attributes )
         }
         return
      }

      if( widget instanceof Calculator ){
         JTextComponent textComponent = attributes.remove("textComponent")
         if( textComponent ){
            SelectAllUtils.install( textComponent )
            textComponent.setHorizontalAlignment(JTextField.TRAILING)
            widget.registerKeyboardActions(textComponent, JComponent.WHEN_FOCUSED)
            widget.addPropertyChangeListener(Calculator.PROPERTY_DISPLAY_TEXT,[
               propertyChange: { event ->
                  textComponent.text = "" + event.getNewValue()
               }] as PropertyChangeListener )
         }
      }

      // let super class handle the rest
      super.handleWidgetAttributes( widget, attributes )
   }

   protected void setParent( Object parent, Object child ){
      // handle JIDE components
      if( child instanceof JideMenu.PopupMenuCustomizer ){
         if( parent instanceof JideMenu ){ parent.setPopupMenuCustomizer( child ) }
         return
      }
      if( child instanceof AbstractDialogPage && parent instanceof MultiplePageDialog ){
         parent.pageList.append( child )
         return
      }
      if( child instanceof DialogBannerPanel && parent instanceof DefaultStandardDialog ){
         parent.bannerPanel = child
         return
      }
      if( child instanceof DialogContentPanel && parent instanceof DefaultStandardDialog ){
         parent.contentPanel = child
         return
      }
      if( child instanceof DialogButtonPanel && parent instanceof DefaultStandardDialog ){
         parent.buttonPanel = child
         return
      }
      if( child instanceof ResizableSVGIcon ){
         try{
            MetaClass mc = InvokerHelper.getInstance().getMetaClass( parent )
            mc.setProperty( parent, "icon", child )
            child.installSizeTracker( parent )
            if( parent.preferredSizeSet && !child.sizeSet ){
               child.setSize( parent.preferredSize )
            }
            return
         }catch( MissingPropertyException mpe ){
            // empty
         }
      }
      if( child instanceof SearchableBarWrapper && child.install ){
         child.installOnContainer( parent )
         return
      }

      // let super class handle the rest
      super.setParent( parent, child )
   }

   protected Object createNode( Object name, Map attributes, Object value ){
      String id = attributes.get("id")
      Object constraintsCopy = attributes.get("constraints")
      Object widget = super.createNode( name, attributes, value )

      if( widget instanceof ComboBoxSearchableWrapper ){
         return exposeDelegate( widget, id, widget.delegateWidget, "_comboBox" )
      }
      if( widget instanceof ListSearchableWrapper ){
         return exposeDelegate( widget, id, widget.delegateWidget, "_list" )
      }
      if( widget instanceof TableSearchableWrapper ){
         return exposeDelegate( widget, id, widget.delegateWidget, "_table" )
      }
      if( widget instanceof TextComponentSearchableWrapper ){
         return exposeDelegate( widget, id, widget.delegateWidget, "_textComponent" )
      }
      if( widget instanceof TreeSearchableWrapper ){
         return exposeDelegate( widget, id, widget.delegateWidget, "_tree" )
      }
      if( widget instanceof FileIntelliHintsWrapper ||
          widget instanceof ListDataIntelliHintsWrapper ){
         return exposeDelegate( widget, id, widget.delegateWidget, "_textComponent" )
      }
      if( widget instanceof AutoCompletionWrapper ){
         return exposeDelegate( widget, id, widget.delegateWidget,
               widget.delegateWidget instanceof JComboBox? "_comboBox":"_textComponent" )
      }

      if( id && parentBuilder ){
         parentBuilder.widgets.put( id, widget )
      }
      if( parentBuilder && parentBuilder.getCurrent() ){
         parentBuilder.@constraints = constraintsCopy
         parentBuilder.setParent( parentBuilder.getCurrent(), widget )
      }
      return widget
   }

   private void registerJideComponents() {
      registerFactory("animator", new AnimatorFactory())
      registerFactory("autoCompletion", new AutoCompletionFactory())
      registerFactory("autoCompletionComboBox", new AutoCompletionComboBoxFactory())
      registerFactory("autoResizingTextArea", new TextArgWidgetFactory(AutoResizingTextArea))
      registerBeanFactory("bannerPanel", BannerPanel)
      registerBeanFactory("buttonPanel", com.jidesoft.dialog.ButtonPanel)
      registerBeanFactory("calculator", Calculator)
      registerBeanFactory("checkBoxList", CheckBoxList)
      registerBeanFactory("checkBoxListWithSelectable", CheckBoxListWithSelectable)
      registerBeanFactory("checkBoxTree", CheckBoxTree)
      registerFactory("clickThroughLabel", new TextArgWidgetFactory(ClickThroughLabel))
      registerFactory("comboBoxSearchable", new ComboBoxSearchableFactory())
      registerBeanFactory("contentContainer", ContentContainer)
      registerBeanFactory("dateSpinner", DateSpinner)
      registerBeanFactory("folderChooser", FolderChooser)
      registerBeanFactory("gripper", Gripper)
      registerBeanFactory("headerBox", HeaderBox)
      registerFactory("jideButton", new RichActionWidgetFactory(JideButton))
      registerBeanFactory("jideBorderLayout", JideBorderLayout)
      registerFactory("jideBoxLayout", new JideBoxLayoutFactory())
      registerFactory("jideMenu", new JideMenuFactory())
      registerBeanFactory("popupMenuCustomizer", DefaultPopupMenuCustomizer)
      registerBeanFactory("jideOptionPane", JideOptionPane)
      registerBeanFactory("jidePopup", JidePopup)
      registerBeanFactory("jidePopupMenu", JidePopupMenu)
      registerBeanFactory("jideScrollPane", JideScrollPane)
      registerFactory("jideSplitButton", new JideSplitButtonFactory())
      registerBeanFactory("jideSplitPane", JideSplitPane)
      registerBeanFactory("jideTabbedPane", JideTabbedPane)
      registerFactory("jideToggleButton", new RichActionWidgetFactory(JideToggleButton))
      registerFactory("jideToggleSplitButton", new JideToggleSplitButtonFactory())
      registerFactory("labeledTextField", new LabeledTextFieldFactory())
      registerFactory("listSearchable", new ListSearchableFactory())
      registerFactory("multilineLabel", new TextArgWidgetFactory(MultilineLabel))
      registerFactory("multiplePageDialog", new MultiplePageDialogFactory())
      registerBeanFactory("multiplePageDialogPane", MultiplePageDialogPane)
      registerFactory("nullButton", new RichActionWidgetFactory(NullButton))
      registerFactory("nullCheckBox", new RichActionWidgetFactory(NullCheckBox))
      registerFactory("nullJideButton", new RichActionWidgetFactory(NullJideButton))
      registerFactory("nullLabel", new TextArgWidgetFactory(NullLabel))
      registerBeanFactory("nullPanel", NullPanel)
      registerFactory("nullRadioButton", new RichActionWidgetFactory(NullRadioButton))
      registerFactory("nullTristateCheckBox", new NullTristateCheckBoxFactory())
      registerBeanFactory("paintPanel", PaintPanel)
      registerBeanFactory("pointSpinner", PointSpinner)
      registerBeanFactory("rangeSlider", RangeSlider)
      registerFactory("resizableDialog", new ResizableDialogFactory())
      registerFactory("resizableFrame", new ResizableFrameFactory())
      registerBeanFactory("resizablePanel", ResizablePanel)
      registerFactory("resizableWindow", new ResizableWindowFactory())
      registerFactory("searchableBar", new SearchableBarFactory())
      registerBeanFactory("simpleScrollPane", SimpleScrollPane)
      registerBeanFactory("splitButtonGroup", SplitButtonGroup)
      registerFactory("styledLabel", new TextArgWidgetFactory(StyledLabel))
      registerFactory("tableSearchable", new TableSearchableFactory())
      registerFactory("textComponentSearchable", new TextComponentSearchableFactory())
      registerFactory("treeSearchable", new TreeSearchableFactory())
      registerFactory("tristateCheckBox", new TristateCheckBoxFactory())

      // hints
      registerFactory("fileIntelliHints", new FileIntelliHintsFactory())
      registerFactory("listDataIntelliHints", new ListDataIntelliHintsFactory())

      registerBeanFactory("dialogPage", DefaultDialogPage)
      registerFactory("standardDialog", new StandardDialogFactory())
      registerBeanFactory("dialogBannerPanel", DialogBannerPanel)
      registerBeanFactory("dialogContentPanel", DialogContentPanel)
      registerBeanFactory("dialogButtonPanel", DialogButtonPanel)

      // JideSwingUtilities
      registerFactory("left", new JideSwingUtilitiesPanelFactory("left"))
      registerFactory("right", new JideSwingUtilitiesPanelFactory("right"))
      registerFactory("top", new JideSwingUtilitiesPanelFactory("top"))
      registerFactory("bottom", new JideSwingUtilitiesPanelFactory("bottom"))
      registerFactory("center", new JideSwingUtilitiesPanelFactory("center"))

      // TODO
      // JideScrollPaneLayout ?
      // PopupWindow
      // StyleRange
      // handle 'panes' in JideSplitPane

      /* ==================================== */
      registerFactory("svgIcon", new SVGIconFactory())

      // Overlayables
      registerFactory("overlayCheckBox", new RichActionWidgetFactory(OverlayCheckBox))
      registerFactory("overlayComboBox", new OverlayComboBoxFactory())
      registerFactory("overlayRadioButton", new RichActionWidgetFactory(OverlayRadioButton))
      registerFactory("overlayTextArea", new TextArgWidgetFactory(OverlayTextArea))
      registerFactory("overlayTextField", new TextArgWidgetFactory(OverlayTextField))
   }

   private void setAttributes( Object widget, Map attributes ){
      if( widget instanceof JTextComponent ){
         if( attributes.remove("selectAll") ){
            SelectAllUtils.install( widget )
         }
      }

      InvokerHelper.setProperties( widget, attributes )
   }

   private void setWrapperAttributes( Object widget, Map attributes ){
      MetaClass mc = InvokerHelper.getInstance().getMetaClass( widget )
      attributes.each { name, value ->
         try{
            mc.setProperty( widget, name, value )
         }catch( MissingPropertyException mpe ){
            // empty
         }
      }
   }

   protected Object exposeDelegate( widget, id, delegateWidget, suffix ){
      if( id ){
         jideWidgets.put( id + suffix, delegateWidget )
         if( parentBuilder ){
            parentBuilder.widgets.put( id, widget )
            parentBuilder.widgets.put( id + suffix, delegateWidget )
            if( parentBuilder.getCurrent() ){
               parentBuilder.@constraints = constraintsCopy
               parentBuilder.setParent( parentBuilder.getCurrent(), widget )
            }
         }
      }
      return delegateWidget
   }
}