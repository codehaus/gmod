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

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation

import java.awt.Shape
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

/**
 * Base implementation of GraphicsOperation.<br>
 * It adds propertyChangeSupport for all parameters and optional parameters
 * among other things.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractGraphicsOperation extends GroovyObjectSupport implements
  GraphicsOperation, PropertyChangeListener {
    private String name
    private PropertyChangeSupport propertyChangeSupport

    protected static required = []
    protected static optional = []

    /**
     * Creates a new GraphicsOperation with a name.
     *
     * @param name the name of the operation
     */
    public AbstractGraphicsOperation( String name ) {
        this.name = name
        propertyChangeSupport = new PropertyChangeSupport( this )
    }

    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        propertyChangeSupport.addPropertyChangeListener( listener )
    }

    public String getName() {
        return name
    }

    public String toString() {
       return name
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        propertyChangeSupport.removePropertyChangeListener( listener );
    }

    public void propertyChange( PropertyChangeEvent event ){
       // empty
    }

    void setProperty( String property, Object value ) {
        def oldValue = getProperty( property )
        super.setProperty( property, value )
        if( isParameter(property) ){
            if( value != oldValue ){
               firePropertyChange( property, oldValue, value )
            }
        }
    }

    protected void firePropertyChange( String name, Object oldValue, Object newValue ) {
        propertyChangeSupport.firePropertyChange( name, oldValue, newValue )
    }

    private boolean isParameter( String name ) {
        return required.contains(name) || optional.contains(name)
    }
}