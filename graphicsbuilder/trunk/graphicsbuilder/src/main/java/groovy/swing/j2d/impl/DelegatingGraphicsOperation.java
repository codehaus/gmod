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

import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.swing.j2d.GraphicsContext;
import groovy.swing.j2d.GraphicsOperation;

import java.awt.Shape;
import java.beans.PropertyChangeListener;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Base class for creating decorators.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class DelegatingGraphicsOperation extends AbstractGraphicsOperation {
    private GraphicsOperation delegate;

    public DelegatingGraphicsOperation( GraphicsOperation delegate ) {
        super( delegate.getName(), delegate.getParameters(), delegate.getOptionalParameters() );
        this.delegate = delegate;
    }

    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        super.addPropertyChangeListener( listener );
        getDelegate().addPropertyChangeListener( listener );
    }

    public Shape getClip( GraphicsContext context ) {
        return delegate.getClip( context );
    }

    public final GraphicsOperation getDelegate() {
        return delegate;
    }

    public String[] getOptionalParameters() {
        return delegate.getOptionalParameters();
    }

    public String[] getParameters() {
        return delegate.getParameters();
    }

    public Object getProperty( String name ) {
        Object value = null;
        try{
            value = InvokerHelper.getProperty( delegate, name );
        }catch( MissingPropertyException e ){
            value = super.getProperty( name );
        }
        return value;
    }

    public Object invokeMethod( String name, Object arg ) {
        Object result = null;
        try{
            result = super.invokeMethod( name, arg );
        }catch( MissingMethodException mme ){
            result = InvokerHelper.invokeMethod( delegate, name, arg );
        }
        return result;
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        super.removePropertyChangeListener( listener );
        getDelegate().removePropertyChangeListener( listener );
    }

    public void setProperty( String name, Object value ) {
        try{
            InvokerHelper.setProperty( delegate, name, value );
        }catch( MissingPropertyException e ){
            super.setProperty( name, value );
        }
    }

    public void verify() {
        delegate.verify();
        super.verify();
    }

    protected void afterDelegateExecutes( GraphicsContext context ) {
    }

    protected void beforeDelegateExecutes( GraphicsContext context ) {
    }

    protected final void doExecute( GraphicsContext context ) {
        beforeDelegateExecutes( context );
        executeDelegate( context );
        afterDelegateExecutes( context );
    }

    protected void executeDelegate( GraphicsContext context ) {
        delegate.execute( context );
    }
}