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

import groovy.swing.j2d.impl.AbstractGraphicsOperation

import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Ellipse2D
import java.awt.image.ImageObserver

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class EllipseGraphicsOperation extends AbstractGraphicsOperation {
    def cx
    def cy
    def radiusx
    def radiusy

    static fillable = true
    static contextual = true
    static hasShape = true

    public EllipseGraphicsOperation() {
        super( "ellipse", ["cx", "cy", "radiusx","radiusy"] as String[] )
    }

    public Shape getClip( Graphics2D g, ImageObserver observer ) {
        int cx = getParameterValue( "cx" )
        int cy = getParameterValue( "cy" )
        int radiusx = getParameterValue( "radiusx" )
        int radiusy = getParameterValue( "radiusy" )
        return new Ellipse2D.Double( cx - radiusx, cy - radiusy, radiusx * 2, radiusy * 2 )
    }

    protected void doExecute( Graphics2D g, ImageObserver observer ){
        g.draw( getClip( g, observer ) )
    }
}