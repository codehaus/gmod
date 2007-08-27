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

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GraphicsContext {
    private Color background;
    private Shape clip;
    private Color color;
    private Font font;
    private Paint paint;
    private Stroke stroke;
    private double x;
    private double y;

    public Color getBackground() {
        return background;
    }

    public Shape getClip() {
        return clip;
    }

    public Color getColor() {
        return color;
    }

    public Font getFont() {
        return font;
    }

    public Paint getPaint() {
        return paint;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setBackground( Color background ) {
        this.background = background;
    }

    public void setClip( Shape clip ) {
        this.clip = clip;
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    public void setFont( Font font ) {
        this.font = font;
    }

    public void setPaint( Paint paint ) {
        this.paint = paint;
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public void setY( double y ) {
        this.y = y;
    }
}