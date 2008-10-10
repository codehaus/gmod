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

package groovy.swing.j2d.swf

import java.awt.*
import java.awt.geom.PathIterator
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import groovy.text.SimpleTemplateEngine
import groovy.swing.j2d.*
import groovy.swing.j2d.operations.*
import groovy.swing.j2d.operations.misc.*
import groovy.swing.j2d.operations.shapes.*

import com.flagstone.transform.*
import com.flagstone.transform.util.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class SWFRenderer {
    private static String HTML_TEMPLATE
    
    static {
       Thread.start {
          HTML_TEMPLATE = Thread.currentThread().contextClassLoader.
               getResourceAsStream("groovy/swing/j2d/swf/html_template.txt").text
       }
    }
    
    private GraphicsBuilder gb
    private def templateEngine = new SimpleTemplateEngine()
    
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
       renderToFile( filename, [width:width, height:height], gb.group(closure) )
    }
     
    /**
     * Writes a swf movie to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator), the params Map
     * must have the following keys<ul>
     * <li>width - the width of the movie</li>
     * <li>height - the height of the movie</li>
     * </ul>
     * The following keys are optional<ul>
     * <li>frameRate - defaults to 1.0f</li>
     * <li>background - defaults to Color.WHITE</li>
     * </ul>
     *
     * @param filename the name of the file where the movie will be written
     * @param params a map of parameters
     * @param closure a closure containg GraphicsBuilder's nodes
     *
     * @throws IOException if the file can't be created and writen to.
     */
    public void renderToFile( String filename, Map params, Closure closure ){
       renderToFile( filename, params, gb.group(closure) )
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
       renderToFile( filename, [width:width, height:height], go )
    }
     
    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator), the params Map
     * must have the following keys<ul>
     * <li>width - the width of the movie</li>
     * <li>height - the height of the movie</li>
     * </ul>
     * The following keys are optional<ul>
     * <li>frameRate - defaults to 1.0f</li>
     * <li>background - defaults to Color.WHITE</li>
     * </ul>
     *
     * @param filename the name of the file where the movie will be written
     * @param params a map of parameters
     * @param go any GraphicsOperation
     *
     * @throws IOException if the file can't be created and writen to.
     */
    public void renderToFile( String filename, Map params, GraphicsOperation go ){
       assert filename : "Must define a value for filename"
       assert params.width : "Must define a value for width"
       assert params.height : "Must define a value for height"
        
       filename -= ".swf"
       def file = filename
       def parentDir = null
       def fileSeparator = "/"

       if( filename.lastIndexOf(fileSeparator) != -1 ){
          def dirs = filename[0..(filename.lastIndexOf(fileSeparator)-1)]
          file = filename[(filename.lastIndexOf(fileSeparator)+1)..-1]
          parentDir = new File(dirs)
          parentDir.mkdirs()
       }
       
       def context = new GraphicsContext()
       context.g = GraphicsBuilderHelper.createCompatibleImage(params.width,params.height).getGraphics()
       context.g.color = Color.BLACK
       context.g.clip = new Rectangle(0,0,params.width,params.height)
       
       def movie = new FSMovie()
       movie.frameRate = params.frameRate ?: 1.0f
       movie.frameSize = new FSBounds(0, 0, params.width*20, params.height*20)
       def background = params.background ?: 'white'
       movie.add( new FSSetBackgroundColor(color(background)) )
       
       def content = processOperation(go, [movie:movie, context:context, layer:2] )
       if( content ) {
           movie.add(content.object)
           movie.add(new FSPlaceObject(content.object.identifier, 
                                       1, 
                                       content.bounds.x as int, 
                                       content.bounds.y as int))
       }
       movie.add( new FSShowFrame() )
       movie.encodeToFile(filename+".swf")
       
       def binding = [width:params.width, height:params.height, file:file]
       def template = templateEngine.createTemplate(HTML_TEMPLATE).make(binding)
       def html = parentDir ? new File(parentDir,file+".html") : new File(file+".html")
       html.text = template.toString()
    }
    
    private def color( clr ) {
       clr = ColorCache.getColor(clr)
       return new FSColor( clr.red, clr.green, clr.blue, clr.alpha )
    }
    
    private boolean areEqual( Color c1, Color c2 ) {
       c1.red == c2.red &&
       c1.green == c2.green &&
       c1.blue == c2.blue &&
       c1.alpha == c2.alpha 
    }
    
    private def processOperation( GraphicsOperation go, context ) {
       switch( go ) {
          case ShapeProvider:          return createShape(go,context)
          case OutlineProvider:        return createOutline(go,context)
          case ImageGraphicsOperation: return createImage(go,context)
          case GroupGraphicsOperation: return createGroup(go,context)
          default:
             // unsupported operation, skip it ??
             System.err.println "SWFRenderer: unsupported operation: $go"
             return null
       }
    }
    
    private def createGroup( GraphicsOperation go, context ) {       
       go.execute(context.context)
       if( go.asImage ) return null
       
       def bs = go.runtime.boundingShape.bounds
       
       def clip = new FSDefineMovieClip(context.movie.newIdentifier())
       go.operations.eachWithIndex { child, index ->
          def content = processOperation(child, context)
          if( content ){
             clip.add(content.object)
             clip.add(new FSPlaceObject(content.object.identifier, 
                                        context.layer++, 
                                        content.bounds.x as int, 
                                        content.bounds.y as int))
          }
       }
       [object:clip, bounds:[x:bs.x,y:bs.y]]
    }

    private def createShape( GraphicsOperation go, context ) {
       go.execute(context.context)
       if( go.asShape || go.asImage ) return null
       
       def bc = go.runtime.borderColor
       def st = go.runtime.stroke
       def bs = go.runtime.boundingShape.bounds
       def fl = go.runtime.fill
       
       def lineStyles = null
       switch( st ) {
          case BasicStroke:
             lineStyles = new FSSolidLine(st.lineWidth as int, color(bc))
       }
       if( !lineStyles ) {
          lineStyles = new FSSolidLine(1i, color('none'))
       }
       
       def fillStyles = null
       switch( fl ) {
          case Color: 
             fillStyles = new FSSolidFill(color(fl)) 
             break
       }
       if( !fillStyles ){
          fillStyles = new FSSolidFill(color('none'))
       }
       
       def shape = new FSShapeConstructor()
       shape.COORDINATES_ARE_PIXELS = true
       shape.add( lineStyles )
       shape.add( fillStyles )
       buildShapeOrOutline( go, shape, true )

       [object:shape.defineShape(context.movie.newIdentifier()), bounds:[x:bs.x, y:bs.y]]
    }

    private def createOutline( GraphicsOperation go, context ) {
       go.execute(context.context)
       if( go.asShape || go.asImage ) return null
       
       def bc = go.runtime.borderColor
       def bw = go.runtime.borderWidth
       def st = go.runtime.stroke
       def bs = go.runtime.boundingShape.bounds
       
       def lineStyles = null
       switch( st ) {
          case BasicStroke:
             lineStyles = new FSSolidLine(st.lineWidth as int, color(bc))
       }
       if( !lineStyles ) {
          lineStyles = new FSSolidLine(1i, color('none'))
       }
       
       def shape = new FSShapeConstructor()
       shape.COORDINATES_ARE_PIXELS = true
       shape.add( lineStyles )    
       buildShapeOrOutline( go, shape, false )
       
       [object:shape.defineShape(context.movie.newIdentifier()), bounds:[x:bs.x, y:bs.y]]
    }

    private def createImage( GraphicsOperation go, context ) {
       go.execute(context.context)
       def image = go.hasFilters() ? go.runtime.filteredImage : go.runtime.image
       def bs = go.runtime.boundingShape.bounds
             
       createImage( image, bs.x, bs.y, context )
    }
    
    private void buildShapeOrOutline( GraphicsOperation go, FSShapeConstructor shape, boolean isShape ) {
       def path = go.runtime.globallyTransformedShape
       def pathIterator = path.getPathIterator(null)
       
       def lastMoveTo = []
       def newPath = true
       while( !pathIterator.isDone() ){
          float[] cds = [0,0,0,0,0,0] as float[]
          def segment = pathIterator.currentSegment(cds)
          int[] coords = cds as int[]
          switch( segment ){
             case PathIterator.SEG_MOVETO:
                if( newPath ){
                   shape.newPath()
                   isShape ? shape.selectStyle(0,0) : shape.selectLineStyle(0)
                   newPath = false
                }
                shape.move( coords[0], coords[1] )
                lastMoveTo[0] = coords[0]
                lastMoveTo[1] = coords[1]
                break
             case PathIterator.SEG_LINETO:
                shape.line( coords[0], coords[1] )
                break
             case PathIterator.SEG_QUADTO:
                shape.curve( *coords[0..3] )
                break
             case PathIterator.SEG_CUBICTO:
                shape.curve( *coords )
                break
             case PathIterator.SEG_CLOSE:
                shape.line( lastMoveTo[0], lastMoveTo[1] )
                shape.closePath()
                //newPath = true
                break
          }
          pathIterator.next()
       }
    }
    
    private def createImage( image, x, y, context ) {
       def file = null
       
       try {
          file = File.createTempFile("groovy2swf",".png")
          ImageIO.write( image, "png", file )
       
          def imageConstructor = new FSImageConstructor(file.absolutePath)
          int imageId = context.movie.newIdentifier()
          int shapeId = context.movie.newIdentifier()
       
          context.movie.add(imageConstructor.defineImage(imageId))
          def shape = imageConstructor.defineEnclosingShape(shapeId, imageId, x as int, y as int, null)
          return [object:shape, bounds:[x:x*20,y:y*20]]
       
       }catch( Exception e ){
          e.printStackTrace()
       
       }finally{   
          file?.delete()
       }
    }
}