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

package groovy.swing.j2d;

import groovy.swing.j2d.impl.ShapeProviderGraphicsOperation;

import java.awt.Component;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GraphicsContext {
    private Graphics2D g;
    private List shapes = new ArrayList();
    private Component target;

    public Graphics2D getG() {
        return g;
    }

    public List getShapes() {
        return Collections.unmodifiableList( shapes );
    }

    public Component getTarget() {
        return target;
    }

    public void setG( Graphics2D g ) {
        this.g = g;
    }

    public void setTarget( Component target ) {
        this.target = target;
    }

    public void addShape( ShapeProviderGraphicsOperation shape ) {
        if( !shapes.contains( shape ) ){
            shapes.add( shape );
        }
    }
}