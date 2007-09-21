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

package groovy.swing.j2d.impl

import groovy.swing.j2d.impl.AbstractExtPathOperation
import java.awt.Graphics2D
import java.awt.Component
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class QuadToExtPathOperation extends AbstractExtPathOperation {
    float x1
    float x2
    float y1
    float y2

    public void apply( ExtendedGeneralPath path, Graphics2D g, Component target ) {
       path.quadTo( x1, y1, x2, y2 )
    }
}