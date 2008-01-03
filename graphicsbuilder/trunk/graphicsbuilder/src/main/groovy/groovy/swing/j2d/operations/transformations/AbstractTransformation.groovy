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

package groovy.swing.j2d.operations.transformations

import groovy.swing.j2d.operations.Transformation
import groovy.swing.j2d.impl.ObservableSupport

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractTransformation extends ObservableSupport implements Transformation {
    public static required = []
    public static optional = ['interpolation']

    private String name

    def interpolation

    public AbstractTransformation( String name ) {
        super()
        this.name = name
    }

    public String getName() {
        return name
    }

    public Transformation copy() {
       // assume no-args constructor
       def copy = getClass().newInstance()
       this.properties.each { key, value ->
          if( AbstractGraphicsOperation.isGraphicsParameter(this,key) ){
             copy."$key" = value
          }
       }

       // copy propertyChangeListeners if any
       this.propertyChangeListeners.each { listener ->
          copy.addPropertyChangeListener( listener )
       }

       return copy
    }

    void setProperty( String property, Object value ) {
        def oldValue = getProperty( property )
        super.setProperty( property, value )
        firePropertyChange( property, oldValue, value )
    }
}