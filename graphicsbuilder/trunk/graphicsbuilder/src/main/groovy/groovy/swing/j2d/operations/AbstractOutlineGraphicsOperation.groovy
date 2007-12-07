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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.OutlineProvider

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Shape

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class AbstractOutlineGraphicsOperation extends GroupGraphicsOperation implements OutlineProvider {
    protected static optional = super.optional + ['asShape']

    private Shape transformedShape

    // properties
    def asShape

    public AbstractOutlineGraphicsOperation( String name ) {
        super( name )
    }

    public Shape getOutline( GraphicsContext context ){ null }

    public Shape getTransformedShape() {
       transformedShape
    }

    protected boolean executeBeforeNestedOperations( GraphicsContext context ) {
        return withinClipBounds( context )
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
        go.execute( context )
    }

    protected void executeOperation( GraphicsContext context ) {
        if( !asShape ){
            draw( context )
        }
    }

    protected void draw( GraphicsContext context ) {
       def previousColor = null
       def previousStroke = null

       def g = context.g

       // short-circuit
       // don't draw the outline if borderColor == false
       if( borderColor instanceof Boolean && !borderColor ){
           return
       }

       // honor the clip
       if( !withinClipBounds( context ) ) {
           return
       }

       // apply color & stroke
       if( borderColor ){
           previousColor = g.color
           if( borderColor instanceof String ){
               g.color = ColorCache.getInstance().getColor( borderColor )
           }else if( value instanceof Color ){
               g.color = borderColor
           }
       }
       if( borderWidth ){
           previousStroke = g.stroke
           g.stroke = new BasicStroke( borderWidth )
       }

       // draw the outline
       g.draw( getOutline(context) )

       // restore color & stroke
       if( previousColor ) g.color = previousColor
       if( previousStroke ) g.stroke = previousStroke
    }

    private boolean withinClipBounds( GraphicsContext context ){
       if( transform ) {
          transformedShape = transform.createTransformedShape(getOutline(context))
          return transformedShape.intersects(context.g.clipBounds)
       }else{
          return getOutline(context).intersects(context.g.clipBounds)
       }
    }
}