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

import java.awt.Graphics2D
import java.awt.Shape
import java.awt.Component

import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.impl.AbstractGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class DrawGraphicsOperation extends AbstractGraphicsOperation {
    def shape

    static fillable = true
    static contextual = true
    static hasShape = true

    DrawGraphicsOperation() {
        super( "draw", ["shape"] as String[] )
    }

    public Shape getClip( Graphics2D g, Component target ){
        if( parameterHasValue("shape") ){
            def shape = getParameterValue("shape")
            if( shape instanceof GraphicsOperation && shape.parameterHasValue("asShape") &&
                    shape.getParameterValue("asShape") ){
                return shape.getClip(g,target)
            }else{
                return shape
            }
        }
        return null
    }

    protected void doExecute( Graphics2D g, Component target ){
        if( shape instanceof GraphicsOperation && shape.parameterHasValue("asShape") &&
                shape.getParameterValue("asShape") ){
           g.draw( shape.getClip(g,target) )
        }else{
           g.draw( shape )
        }
    }
}