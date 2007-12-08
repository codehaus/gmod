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

package groovy.swing.j2d.operations

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractExtPathOperation extends GroovyObjectSupport implements ExtPathOperation {
    private PropertyChangeSupport propertyChangeSupport
    
    public AbstractExtPathOperation() {
        propertyChangeSupport = new PropertyChangeSupport( this )
    }
    
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
       propertyChangeSupport.addPropertyChangeListener( listener )
    }
    
    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        propertyChangeSupport.removePropertyChangeListener( listener );
    }

    void setProperty( String property, Object value ) {
       def oldValue = getProperty( property )
       super.setProperty( property, value )
       if( value != oldValue ){
          firePropertyChange( property, oldValue, value )
       }
    }

    protected void firePropertyChange( String name, Object oldValue, Object newValue ) {
       propertyChangeSupport.firePropertyChange( name, oldValue, newValue )
    }    
}
