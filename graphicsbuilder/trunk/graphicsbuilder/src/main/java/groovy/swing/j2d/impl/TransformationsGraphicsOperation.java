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

import java.awt.Component;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Groups all TransformSupportGraphicsOperations.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TransformationsGraphicsOperation extends AbstractGraphicsOperation {
    private List operations = new ArrayList();

    public TransformationsGraphicsOperation() {
        super( "transformations", null );
        operations = new ArrayList();
    }

    /**
     * Adds a new operation to the operation list.<br>
     * Only instances of TransformSupportGraphicsOperation will be added
     */
    public void addOperation( GraphicsOperation go ) {
        if( go instanceof TransformSupportGraphicsOperation ){
            operations.add( go );
        }
    }

    public final int size() {
        return operations.size();
    }

    public void verify() {
        for( Iterator i = operations.iterator(); i.hasNext(); ){
            GraphicsOperation go = (GraphicsOperation) i.next();
            go.verify();
        }
    }

    protected void doExecute( Graphics2D g, Component target ) {
        for( Iterator i = operations.iterator(); i.hasNext(); ){
            GraphicsOperation go = (GraphicsOperation) i.next();
            go.execute( g, target );
        }
    }
}