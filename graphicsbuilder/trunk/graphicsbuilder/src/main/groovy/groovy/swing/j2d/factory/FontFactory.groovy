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

package groovy.swing.j2d.factory

import groovy.swing.j2d.operations.Grouping
import groovy.swing.j2d.operations.misc.FontGraphicsOperation
import groovy.swing.j2d.operations.shapes.GlyphGraphicsOperation
import groovy.swing.j2d.operations.shapes.TextGraphicsOperation
import groovy.swing.j2d.operations.strokes.TextStrokeGraphicsOperation

import java.awt.Font

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class FontFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
            Map properties ) throws InstantiationException, IllegalAccessException {
        FontGraphicsOperation go = new FontGraphicsOperation()
        if( value != null && Font.class.isAssignableFrom( value.class ) ){
            go.font = value
            return go
        }

        def face = properties.remove( "face" )
        face = face != null ? face : "Default"
        def style = properties.remove( "style" )
        style = style != null ? getStyle(style): Font.PLAIN
        def size = properties.remove( "size" )
        size = size ? size : 12
        go.font = new Font( face, style as int, size as int )

        return go
    }

    public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
       if( parent instanceof Grouping || parent instanceof TextGraphicsOperation ||
           parent instanceof GlyphGraphicsOperation ) {
          parent.addOperation( child )
       }else if( parent instanceof TextStrokeGraphicsOperation ){
    	  parent.font = child 
       }else{   
          throw new IllegalArgumentException("font() can only be nested in [group,text,textStroke]")
       }
    }

    public boolean isLeaf(){
        return true
    }

    private def getStyle( style ){
       if( style instanceof String ){
          def s = Font.PLAIN
          style.split(/\|/).each { w ->
             if( w.equalsIgnoreCase("plain") ){ s |= Font.PLAIN }
             else if( w.equalsIgnoreCase("bold") ){ s |= Font.BOLD }
             else if( w.equalsIgnoreCase("italic") ) s |= Font.ITALIC
          }
          return s
       }
       return style
    }
}