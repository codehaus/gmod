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
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.Rect3DGraphicsOperation

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.image.ImageObserver

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

    protected void beforeDelegateExecutes( Graphics2D g, ImageObserver observer ) {
        if( parameterHasValue( "fill" ) ){
            Object fillValue = getParameterValue( "fill" )
            if( fillValue instanceof Color ){
                // Color is a subclass of Paint
                // we need to check it first
                Color color = g.getColor()
                g.setColor( (Color) fillValue )
                invokeFillMethod( g, observer )
                g.setColor( color )
            }else if( fillValue instanceof Paint ){
                Paint paint = g.getPaint()
                g.setPaint( (Paint) fillValue )
                invokeFillMethod( g, observer )
                g.setPaint( paint )
            }else if( fillValue instanceof PaintSupportGraphicsOperation ){
                Paint paint = g.getPaint()
                fillValue.execute( g, observer )
                g.setPaint( fillValue.adjustPaintToBounds(getClip(g,observer).bounds) )
                invokeFillMethod( g, observer )
                g.setPaint( paint )
            }else if( fillValue instanceof String ){
                Color color = g.getColor()
                g.setColor( ColorCache.getInstance()
                        .getColor( fillValue ) )
                invokeFillMethod( g, observer )
                g.setColor( color )
            }else if( fillValue instanceof Boolean && fillValue ){
                invokeFillMethod( g, observer )
            }
        }
        super.beforeDelegateExecutes( g, observer )
    }

    private void invokeFillMethod( Graphics2D g, ImageObserver observer ) {
        // some special cases
        GraphicsOperation go = getDelegate()
        if( delegate instanceof Rect3DGraphicsOperation ){
            g.fill3DRect( delegate.x, delegate.y, delegate.width, delegate.height,
                    delegate.raised )
        }else{
            // general case
            g.fill( getDelegate().getClip( g, observer ) )
        }
    }
}