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

package groovy.swing.j2d.impl;

import groovy.lang.GroovyObjectSupport;
import groovy.swing.j2d.Observable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ObservableSupport extends GroovyObjectSupport implements Observable,
      PropertyChangeListener {
   private PropertyChangeSupport propertyChangeSupport;

   public ObservableSupport() {
      propertyChangeSupport = new PropertyChangeSupport( this );
   }

   public void addPropertyChangeListener( PropertyChangeListener listener ) {
      if( listener == null ){
         return;
      }
      PropertyChangeListener[] listeners = propertyChangeSupport.getPropertyChangeListeners();
      for( int i = 0; i < listeners.length; i++ ){
         if( listener.equals( listeners[i] ) ){
            return;
         }
      }
      propertyChangeSupport.addPropertyChangeListener( listener );
   }

   public PropertyChangeListener[] getPropertyChangeListeners() {
      return propertyChangeSupport.getPropertyChangeListeners();
   }

   public void propertyChange( PropertyChangeEvent event ) {
      // empty
   }

   public void removePropertyChangeListener( PropertyChangeListener listener ) {
      propertyChangeSupport.removePropertyChangeListener( listener );
   }

   protected void firePropertyChange( PropertyChangeEvent event ){
      propertyChangeSupport.firePropertyChange( event );
   }

   protected void firePropertyChange( String name, Object oldValue, Object newValue ) {
      propertyChangeSupport.firePropertyChange( name, oldValue != null ? oldValue
            : NullValue.getInstance(), newValue != null ? newValue : NullValue.getInstance() );
   }

   public static class NullValue {
      private static NullValue instance = new NullValue();

      public static NullValue getInstance() {
         return instance;
      }

      private NullValue() {

      }
   }
}