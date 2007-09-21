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
import groovy.swing.j2d.impl.AbstractGraphicsOperation

import java.awt.Graphics2D
import java.awt.Image
import java.awt.Paint
import java.awt.Rectangle
import java.awt.Shape
import java.awt.TexturePaint
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.awt.Component
import java.net.URL

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class TexturePaintGraphicsOperation extends AbstractGraphicsOperation {
    private Paint paint
    def x1
    def y1
    def x2
    def y2
    def image
    def file
    def url

    static supportsFill = true

    TexturePaintGraphicsOperation() {
        super( "texturePaint", ["x1", "y1", "x2", "y2"] as String[],
               ["image", "file", "url"] as String[] )
    }

    public Shape getClip( Graphics2D g, Component target ) {
        int x1 = getParameterValue( "x1" )
        int x2 = getParameterValue( "x2" )
        int y1 = getParameterValue( "y1" )
        int y2 = getParameterValue( "y2" )
        return new Rectangle( x1, y1, x2 - x1, y2 - y1 )
    }

    public Paint getPaint() {
        return paint;
    }

    public void verify() {
        Map parameters = getParameterMap()
        parameters.each { k, v ->
            if( !k.equals( "image" ) && !k.equals( "file" ) && !k.equals( "url" ) ){
                if( !v ){
                throw new IllegalStateException( "Property '${k}' for 'texturePaint' has no value" )
                }
            }
        }

        if( !parameters.containsKey( "image" ) || !parameters.containsKey( "file" )
                || !parameters.containsKey( "url" ) ){
            throw new IllegalStateException(
                    "Must define  one of [image,file,url] for 'texturePaint'" )
        }
    }

    protected void doExecute( Graphics2D g, Component target ){
        int x1 = getParameterValue( "x1" )
        int x2 = getParameterValue( "x2" )
        int y1 = getParameterValue( "y1" )
        int y2 = getParameterValue( "y2" )

        BufferedImage image = loadImage( g, target )
        if( image != null ){
            paint = new TexturePaint( image, new Rectangle( x1, y1, x2 - x1, y2 - y1 ) )
            g.setPaint( paint )
        }
    }

    private BufferedImage loadImage( Graphics2D g, Component target ) {
        Image image = null
        if( parameterHasValue( "image" ) ){
            if( image instanceof BufferedImage ){
                return (BufferedImage) getParameterValue( "image" )
            }
            image = (Image) getParameterValue( "image" )
        }else if( parameterHasValue( "file" ) ){
            image = Toolkit.getDefaultToolkit()
                    .getImage( (String) getParameterValue( "file" ) )
        }else if( parameterHasValue( "url" ) ){
            image = Toolkit.getDefaultToolkit()
                    .getImage( (URL) getParameterValue( "url" ) )
        }
        if( image.getWidth( target ) <= 0 || image.getHeight( target ) <= 0 ){
            return null
        }
        BufferedImage bi = g.getDeviceConfiguration()
                .createCompatibleImage( image.getWidth( target ), image.getHeight( target ) )
        bi.getGraphics().drawImage( image, 0, 0, target )
        return bi
    }
}