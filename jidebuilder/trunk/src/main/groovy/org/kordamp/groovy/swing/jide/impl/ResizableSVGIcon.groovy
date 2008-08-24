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
 * limitations under the License.
 */

package org.kordamp.groovy.swing.jide.impl

import java.awt.Component
import java.awt.Dimension
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import com.kitfox.svg.SVGDiagram
import com.kitfox.svg.app.beans.SVGIcon

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ResizableSVGIcon extends SVGIcon implements ComponentListener {
    boolean trackSize
    boolean retainAspectRatio = true
    int resizePercentage = 100

    private Dimension lastSize
    private Dimension iconSize
    private Component component
    private boolean sizeSet

    ResizableSVGIcon(){
       setAntiAlias( true )
       setScaleToFit( true )
    }

    public void setResizePercentage( int pct ){
       if( pct in (10..100) ){
          resizePercentage = pct
       }
    }

    void installSizeTracker( Component component ){
       if( component && trackSize ){
          this.component?.removeComponentListener( this )
          component.addComponentListener( this )
          lastSize = component.size
          this.component = component
       }
    }

    /**
     * Invoked when the component has been made invisible.
     */
    void componentHidden(ComponentEvent e){ /*empty */ }

    /**
     * Invoked when the component's position changes.
     */
    void componentMoved(ComponentEvent e){ /*empty */ }

    /**
     * Invoked when the component has been made visible.
     */
    void componentShown(ComponentEvent e){
       resize( e.component.size )
    }

    /**
     * Invoked when the component's size changes.
     */
    void componentResized(ComponentEvent e){
       resize( e.component.size )
    }

    /**
     * Returns true if the size has been set to a
     * non-<code>null</code> value otherwise returns false.
     *
     * @return true if <code>setSize</code> has been invoked
     *         with a non-null value.
     */
    public boolean isSizeSet(){
       return sizeSet
    }

    /**
     * Resizes this object so that it has width and height.
     *
     * @param d - the dimension specifying the new size of the object
     */
    public void setSize( Dimension size ){
       sizeSet = true
       _setSize( size )
    }

    private void resize( Dimension actualSize ){
       if( trackSize && actualSize.width != lastSize.width &&
             actualSize.height != lastSize.height  ){
            int width = actualSize.width * resizePercentage / 100
            int height = actualSize.height * resizePercentage / 100
            _setSize( new Dimension(width,height) )
         }
    }

    private void _setSize( Dimension size ){
       if( retainAspectRatio ){
          int width = size.width
          int height = size.height
          if( iconSize == null ){ cacheIconSize() }
          int nheight = Math.round( width * iconSize.height / iconSize.width )
          int nwidth = Math.round( height * iconSize.width / iconSize.height )
          if( nheight <= height ){
             height = nheight
          }else{
             width = nwidth
          }
          setPreferredSize( new Dimension(width,height) )
       }else{
          setPreferredSize( size )
       }
    }

    private void cacheIconSize(){
       SVGDiagram diagram = this.svgUniverse.getDiagram(this.svgURI)
       if( diagram == null ){ return }
       iconSize = new Dimension( (int)diagram.getWidth(), (int)diagram.getHeight() )
    }
}