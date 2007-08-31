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

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Toolkit
import java.awt.image.ImageObserver

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ImageGraphicsOperation extends AbstractGraphicsOperation {
    def image
    def file
    def url
    def x
    def y
    def width
    def height
    def bgcolor
    def observer

    static contextual = true

    ImageGraphicsOperation() {
        super( "image", ["x", "y"] as String[], ["image", "file", "url", "width", "height",
                "bgcolor", "observer"] as String[] )
    }

    public Shape getClip( Graphics2D g, ImageObserver observer ) {
        int x = getParameterValue( "x" )
        int y = getParameterValue( "y" )
        int width = -1
        int height = -1
        if( parameterHasValue( "width" ) ){
            width = getParameterValue( "width" )
        }
        if( parameterHasValue( "height" ) ){
            width = getParameterValue( "height" )
        }
        if( width < 0 || height < 0 ){
            Image image = loadImage()
            width = image.getWidth( observer )
            height = image.getHeight( observer )
        }
        return new Rectangle( x, y, width, height )
    }

    public void verify() {
        if( !parameterHasValue( "x" ) ){
            throw new IllegalStateException( "Property 'x' for 'image' has no value" )
        }
        if( !parameterHasValue( "y" ) ){
            throw new IllegalStateException( "Property 'y' for 'image' has no value" )
        }
        if( !parameterHasValue( "image" ) && !parameterHasValue( "url" )
                && !parameterHasValue( "file" ) ){
            throw new IllegalStateException( "Must define  one of [image,file,url] for 'image'" )
        }
    }

    protected void doExecute( Graphics2D g, ImageObserver observer ) {
        int x = getParameterValue( "x" )
        int y = getParameterValue( "y" )
        int width = -1
        int height = -1
        if( parameterHasValue( "width" ) ){
            width = getParameterValue( "width" )
        }
        if( parameterHasValue( "height" ) ){
            width = getParameterValue( "height" )
        }
        Color bgcolor = null;
        if( parameterHasValue( "bgcolor" ) ){
            bgcolor = ColorCache.getInstance().getColor( getParameterValue( "bgcolor" ) )
        }

        ImageObserver previousObserver = observer
        if( parameterHasValue( "observer" ) ){
            observer = (ImageObserver) getParameterValue( "observer" )
            if( observer == null ){
                observer = previousObserver
            }
        }

        // TODO scale image if needed
        Image image = loadImage()
        if( !image.bufferedImage ){
            throw new IllegalStateException("Couldn't locate the image")
        }
        if( bgcolor != null ){
            if( width > -1 && height > -1 ){
                g.drawImage( image, x, y, width, height, bgcolor, observer )
            }else{
                g.drawImage( image, x, y, bgcolor, observer )
            }
        }else{
            if( width > -1 && height > -1 ){
                g.drawImage( image, x, y, width, height, observer )
            }else{
                g.drawImage( image, x, y, observer )
            }
        }
    }

    private Image loadImage() {
        if( parameterHasValue( "image" ) ){
            return (Image) getParameterValue( "image" )
        }else if( parameterHasValue( "file" ) ){
            return Toolkit.getDefaultToolkit()
                    .getImage( (String) getParameterValue( "file" ) )
        }else if( parameterHasValue( "url" ) ){
            return Toolkit.getDefaultToolkit()
                    .getImage( (URL) getParameterValue( "url" ) )
        }else{
            throw new IllegalStateException("Couldn't locate the image")
        }
    }
}