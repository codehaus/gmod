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

import groovy.swing.j2d.GraphicsOperation;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

/**
 * Decorator that adds 'color' and 'strokeWidth' properties.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ShapeProviderGraphicsOperation extends DelegatingGraphicsOperation {
    private boolean asShape;

    public ShapeProviderGraphicsOperation( GraphicsOperation delegate ) {
        super( delegate );
        addParameter( "asShape", false );
    }

    public String[] getOptionalParameters() {
        String[] optionals = getDelegate().getOptionalParameters();
        String[] other = new String[optionals.length + 1];
        System.arraycopy( optionals, 0, other, 0, optionals.length );
        other[optionals.length] = "asShape";
        return other;
    }

    public boolean isAsShape() {
        return asShape;
    }

    public void setAsShape( boolean asShape ) {
        this.asShape = asShape;
    }

    protected void executeDelegate( Graphics2D g, ImageObserver observer ) {
        if( !asShape ){
            super.executeDelegate( g, observer );
        }
    }
}