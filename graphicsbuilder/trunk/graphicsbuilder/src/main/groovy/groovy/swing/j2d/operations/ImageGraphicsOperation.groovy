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
import java.awt.Component
import java.awt.Graphics2D
import java.awt.Image
import java.awt.MediaTracker
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Toolkit

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ImageGraphicsOperation extends AbstractGraphicsOperation {
    def image
    def file
    def url
    def classpath
    def x = 0
    def y = 0
    def bgcolor

    private Image loadedImage

    static contextual = true

    ImageGraphicsOperation() {
        super( "image", ["x", "y"] as String[], ["image", "file", "url", "classpath", "bgcolor"] as String[] )
    }

    public Shape getClip( Graphics2D g, Component target ) {
        int x = getParameterValue( "x" )
        int y = getParameterValue( "y" )

        if( !loadedImage ){
            loadedImage = loadImage( target )
        }
        if( !loadedImage ){
            throw new IllegalStateException("Couldn't locate the image")
        }

        return new Rectangle( x, y, loadedImage.getWidth(null), loadedImage.getHeight(null) )
    }

    public void verify() {
        if( !parameterHasValue( "x" ) ){
            throw new IllegalStateException( "Property 'x' for 'image' has no value" )
        }
        if( !parameterHasValue( "y" ) ){
            throw new IllegalStateException( "Property 'y' for 'image' has no value" )
        }
        if( !parameterHasValue( "image" ) && !parameterHasValue( "url" )
            && !parameterHasValue( "file" ) && !parameterHasValue( "classpath" ) ){
            throw new IllegalStateException( "Must define one of [image,file,url,classpath] for 'image'" )
        }
    }

    protected void doExecute( Graphics2D g, Component target ) {
        int x = getParameterValue( "x" )
        int y = getParameterValue( "y" )
        Color bgcolor = null;
        if( parameterHasValue( "bgcolor" ) ){
            bgcolor = ColorCache.getInstance().getColor( getParameterValue( "bgcolor" ) )
        }

        if( !loadedImage ){
            loadedImage = loadImage( target )
        }
        if( !loadedImage ){
            throw new IllegalStateException("Couldn't locate the image")
        }
        if( bgcolor != null ){
            g.drawImage( loadedImage, x, y, bgcolor, null )
        }else{
            g.drawImage( loadedImage, x, y, null )
        }
    }

    private Image loadImage( Component comp ) {
        Image image = null;
        if( parameterHasValue( "image" ) ){
            image = getParameterValue( "image" )
        }else if( parameterHasValue( "file" ) ){
            image = Toolkit.getDefaultToolkit()
                    .getImage( (String) getParameterValue( "file" ) )
        }else if( parameterHasValue( "url" ) ){
            image = Toolkit.getDefaultToolkit()
                    .getImage( getParameterValue( "url" ).toURL() )
        }else if( parameterHasValue( "classpath" ) ){
            URL url = Thread.currentThread().getContextClassLoader()
                        .getResource( getParameterValue( "classpath" ) )
            image = Toolkit.getDefaultToolkit().getImage( url )
        }else{
            throw new IllegalStateException("Couldn't locate the image")
        }

        MediaTracker tracker = new MediaTracker( comp )
        tracker.addImage( image, 0 )
        try {
            tracker.waitForID( 0 )
        }catch( InterruptedException e ){
            return null
        }

        return image
    }
}