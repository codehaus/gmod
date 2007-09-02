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
import groovy.swing.j2d.GraphicsOperation;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Base implementation of GraphicsOperation.<br>
 * It adds propertyChangeSupport for all parameters and optional parameters
 * among other things.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractGraphicsOperation extends GroovyObjectSupport implements
        GraphicsOperation {
    private String name;
    private String[] optionalParameters = new String[0];
    private Map parameterMap = new LinkedHashMap();
    private String[] parameters = new String[0];
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Creates a new GraphicsOperation with name and no parameters.
     *
     * @param name the name of the operation
     */
    public AbstractGraphicsOperation( String name ) {
        this( name, null, null );
    }

    /**
     * Creates a new GraphicsOperation with name and parameters.
     *
     * @param name the name of the operation
     * @param parameters a list of parameters.
     */
    public AbstractGraphicsOperation( String name, String[] parameters ) {
        this( name, parameters, null );
    }

    /**
     * Creates a new GraphicsOperation with name, parameters and optional
     * parameters.
     *
     * @param name the name of the operation
     * @param parameters a list of parameters.
     * @param parameters a list of optional parameters.
     */
    public AbstractGraphicsOperation( String name, String[] parameters, String[] optional ) {
        this.name = name;
        Set optionalParams = new LinkedHashSet();
        if( optional != null ){
            this.optionalParameters = optional;
            for( int i = 0; i < optional.length; i++ ){
                optionalParams.add( optional[i] );
            }
        }
        if( parameters != null ){
            this.parameters = parameters;
            for( int i = 0; i < parameters.length; i++ ){
                this.parameterMap.put( parameters[i],
                        optionalParams.contains( parameters[i] ) ? Boolean.FALSE : Boolean.TRUE );
            }
            if( optional != null ){
                for( int i = 0; i < optional.length; i++ ){
                    this.parameterMap.put( optional[i], Boolean.FALSE );
                }
            }
        }
        propertyChangeSupport = new PropertyChangeSupport( this );
    }

    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        propertyChangeSupport.addPropertyChangeListener( listener );
    }

    public void execute( Graphics2D g, ImageObserver observer ) {
        applyParameters();
        doExecute( g, observer );
    }

    public Shape getClip( Graphics2D g, ImageObserver observer ) {
        return null;
    }

    public String getName() {
        return name;
    }

    public String[] getOptionalParameters() {
        return optionalParameters;
    }

    public String[] getParameters() {
        return parameters;
    }

    public Object getParameterValue( String name ) {
        if( parameterMap.containsKey( name ) ){
            return getProperty( name );
        }
        return null;
    }

    public boolean hasListeners( String propertyName ) {
        return propertyChangeSupport.hasListeners( propertyName );
    }

    public boolean parameterHasValue( String name ) {
        if( parameterMap.containsKey( name ) ){
            return getProperty( name ) != null;
        }
        return false;
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        propertyChangeSupport.removePropertyChangeListener( listener );
    }

    public void setParameterValue( String name, Object value ) {
        if( parameterMap.containsKey( name ) ){
            setProperty( name, value );
        }
    }

    public void setProperty( String name, Object value ) {
        Object oldValue = getProperty( name );
        super.setProperty( name, value );
        if( parameterMap.containsKey( name ) ){
            firePropertyChange( name, oldValue, value );
        }
    }

    public String toString() {
        return name;
    }

    public void verify() {
        for( int i = 0; i < parameters.length; i++ ){
            String param = parameters[i];
            if( parameterMap.get( param )
                    .equals( Boolean.TRUE ) && getProperty( param ) == null ){
                throw new IllegalStateException( "Property '" + param + "' for '" + name
                        + "' has no value" );
            }
        }
    }

    /**
     * Adds a new parameter to the parameter list.
     *
     * @param name the name of the parameter
     */
    protected void addParameter( String name ) {
        addParameter( name, true );
    }

    /**
     * Adds a new parameter to the parameter list which may be verified.
     *
     * @param name the name of the parameter
     * @param verify true if the parameter value should be verified before
     *        executing, false otherwise
     */
    protected void addParameter( String name, boolean verify ) {
        String[] params = new String[parameters.length + 1];
        System.arraycopy( parameters, 0, params, 0, parameters.length );
        params[parameters.length] = name;
        parameters = params;
        parameterMap.put( name, verify ? Boolean.TRUE : Boolean.FALSE );
    }

    protected final void applyParameters() {
        /*
         * for( int i = 0; i < parameters.length; i++ ){ String param =
         * parameters[i]; Object value = getProperty( param ); if( value
         * instanceof Closure ){ setProperty( param, ((Closure) value).call() ); } }
         */
    }

    /**
     * Executes the operation
     */
    protected abstract void doExecute( Graphics2D g, ImageObserver observer );

    protected void firePropertyChange( String name, Object oldValue, Object newValue ) {
        propertyChangeSupport.firePropertyChange( name, oldValue, newValue );
    }

    protected final Map getParameterMap() {
        return Collections.unmodifiableMap( parameterMap );
    }
}