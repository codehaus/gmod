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

import java.awt.Font
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.image.ImageObserver
import java.awt.font.FontRenderContext
import java.awt.font.TextLayout
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D

import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class TextGraphicsOperation extends AbstractShapeGraphicsOperation {
    def text
    def x
    def y

    TextGraphicsOperation() {
        super( "text", ["text", "x", "y"] as String[] )
    }

    protected Shape computeShape( Graphics2D g, ImageObserver observer ){
        FontRenderContext frc = g.getFontRenderContext()
        TextLayout layout = new TextLayout( text, g.font, frc )
        Rectangle2D bounds = layout.getBounds()
        return layout.getOutline( AffineTransform.getTranslateInstance( x, y + bounds.height ) )
    }
}