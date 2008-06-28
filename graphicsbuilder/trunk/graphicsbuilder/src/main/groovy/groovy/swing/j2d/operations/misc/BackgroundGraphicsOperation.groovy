/*
 * Copyright 2007-2008 the original author or authors.
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

package groovy.swing.j2d.operations.misc

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.AbstractGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class BackgroundGraphicsOperation extends AbstractGraphicsOperation {
    public static required = ['color']

    def color

    BackgroundGraphicsOperation() {
        super( "color" )
    }

    protected void doExecute( GraphicsContext context ){
        if( !color ) return
        def clip = context.g.clipBounds
        context.g.setBackground( color )
        context.g.clearRect( clip.x as int, clip.y as int, clip.width as int, clip.height as int )
    }
}
