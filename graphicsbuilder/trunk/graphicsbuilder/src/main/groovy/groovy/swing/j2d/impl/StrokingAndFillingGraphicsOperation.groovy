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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.Rect3DGraphicsOperation

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.Component

/**
 * Decorator that adds 'color', 'strokeWidth' and 'fill' properties.
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StrokingAndFillingGraphicsOperation extends StrokingGraphicsOperation {
    private Object fill

    public StrokingAndFillingGraphicsOperation( GraphicsOperation delegate ) {
        super( delegate )
        addParameter( "fill", false )
    }

    public String[] getOptionalParameters() {
        def optional = super.getOptionalParameters() as List
        return (optional + [ "fill" ]) as String[]
    }

    protected void beforeDelegateExecutes( GraphicsContext context ) {
        if( parameterHasValue( "fill" ) ){
            Object fillValue = getParameterValue( "fill" )
            if( fillValue instanceof Color ){
                // Color is a subclass of Paint
                // we need to check it first
                Color color = context.g.getColor()
                context.g.setColor( (Color) fillValue )
                invokeFillMethod( context )
                context.g.setColor( color )
            }else if( fillValue instanceof Paint ){
                Paint paint = gcontext..getPaint()
                context.g.setPaint( (Paint) fillValue )
                invokeFillMethod( context )
                context.g.setPaint( paint )
            }else if( fillValue instanceof PaintSupportGraphicsOperation ){
                Paint paint = context.g.getPaint()
                fillValue.execute( context )
                context.g.setPaint( fillValue.adjustPaintToBounds(getClip(context).bounds) )
                invokeFillMethod( context )
                context.g.setPaint( paint )
            }else if( fillValue instanceof String ){
                Color color = context.g.getColor()
                context.g.setColor( ColorCache.getInstance()
                        .getColor( fillValue ) )
                invokeFillMethod( context )
                context.g.setColor( color )
            }else if( fillValue instanceof Boolean && fillValue ){
                invokeFillMethod( context )
            }
        }
        super.beforeDelegateExecutes( context )
    }

    private void invokeFillMethod( GraphicsContext context ) {
        // some special cases
        GraphicsOperation go = getDelegate()
        if( delegate instanceof Rect3DGraphicsOperation ){
            context.g.fill3DRect( delegate.x, delegate.y, delegate.width, delegate.height,
                    delegate.raised )
        }else{
            // general case
            context.g.fill( getDelegate().getClip( context ) )
        }
    }
}