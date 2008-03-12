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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.impl.ObservableSupport

import java.awt.Shape
import java.awt.Color
import java.beans.PropertyChangeEvent

/**
 * Base implementation of GraphicsOperation.<br>
 * It adds propertyChangeSupport for all parameters and optional parameters
 * among other things.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractGraphicsOperation extends ObservableSupport implements GraphicsOperation {
    private String nodeName
    private boolean executing
    
    // non-observable
    Closure beforeRender
    Closure afterRender
    String name

    //public static required = []
    //public static optional = []

    AbstractGraphicsOperation( String nodeName ) {
        super()
        this.nodeName = nodeName
        addPropertyChangeListener( this )
    }

    public String getNodeName() {
        return nodeName  
    }

    public String toString() {
        return name ? "${nodeName[name]}" : nodeName
    }

    public final void execute( GraphicsContext context ) {
        try {
           executing = true
           if( beforeRender ) beforeRender( context, this )
        }
        finally {
           executing = false
        }
        
        doExecute( context )

        try {
           executing = true
           if( afterRender ) afterRender( context, this )
        }
        finally {
           executing = false
        }
    }

    public void propertyChange( PropertyChangeEvent event ) {
       if( event.source == this ){
          localPropertyChange( event )
       }
    }

    void setProperty( String property, Object value ) {
        def oldValue = getProperty( property )
        super.setProperty( property, value )
        if( !executing && isParameter(property) && compare(oldValue,value) ){
           firePropertyChange( property, oldValue, value )
        }
    }

    protected abstract void doExecute( GraphicsContext context )

    protected void localPropertyChange( PropertyChangeEvent event ) {

    }

    protected boolean isParameter( String property ) {
        if( AbstractGraphicsOperation.hasProperty(this,'required') && required.contains(property) ) return true
        if( AbstractGraphicsOperation.hasProperty(this,'optional') && optional.contains(property) ) return true
        false
    }

    public static boolean isGraphicsParameter( GraphicsOperation target, String property ) {
       if( hasProperty(target,'required') && target.required.contains(property) ) return true
       if( hasProperty(target,'optional') && target.optional.contains(property) ) return true
       false
    }

    private boolean compare( oldvalue, newvalue ){
       if( oldvalue == null && newvalue == null ) return false
       if( oldvalue == null && newvalue != null ) return true
       if( oldvalue != null && newvalue == null ) return true

       switch( oldvalue.class ){
          case Boolean:
             if( newvalue instanceof String ) return (oldvalue as String) != newvalue
             if( newvalue instanceof Boolean ) return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             return true
             break;
          case String:
             if( newvalue instanceof Boolean ) return oldvalue != (newvalue as String)
             if( newvalue instanceof Color ) return ColorCache.getInstance().getColor(oldvalue) != newvalue
             return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             break;
          case Color:
             if( newvalue instanceof Boolean ) return true
             if( newvalue instanceof String ) return oldvalue != ColorCache.getInstance().getColor(newvalue)
             return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             break;
       }
       switch( newvalue.class ){
          case Boolean:
             if( oldvalue instanceof String ) return (newvalue as String) != oldvalue
             if( oldvalue instanceof Boolean ) return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             return true
             break;
          case String:
             if( oldvalue instanceof Boolean ) return newvalue != (oldvalue as String)
             if( oldvalue instanceof Color ) return ColorCache.getInstance().getColor(newvalue) != oldvalue
             return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             break;
          case Color:
             if( oldvalue instanceof Boolean ) return true
             if( oldvalue instanceof String ) return newvalue != ColorCache.getInstance().getColor(oldvalue)
             return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             break;
       }

       return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
    }

    private static boolean hasProperty( Object target, String property ){
       try{
          def v = target."$property"
       }catch( MissingPropertyException e ){
          return false
       }
       return true
    }
}
