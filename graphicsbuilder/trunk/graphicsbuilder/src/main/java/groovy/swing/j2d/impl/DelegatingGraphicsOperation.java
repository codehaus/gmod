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

import groovy.lang.MissingPropertyException;
import groovy.swing.j2d.GraphicsOperation;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.ImageObserver;

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

    public Shape getClip( Graphics2D g, ImageObserver observer ) {
        return delegate.getClip( g, observer );
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

    protected void afterDelegateExecutes( Graphics2D g, ImageObserver observer ) {
    }

    protected void beforeDelegateExecutes( Graphics2D g, ImageObserver observer ) {
    }

    protected final void doExecute( Graphics2D g, ImageObserver observer ) {
        beforeDelegateExecutes( g, observer );
        executeDelegate( g, observer );
        afterDelegateExecutes( g, observer );
    }

    protected void executeDelegate( Graphics2D g, ImageObserver observer ) {
        delegate.execute( g, observer );
    }
}