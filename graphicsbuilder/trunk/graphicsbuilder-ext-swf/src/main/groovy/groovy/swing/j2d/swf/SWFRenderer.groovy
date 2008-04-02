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

package groovy.swing.j2d.swf

import java.awt.Rectangle
import groovy.swing.j2d.GraphicsBuilder
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.*
//import groovy.swing.j2d.impl.InterceptableGraphics

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class SWFRenderer {
    private GraphicsBuilder gb
    
    public SWFRenderer(){
       gb = new GraphicsBuilder()
    }

    /**
     * Writes a swf movie to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     *
     * @param filename the name of the file where the movie will be written
     * @param width the width of the movie
     * @param height the height of the movie
     * @param closure a closure containg GraphicsBuilder's nodes
     *
     * @throws IOException if the file can't be created and writen to.
     */
    public void renderToFile( String filename, int width, int height, Closure closure ){
       renderToFile( filename, width, height, gb.group(closure) )
    }

    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     *
     * @param filename the name of the file where the movie will be written
     * @param width the width of the movie
     * @param height the height of the movie
     * @param go any GraphicsOperation
     *
     * @throws IOException if the file can't be created and writen to.
     */
    public void renderToFile( String filename, int width, int height, GraphicsOperation go ){
       def fileSeparator = "/" /*System.getProperty("file.separator")*/
       def file = null

       if( filename.lastIndexOf(fileSeparator) != -1 ){
          def dirs = filename[0..(filename.lastIndexOf(fileSeparator)-1)]
          def fname = filename[(filename.lastIndexOf(fileSeparator)+1)..-1]
          File parent = new File(dirs)
          parent.mkdirs()
          file = new File(parent,fname)
       }else{
          file = new File(filename)
       }
       
       /*
       InterceptableGraphics ig = new InterceptableGraphics()
       ig.drawInterceptor = { g, s -> println s }
       ig.clip = new Rectangle( 0, 0, width, height )
       GraphicsContext context = new GraphicsContext(g:ig)
       go.execute( context )
       */
       
       def swf = new SWFMovie()
    }
}