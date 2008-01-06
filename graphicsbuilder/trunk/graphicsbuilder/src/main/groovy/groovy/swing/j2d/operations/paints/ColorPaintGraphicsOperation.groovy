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

package groovy.swing.j2d.operations.paints

import java.awt.Color
import java.awt.Paint
import java.awt.geom.Rectangle2D
import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class ColorPaintGraphicsOperation extends AbstractPaintingGraphicsOperation {
    public static required = ['color']

    def color = Color.BLACK

    ColorPaintGraphicsOperation(){
       super( "colorPaint" )
    }

    public Paint getPaint( GraphicsContext context, Rectangle2D bounds ) {
       if( color instanceof String ){
          return ColorCache.getInstance().getColor( color )
       }else if( color instanceof Color ){
          return color
       }
    }
}