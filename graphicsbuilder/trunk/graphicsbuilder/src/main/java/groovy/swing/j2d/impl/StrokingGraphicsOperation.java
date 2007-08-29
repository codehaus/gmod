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

package groovy.swing.j2d.impl;

import groovy.swing.j2d.ColorCache;
import groovy.swing.j2d.GraphicsOperation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.ImageObserver;

/**
 * Decorator that adds 'color' and 'strokeWidth' properties.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StrokingGraphicsOperation extends DelegatingGraphicsOperation {
    private Object color;
    private Color previousColor;
    private Stroke previousStroke;
    private Object strokeWidth;

    public StrokingGraphicsOperation( GraphicsOperation delegate ) {
        super( delegate );
        addParameter( "color", false );
        addParameter( "strokeWidth", false );
    }

    public Object getColor() {
        return color;
    }

    public String[] getOptionalParameters() {
        String[] optionals = getDelegate().getOptionalParameters();
        String[] other = new String[optionals.length + 2];
        System.arraycopy( optionals, 0, other, 0, optionals.length );
        other[optionals.length] = "color";
        other[optionals.length + 1] = "strokeWidth";
        return other;
    }

    public Object getStrokeWidth() {
        return strokeWidth;
    }

    public void setColor( Object color ) {
        this.color = color;
    }

    public void setStrokeWidth( Object strokeWidth ) {
        this.strokeWidth = strokeWidth;
    }

    protected void afterDelegateExecutes( Graphics2D g, ImageObserver observer ) {
        if( previousStroke != null ){
            g.setStroke( previousStroke );
        }
        if( previousColor != null ){
            g.setColor( previousColor );
        }
    }

    protected void beforeDelegateExecutes( Graphics2D g, ImageObserver observer ) {
        if( parameterHasValue( "color" ) ){
            Object value = getParameterValue( "color" );
            if( value instanceof String ){
                previousColor = g.getColor();
                g.setColor( ColorCache.getInstance()
                        .getColor( (String) value ) );
            }else if( value instanceof Color ){
                previousColor = g.getColor();
                g.setColor( (Color) value );
            }
        }
        if( parameterHasValue( "strokeWidth" ) ){
            previousStroke = g.getStroke();
            g.setStroke( new BasicStroke(
                    ((Number) getParameterValue( "strokeWidth" )).floatValue() ) );
        }
    }
}