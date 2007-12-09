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

package groovy.swing.j2d.impl

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation

import java.awt.Shape

/**
 * Base implementation of GraphicsOperation.<br>
 * It adds propertyChangeSupport for all parameters and optional parameters
 * among other things.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractGraphicsOperation extends ObservableSupport implements GraphicsOperation {
    private String name
    //protected static required = []
    //protected static optional = []

    /**
     * Creates a new GraphicsOperation with a name.
     *
     * @param name the name of the operation
     */
    AbstractGraphicsOperation( String name ) {
        super()
        this.name = name
    }

    public String getName() {
        return name
    }

    public String toString() {
        return name
    }

    public void execute( GraphicsContext context ) {
        // empty
    }

    void setProperty( String property, Object value ) {
        def oldValue = getProperty( property )
        super.setProperty( property, value )
        if( isParameter(property) && value != oldValue ){
           firePropertyChange( property, oldValue, value )
        }
    }

    private boolean isParameter( String property ) {
        if( getMetaClass().hasProperty(this,'required') && required.contains(property) ) return true
        if( getMetaClass().hasProperty(this,'optional') && optional.contains(property) ) return true
        false
    }
}