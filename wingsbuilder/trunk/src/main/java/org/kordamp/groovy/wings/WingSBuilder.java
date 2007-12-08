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

package org.kordamp.groovy.wings;

import groovy.swing.factory.ActionFactory;
import groovy.swing.factory.ClosureColumnFactory;
import groovy.swing.factory.CollectionFactory;
import groovy.swing.factory.MapFactory;
import groovy.swing.factory.PropertyColumnFactory;
import groovy.swing.factory.TableModelFactory;
import groovy.util.FactoryBuilderSupport;

import java.awt.GridBagConstraints;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

import org.kordamp.groovy.wings.factory.AbstractWingSFactory;
import org.kordamp.groovy.wings.factory.SBevelBorderFactory;
import org.kordamp.groovy.wings.factory.SBoxLayoutFactory;
import org.kordamp.groovy.wings.factory.SComboBoxFactory;
import org.kordamp.groovy.wings.factory.SDialogFactory;
import org.kordamp.groovy.wings.factory.SDownloadButtontFactory;
import org.kordamp.groovy.wings.factory.SEmptyBorderFactory;
import org.kordamp.groovy.wings.factory.SEtchedBorderFactory;
import org.kordamp.groovy.wings.factory.SFileIconFactory;
import org.kordamp.groovy.wings.factory.SFormFactory;
import org.kordamp.groovy.wings.factory.SFormattedTextFactory;
import org.kordamp.groovy.wings.factory.SFrameFactory;
import org.kordamp.groovy.wings.factory.SImageIconFactory;
import org.kordamp.groovy.wings.factory.SLineBorderFactory;
import org.kordamp.groovy.wings.factory.SPageScrollerFactory;
import org.kordamp.groovy.wings.factory.SPopupFactory;
import org.kordamp.groovy.wings.factory.SResourceIconFactory;
import org.kordamp.groovy.wings.factory.SRichActionWidgetFactory;
import org.kordamp.groovy.wings.factory.SRootLayoutFactory;
import org.kordamp.groovy.wings.factory.SSpacerFactory;
import org.kordamp.groovy.wings.factory.STextArgWidgetFactory;
import org.kordamp.groovy.wings.factory.SWidgetFactory;
import org.wings.SAnchor;
import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SButtonGroup;
import org.wings.SByteArrayIcon;
import org.wings.SCardLayout;
import org.wings.SCheckBox;
import org.wings.SComponent;
import org.wings.SContainer;
import org.wings.SDefaultBoundedRangeModel;
import org.wings.SDesktopPane;
import org.wings.SFileChooser;
import org.wings.SFlowDownLayout;
import org.wings.SFlowLayout;
import org.wings.SGridBagLayout;
import org.wings.SGridLayout;
import org.wings.SInternalFrame;
import org.wings.SLabel;
import org.wings.SList;
import org.wings.SMenu;
import org.wings.SMenuBar;
import org.wings.SMenuItem;
import org.wings.SNullLayout;
import org.wings.SOptionPane;
import org.wings.SPagingBoundedRangeModel;
import org.wings.SPanel;
import org.wings.SPasswordField;
import org.wings.SPopupMenu;
import org.wings.SProgressBar;
import org.wings.SRadioButton;
import org.wings.SRawTextComponent;
import org.wings.SRootContainer;
import org.wings.SScrollBar;
import org.wings.SScrollPane;
import org.wings.SSeparator;
import org.wings.SSpinner;
import org.wings.STabbedPane;
import org.wings.STable;
import org.wings.STemplateLayout;
import org.wings.STextArea;
import org.wings.STextField;
import org.wings.SToggleButton;
import org.wings.SToolBar;
import org.wings.STree;
import org.wings.border.SBevelBorder;
import org.wings.border.SEtchedBorder;
import org.wings.border.SLineBorder;
import org.wings.table.STableColumn;

/**
 * A helper class for creating WingS widgets using GroovyMarkup.<br>
 * Based on the original SwingBuilder.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class WingSBuilder extends FactoryBuilderSupport {
   public static final String CONSTRAINTS = "_CONSTRAINTS_";
   public static final String WIDGET_ID = "_WIDGET_ID_";
   public static final String Z_INDEX = "_Z_INDEX_";

   public static SContainer getLayoutTarget( SContainer parent ) {
      if( parent instanceof SRootContainer ){
         SRootContainer rc = (SRootContainer) parent;
         parent = rc.getContentPane();
      }
      return parent;
   }

   private boolean allowMissingProperties = false;
   private LinkedList containingWindows = new LinkedList();

   public WingSBuilder() {
      this( false );
   }

   public WingSBuilder( boolean allowMissingProperties ) {
      this.allowMissingProperties = allowMissingProperties;
      registerWidgets();
   }

   public Object getConstraints() {
      return getContext().get( CONSTRAINTS );
   }

   public LinkedList getContainingWindows() {
      return containingWindows;
   }

   public Integer getZIndex() {
      return (Integer) getContext().get( Z_INDEX );
   }

   public boolean isAllowMissingProperties() {
      return allowMissingProperties;
   }

   public void registerBeanFactory( String theName, final Class beanClass ) {
      registerFactory( theName, new AbstractWingSFactory(){
         public Object doNewInstance( WingSBuilder builder, Object name, Object value,
               Map properties ) throws InstantiationException, IllegalAccessException {
            if( checkValueIsTypeNotString( value, name, beanClass ) ){
               return value;
            }else{
               return beanClass.newInstance();
            }
         }
      } );
   }

   protected void postInstantiate( Object name, Map attributes, Object node ) {
      Map context = getContext();
      String widgetId = (String) context.get( WIDGET_ID );
      if( widgetId != null && node != null ){
         //widgets.put( widgetId, node );
         setVariable( widgetId, node );
      }
   }

   protected void preInstantiate( Object name, Map attributes, Object value ) {
      Map context = getContext();
      context.put( WIDGET_ID, attributes.remove( "id" ) );
      context.put( CONSTRAINTS, attributes.remove( "constraints" ) );
      context.put( Z_INDEX, attributes.remove( "zindex" ) );
   }

   protected void registerWidgets() {
      //
      // non-widget support classes
      //
      registerFactory( "action", new ActionFactory() );
      registerFactory( "actions", new CollectionFactory() );
      registerBeanFactory( "buttonGroup", SButtonGroup.class );
      registerFactory( "map", new MapFactory() );

      // ulimate pass through types
      registerFactory( "widget", new SWidgetFactory( SComponent.class, true ) );
      registerFactory( "container", new SWidgetFactory( SComponent.class, false ) );
      registerFactory( "bean", new SWidgetFactory( Object.class, true ) );

      //
      // standalone window classes
      //
      registerFactory( "dialog", new SDialogFactory() );
      registerFactory( "frame", new SFrameFactory() );
      registerBeanFactory( "optionPane", SOptionPane.class );

      //
      // widgets
      //
      registerFactory( "button", new SRichActionWidgetFactory( SButton.class ) );
      registerFactory( "checkBox", new SRichActionWidgetFactory( SCheckBox.class ) );
      registerFactory( "menuItem", new SRichActionWidgetFactory( SMenuItem.class ) );
      registerFactory( "radioButton", new SRichActionWidgetFactory( SRadioButton.class ) );
      registerFactory( "toggleButton", new SRichActionWidgetFactory( SToggleButton.class ) );

      registerFactory( "label", new STextArgWidgetFactory( SLabel.class ) );
      registerFactory( "passwordField", new STextArgWidgetFactory( SPasswordField.class ) );
      registerFactory( "textArea", new STextArgWidgetFactory( STextArea.class ) );
      registerFactory( "textField", new STextArgWidgetFactory( STextField.class ) );

      registerFactory( "comboBox", new SComboBoxFactory() );
      registerBeanFactory( "desktopPane", SDesktopPane.class );
      registerFactory( "formattedTextField", new SFormattedTextFactory() );
      registerBeanFactory( "internalFrame", SInternalFrame.class );
      registerBeanFactory( "list", SList.class );
      registerBeanFactory( "menu", SMenu.class );
      registerBeanFactory( "menuBar", SMenuBar.class );
      registerBeanFactory( "panel", SPanel.class );
      registerBeanFactory( "popupMenu", SPopupMenu.class );
      registerBeanFactory( "progressBar", SProgressBar.class );
      registerBeanFactory( "scrollBar", SScrollBar.class );
      registerBeanFactory( "scrollPane", SScrollPane.class );
      registerBeanFactory( "separator", SSeparator.class );
      // removed in wings 3.1
      // registerBeanFactory( "slider", SSlider.class );
      registerBeanFactory( "spinner", SSpinner.class );
      registerBeanFactory( "tabbedPane", STabbedPane.class );
      registerBeanFactory( "table", STable.class );
      registerBeanFactory( "tableColumn", STableColumn.class );
      registerBeanFactory( "toolBar", SToolBar.class );
      registerBeanFactory( "tree", STree.class );

      //
      // MVC models
      //
      registerBeanFactory( "boundedRangeModel", SDefaultBoundedRangeModel.class );
      registerBeanFactory( "pagingBoundedRangeModel", SPagingBoundedRangeModel.class );
      // spinner models
      registerBeanFactory( "spinnerDateModel", SpinnerDateModel.class );
      registerBeanFactory( "spinnerListModel", SpinnerListModel.class );
      registerBeanFactory( "spinnerNumberModel", SpinnerNumberModel.class );

      // table models
      registerFactory( "tableModel", new TableModelFactory() );
      registerFactory( "propertyColumn", new PropertyColumnFactory() );
      registerFactory( "closureColumn", new ClosureColumnFactory() );

      //
      // Layouts
      //
      registerBeanFactory( "borderLayout", SBorderLayout.class );
      registerBeanFactory( "cardLayout", SCardLayout.class );
      registerBeanFactory( "flowLayout", SFlowLayout.class );
      registerBeanFactory( "flowDownLayout", SFlowDownLayout.class );
      registerBeanFactory( "gridBagLayout", SGridBagLayout.class );
      registerBeanFactory( "gridLayout", SGridLayout.class );
      registerBeanFactory( "nullLayout", SNullLayout.class );
      registerBeanFactory( "gridBagConstraints", GridBagConstraints.class );
      registerBeanFactory( "gbc", GridBagConstraints.class ); // shortcut name

      registerFactory( "boxLayout", new SBoxLayoutFactory() );
      registerFactory( "rootLayout", new SRootLayoutFactory() );
      registerBeanFactory( "templateLayout", STemplateLayout.class );

      //
      // WingS specific
      //
      registerBeanFactory( "anchor", SAnchor.class );
      registerBeanFactory( "byteArrayIcon", SByteArrayIcon.class );
      registerFactory( "downloadButton", new SDownloadButtontFactory() );
      registerBeanFactory( "fileChooser", SFileChooser.class );
      registerFactory( "fileIcon", new SFileIconFactory() );
      registerFactory( "form", new SFormFactory() );
      registerFactory( "imageIcon", new SImageIconFactory() );
      registerFactory( "pageScroller", new SPageScrollerFactory() );
      registerFactory( "popup", new SPopupFactory() );
      registerBeanFactory( "rawText", SRawTextComponent.class );
      registerFactory( "resourceIcon", new SResourceIconFactory() );
      registerFactory( "spacer", new SSpacerFactory() );

      //
      // borders
      //
      registerFactory( "lineBorder", new SLineBorderFactory( SLineBorder.SOLID ) );
      registerFactory( "dottedLineBorder", new SLineBorderFactory( SLineBorder.DOTTED ) );
      registerFactory( "dashedLineBorder", new SLineBorderFactory( SLineBorder.DASHED ) );
      registerFactory( "loweredBevelBorder", new SBevelBorderFactory( SBevelBorder.LOWERED ) );
      registerFactory( "raisedBevelBorder", new SBevelBorderFactory( SBevelBorder.RAISED ) );
      registerFactory( "etchedBorder", new SEtchedBorderFactory( SEtchedBorder.LOWERED ) );
      registerFactory( "loweredEtchedBorder", new SEtchedBorderFactory( SEtchedBorder.LOWERED ) );
      registerFactory( "raisedEtchedBorder", new SEtchedBorderFactory( SEtchedBorder.RAISED ) );
      // registerFactory("titledBorder", new TitledBorderFactory())
      registerFactory( "emptyBorder", new SEmptyBorderFactory() );
      // registerFactory("compoundBorder", new CompoundBorderFactory())
      // registerFactory("matteBorder", new MatteBorderFactory())
   }
}