/*
 * Copyright 2007-2008 the original author or authors.
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

package groovy.swing.j2d.svg

import java.awt.*
import groovy.swing.j2d.*

import org.apache.batik.svggen.SVGGraphics2D
import org.apache.batik.dom.svg.SVGDOMImplementation

import org.w3c.dom.Document
import org.w3c.dom.DOMImplementation


/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class SVGRenderer {    
    private GraphicsBuilder gb
    
    public SVGRenderer(){
       gb = new GraphicsBuilder()
    }

    /**
     * Writes an svg document to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     *
     * @param filename the name of the file where the movie will be written
     * @param width the width of the svg document
     * @param height the height of the svg document
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
     * @param width the width of the svg document
     * @param height the height of the svg document
     * @param go any GraphicsOperation
     *
     * @throws IOException if the file can't be created and writen to.
     */
    public void renderToFile( String filename, int width, int height, GraphicsOperation go ){     
       assert filename : "Must define a value for filename"
        
       filename -= ".svg"
       def parentDir = null
       def fileSeparator = "/"

       if( filename.lastIndexOf(fileSeparator) != -1 ){
          def dirs = filename[0..(filename.lastIndexOf(fileSeparator)-1)]
          parentDir = new File(dirs)
          parentDir.mkdirs()
       }
       
       // Get a DOMImplementation.
       DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation()

       // Create an instance of org.w3c.dom.Document.
       String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
       Document document = domImpl.createDocument(svgNS, "svg", null)

       // Create an instance of the SVG Generator.
       SVGGraphics2D svgGenerator = new SVGGraphics2D(document)

       def context = new GraphicsContext()
       context.g = svgGenerator
       context.g.color = Color.BLACK
       context.g.clip = new Rectangle(0,0,width,height)
       context.g.setSVGCanvasSize( new Dimension(width,height) )
       go.execute(context)

       // Finally, stream out SVG to the standard output using
       // UTF-8 encoding.
       Writer out = new OutputStreamWriter(new FileOutputStream(filename+".svg"), "UTF-8")
       svgGenerator.stream(out)
    }
}