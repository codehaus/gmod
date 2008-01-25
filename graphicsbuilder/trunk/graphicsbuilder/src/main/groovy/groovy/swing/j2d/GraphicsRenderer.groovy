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

package groovy.swing.j2d

import java.awt.GraphicsConfiguration
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.Transparency
import java.awt.image.BufferedImage

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class GraphicsRenderer {
    private GraphicsBuilder gb = new GraphicsBuilder()
    RenderingHints renderingHints = new RenderingHints(null)

    public GraphicsRenderer(){
       def helpers = ["Jdk6GraphicsBuilderHelper",
                      "SwingXGraphicsBuilderHelper",
                      "BatikGraphicsBuilderHelper"]
       helpers.each { helper ->
          try{
             Class helperClass = Class.forName("groovy.swing.j2d.${helper}")
             helperClass.registerOperations( gb )
          }catch( Exception e ){
             System.err.println("GraphicsRenderer: couldn't register ${helper}")
          }
       }
    }

    public GraphicsBuilder getGraphicsBuilder(){
       return gb
    }

    public BufferedImage render( int width, int height, Closure closure ){
       return render( width, height, gb.group(closure) )
    }

    public BufferedImage render( int width, int height, GraphicsOperation go ){
       return render( createImage( width, height ), go )
    }

    public BufferedImage render( Rectangle clip, Closure closure ){
       return render( clip, gb.group(closure) )
    }

    public BufferedImage render( Rectangle clip, GraphicsOperation go ){
       return render( createImage( clip.width as int, clip.height as int ), clip, go )
    }

    public BufferedImage render( BufferedImage dst, Closure closure ){
       return render( dst, gb.group(closure) )
    }

    public BufferedImage render( BufferedImage dst, GraphicsOperation go ){
       return render( dst, [0,0,dst.width,dst.height] as Rectangle, go )
    }

    public BufferedImage render( BufferedImage dst, Rectangle clip, Closure closure ){
       return render( dst, clip, gb.group(closure) )
    }

    public BufferedImage render( BufferedImage dst, Rectangle clip, GraphicsOperation go ){
       def context = new GraphicsContext()
       def g = dst.createGraphics()
       g.renderingHints = renderingHints
       def cb = g.clipBounds
       g.setClip( clip )
       context.g = g
       go.execute( context )
       g.dispose()
       if( !cb ) g.setClip( cb )
       return dst
    }

    private BufferedImage createImage( int width, int height ){
       GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
       if( ge.isHeadless() ){
           return BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB  )
       }else{
          for( gd in ge.getScreenDevices() ){
             GraphicsConfiguration[] gc = gd.configurations
             return gc[0].createCompatibleImage( width as int, height as int, Transparency.BITMASK as int )
          }
       }
       throw new IllegalStateException("Couldn't create BufferedImage")
    }
 }
