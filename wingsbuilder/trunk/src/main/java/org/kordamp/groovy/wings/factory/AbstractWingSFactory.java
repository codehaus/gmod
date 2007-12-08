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
 */

package org.kordamp.groovy.wings.factory;

import groovy.lang.MissingPropertyException;
import groovy.model.DefaultTableModel;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.table.TableModel;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.kordamp.groovy.wings.WingSBuilder;
import org.kordamp.groovy.wings.impl.STableColumnModelAdapter;
import org.wings.SAbstractButton;
import org.wings.SButtonGroup;
import org.wings.SComponent;
import org.wings.SContainer;
import org.wings.SFrame;
import org.wings.SLayoutManager;
import org.wings.SMenu;
import org.wings.SMenuBar;
import org.wings.SRootContainer;
import org.wings.SScrollPane;
import org.wings.STabbedPane;
import org.wings.STable;
import org.wings.table.STableColumn;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractWingSFactory extends AbstractFactory {
   private Logger log;

   public AbstractWingSFactory() {
      this.log = Logger.getLogger( getClass().getName() );
   }

   public abstract Object doNewInstance( WingSBuilder builder, Object name, Object value,
         Map properties ) throws InstantiationException, IllegalAccessException;

   public final Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
         Map properties ) throws InstantiationException, IllegalAccessException {
      if( !(builder instanceof WingSBuilder) ){
         throw new RuntimeException( "This factory must be registered to a WingSBuilder" );
      }
      return doNewInstance( (WingSBuilder) builder, name, value, properties );
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object widget,
         Map attributes ) {
      // first, short circuit
      if( attributes.isEmpty() || (widget == null) ){
         return false;
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
         try{
            InvokerHelper.setProperty( widget, property, value );
         }catch( MissingPropertyException e ){
            if( ((WingSBuilder) builder).isAllowMissingProperties() ){
               log.log( Level.WARNING, "Could not set property '" + property
                     + "' for object of class '" + widget.getClass()
                           .getName() + "'" );
            }else{
               throw e;
            }
         }
      }
      return false;
   }

   public void onNodeCompleted( FactoryBuilderSupport builder, Object parent, Object node ) {
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
      WingSBuilder wingSBuilder = (WingSBuilder) builder;

      if( node instanceof SFrame ){
         LinkedList containingWindows = wingSBuilder.getContainingWindows();
         if( !containingWindows.isEmpty() && containingWindows.getLast() == node ){
            containingWindows.removeLast();
         }
      }
   }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
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
            setParentForComponent( (WingSBuilder) builder, parent, component );
         }
      }
   }

   protected SContainer getLayoutTarget( SContainer parent ) {
      if( parent instanceof SRootContainer ){
         SRootContainer rc = (SRootContainer) parent;
         parent = rc.getContentPane();
      }
      return parent;
   }

   protected void setParentForAction( Object parent, Action action ) {
      try{
         InvokerHelper.setProperty( parent, "action", action );
      }catch( RuntimeException re ){
         // must not have an action property...
         // so we ignore it and go on
      }
   }

   protected void setParentForComponent( WingSBuilder builder, Object parent, SComponent component ) {
      Object constraints = builder.getConstraints();
      Integer zindex = builder.getZIndex();
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