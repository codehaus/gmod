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

package groovy.wings;

import groovy.lang.Closure;
import groovy.model.DefaultTableModel;
import groovy.swing.SwingBuilder;
import groovy.swing.factory.ActionFactory;
import groovy.swing.factory.CollectionFactory;
import groovy.swing.factory.MapFactory;
import groovy.swing.factory.TableModelFactory;
import groovy.wings.factory.SBoxLayoutFactory;
import groovy.wings.factory.SComboBoxFactory;
import groovy.wings.factory.SDialogFactory;
import groovy.wings.factory.SDownloadButtontFactory;
import groovy.wings.factory.SFileIconFactory;
import groovy.wings.factory.SFormFactory;
import groovy.wings.factory.SFormattedTextFactory;
import groovy.wings.factory.SFrameFactory;
import groovy.wings.factory.SImageIconFactory;
import groovy.wings.factory.SPageScrollerFactory;
import groovy.wings.factory.SPopupFactory;
import groovy.wings.factory.SResourceIconFactory;
import groovy.wings.factory.SRichActionWidgetFactory;
import groovy.wings.factory.SRootLayoutFactory;
import groovy.wings.factory.SSpacerFactory;
import groovy.wings.factory.STextArgWidgetFactory;
import groovy.wings.factory.SWidgetFactory;
import groovy.wings.impl.STableColumnModelAdapter;

import java.awt.GridBagConstraints;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.Action;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableModel;


import org.codehaus.groovy.runtime.InvokerHelper;
import org.wings.SAbstractButton;
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
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.SGridBagLayout;
import org.wings.SGridLayout;
import org.wings.SInternalFrame;
import org.wings.SLabel;
import org.wings.SLayoutManager;
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
import org.wings.SSlider;
import org.wings.SSpinner;
import org.wings.STabbedPane;
import org.wings.STable;
import org.wings.STemplateLayout;
import org.wings.STextArea;
import org.wings.STextField;
import org.wings.SToggleButton;
import org.wings.SToolBar;
import org.wings.STree;
import org.wings.table.STableColumn;

/**
 * A helper class for creating WingS widgets using GroovyMarkup.<br>
 * Based on the original SwingBuilder.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class WingSBuilder extends SwingBuilder {
   public static SContainer getLayoutTarget( SContainer parent ) {
      if( parent instanceof SRootContainer ){
         SRootContainer rc = (SRootContainer) parent;
         parent = rc.getContentPane();
      }
      return parent;
   }

   private Boolean autoForm;
   private Object constraints;
   private boolean formInHierarchy;
   private Integer zindex;

   public WingSBuilder() {
      registerWidgets();
   }

   public void addFormToHierarchy( SForm form ) {
      if( !getContainingWindows().isEmpty() ){
         this.formInHierarchy = true;
      }
   }

   public SwingBuilder edt( Closure closure ) {
      throw new UnsupportedOperationException( "WingSBuilder does not use the EDT" );
   }

   protected Object createNode( Object name, Map attributes, Object value ) {
      constraints = attributes.remove( "constraints" );
      zindex = (Integer) attributes.remove( "zindex" );
      if( "frame".equals( name ) ){
         autoForm = (Boolean) attributes.remove( "autoForm" );
      }
      return super.createNode( name, attributes, value );
   }

   protected Object getConstraints() {
      return constraints;
   }

   protected Integer getZindex() {
      return zindex;
   }

   protected void handleWidgetAttributes( Object widget, Map attributes ) {
      // first, short circuit
      if( attributes.isEmpty() || (widget == null) ){
         return;
      }

      // some special cases...
      if( attributes.containsKey( "buttonGroup" ) ){
         Object o = attributes.get( "buttonGroup" );
         if( (o instanceof SButtonGroup) && (widget instanceof SAbstractButton) ){
            SButtonGroup buttonGroup = (SButtonGroup) o;
            buttonGroup.add( (SAbstractButton) widget );
            attributes.remove( "buttonGroup" );
         }
      }

      // set the properties
      for( Iterator iter = attributes.entrySet()
            .iterator(); iter.hasNext(); ){
         Map.Entry entry = (Map.Entry) iter.next();
         String property = entry.getKey()
               .toString();
         Object value = entry.getValue();
         InvokerHelper.setProperty( widget, property, value );
      }
   }

   protected void nodeCompleted( Object parent, Object node ) {
      // set models after the node has been completed
      if( node instanceof TableModel && parent instanceof STable ){
         STable table = (STable) parent;
         TableModel model = (TableModel) node;
         table.setModel( model );
         if( model instanceof DefaultTableModel ){
            table.setColumnModel( new STableColumnModelAdapter(
                  ((DefaultTableModel) model).getColumnModel() ) );
         }
      }
      if( node instanceof SFrame ){
         LinkedList containingWindows = getContainingWindows();
         if( containingWindows.size() == 1 && !hasFormInHierarchy()
               && (autoForm == null || autoForm.booleanValue()) ){
            SFrame frame = (SFrame) containingWindows.peek();
            SContainer contentPane = frame.getContentPane();
            frame.setContentPane( new SPanel( new SBorderLayout() ) );
            SForm form = new SForm( frame.getLayout() );
            form.add( contentPane );
            frame.getContentPane()
                  .add( form );
         }

         if( !containingWindows.isEmpty() && containingWindows.getLast() == node ){
            containingWindows.removeLast();
         }
      }
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
      registerFactory( "widget", new SWidgetFactory() );
      registerFactory( "container", new SWidgetFactory() );

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
      registerBeanFactory( "slider", SSlider.class );
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
      registerFactory( "propertyColumn", new TableModelFactory.PropertyColumnFactory() );
      registerFactory( "closureColumn", new TableModelFactory.ClosureColumnFactory() );

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
   }

   protected void setParent( Object parent, Object child ) {
      if( parent instanceof Collection ){
         ((Collection) parent).add( child );
      }else if( child instanceof Action ){
         setParentForAction( parent, (Action) child );
      }else if( (child instanceof SLayoutManager) && (parent instanceof SContainer) ){
         SContainer target = getLayoutTarget( (SContainer) parent );
         InvokerHelper.setProperty( target, "layout", child );
      }else if( parent instanceof STable && child instanceof STableColumn ){
         STable table = (STable) parent;
         STableColumn column = (STableColumn) child;
         table.getColumnModel()
               .addColumn( column );
      }else if( parent instanceof STabbedPane && child instanceof SComponent ){
         STabbedPane tabbedPane = (STabbedPane) parent;
         tabbedPane.add( (SComponent) child );
      }else if( child instanceof SFrame ){
         // do nothing. owner of frame is set elsewhere, and this
         // shouldn't get added to any parent as a child
         // if it is a top level component anyway
      }else{
         SComponent component = null;
         if( child instanceof SComponent ){
            component = (SComponent) child;
         }
         if( component != null ){
            setParentForComponent( parent, component );
         }
      }
   }

   private boolean hasFormInHierarchy() {
      return formInHierarchy;
   }

   private void setParentForAction( Object parent, Action action ) {
      try{
         InvokerHelper.setProperty( parent, "action", action );
      }catch( RuntimeException re ){
         // must not have an action property...
         // so we ignore it and go on
      }
   }

   private void setParentForComponent( Object parent, SComponent component ) {
      if( parent instanceof SRootContainer ){
         SRootContainer rc = (SRootContainer) parent;
         if( constraints != null ){
            if( zindex != null ){
               rc.getContentPane()
                     .add( component, constraints, zindex.intValue() );
            }else{
               rc.getContentPane()
                     .add( component, constraints );
            }
         }else{
            rc.getContentPane()
                  .add( component );
         }
      }else if( parent instanceof SScrollPane ){
         SScrollPane scrollPane = (SScrollPane) parent;
         scrollPane.setViewportView( component );

      }else if( parent instanceof SMenuBar && component instanceof SMenu ){
         SMenuBar menuBar = (SMenuBar) parent;
         menuBar.add( (SMenu) component );
      }else if( parent instanceof SContainer ){
         SContainer container = (SContainer) parent;
         if( constraints != null ){
            if( zindex != null ){
               container.add( component, constraints, zindex.intValue() );
            }else{
               container.add( component, constraints );
            }
         }else{
            container.add( component );
         }
      }
   }
}