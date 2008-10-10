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

package groovy.swing.j2d.operations.filters

import java.awt.Color
import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.impl.ObservableSupport

import com.jhlabs.image.Gradient

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class Knot extends ObservableSupport {
    def color
    def index
    def x
    def type
    def opacity

    Knot copy() {
       new Knot( index: index, color: color, opacity: opacity, x: x, type: type )
    }

    int getRealColor(){
       Color c = ColorCache.getColor( color )
       if( opacity != null ) c = c.derive(alpha:opacity)
       return c.getRGB()
    }

    String toString(){
       return "knot[index: $index, color: $color, opacity: $opacity, x: $x, type: $type]"
    }
}