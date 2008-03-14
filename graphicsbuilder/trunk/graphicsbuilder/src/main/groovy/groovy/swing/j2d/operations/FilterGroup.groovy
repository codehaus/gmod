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

import java.awt.Shape
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.ObservableSupport
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class FilterGroup extends ObservableSupport {
    private List filters = []
    int offset = 10

    public List getFilters(){
       return Collections.unmodifiableList(filters)
    }
    
    public FilterProvider getAt( int index ) {
    	return this.@filters[index]
    }

    public FilterProvider getAt( String name ) {
    	return this.@filters.find { it?.name == name }
    }

    public void addFilter( FilterProvider filter ) {
        if( !filter ) return
        this.@filters << filter
        filter.addPropertyChangeListener( this )
        firePropertyChange( "size", this.@filters.size()-1, this.@filters.size() )
    }

    public void removeFilter( FilterProvider filter ) {
        if( !filter ) return
        filter.removePropertyChangeListener( this )
        this.@filters.remove( filter )
        firePropertyChange( "size", this.@filters.size()+1, this.@filters.size() )
    }

    public boolean isEmpty() {
       return this.@filters.isEmpty()
    }
    
    public void clear() {
       if( this.@filters.isEmpty() ) return
       int actualSize = this.@filters.size()
       this.@filters.clear()
       firePropertyChange( "size", actualSize, 0 )
    }
    
    public int getSize() {
       return this.@filters.size()
    }

    public void propertyChange( PropertyChangeEvent event ) {
       firePropertyChange( new ExtPropertyChangeEvent(this,event) )
    }

    public BufferedImage apply( BufferedImage image, Shape clip ) {
       BufferedImage dst = null
       this.@filters.each { filter ->
          if( filter.enabled ){
             if( !dst ){
                dst = filter.filter( image, null, clip )
             }else{
                dst = filter.filter( dst, dst, clip )
             }
          }
       }
       return dst ?: image
    }
    
    public String toString() {
    	"filters$filters"
    }
    
    /* ===== OPERATOR OVERLOADING ===== */

    public FilterGroup leftShift( FilterProvider filter ) {
       addFilter( filter )
       this
    }
}
