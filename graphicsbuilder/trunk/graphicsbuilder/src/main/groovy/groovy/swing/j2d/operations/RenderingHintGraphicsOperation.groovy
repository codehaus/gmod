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
import java.awt.RenderingHints
import java.awt.image.ImageObserver

import groovy.swing.j2d.impl.AbstractGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class RenderingHintGraphicsOperation extends AbstractGraphicsOperation {
    String key
    String value

    RenderingHintGraphicsOperation() {
        super( "renderingHint", ["key","value"] as String[] )
    }

    protected void doExecute( Graphics2D g, ImageObserver observer ){
        g.setRenderingHint( convertKey(), convertValue() )
    }

    private RenderingHints.Key convertKey(){
        def prop = ("KEY_"+key.toUpperCase()).replaceAll(" ","_")
        return RenderingHints.@"${prop}"
    }

    private Object convertValue(){
        def prop = ("VALUE_"+value.toUpperCase()).replaceAll(" ","_")
        return RenderingHints.@"${prop}"
    }
}