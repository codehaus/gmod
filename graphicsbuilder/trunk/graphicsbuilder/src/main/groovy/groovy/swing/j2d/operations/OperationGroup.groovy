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

import java.beans.PropertyChangeEvent

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.impl.ObservableSupport
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class OperationGroup extends ObservableSupport {
    private List operations = []

    public List getOperations(){
       return Collections.unmodifiableList(operations)
    }
    
    public GraphicsOperation getAt( int index ) {
    	return this.@operations[index]
    }

    public GraphicsOperation getAt( String name ) {
    	return this.@operations.find { it?.name == name }
    }

    public void addOperation( GraphicsOperation operation ) {
        if( !operation ) return
        this.@operations << operation
        operation.addPropertyChangeListener( this )
        firePropertyChange( "size", this.@operations.size()-1, this.@operations.size() )
    }

    public void removeOperation( GraphicsOperation operation ) {
        if( !operation ) return
        operation.removePropertyChangeListener( this )
        this.@operations.remove( operation )
        firePropertyChange( "size", this.@operations.size()+1, this.@operations.size() )
    }

    public boolean isEmpty() {
       return this.@operations.isEmpty()
    }
    
    public void clear() {
       if( this.@operations.isEmpty() ) return
       int actualSize = this.@operations.size()
       this.@operations.clear()
       firePropertyChange( "size", actualSize, 0 )
    }
    
    public int getSize() {
       return this.@operations.size()
    }

    public void propertyChange( PropertyChangeEvent event ) {
       firePropertyChange( new ExtPropertyChangeEvent(this,event) )
    }
    
    public String toString() {
    	"ops$operations"
    }
    
    public Iterator iterator() {
        this.@operations.iterator()
    }
    
    /* ===== OPERATOR OVERLOADING ===== */

    public OperationGroup leftShift( GraphicsOperation operation ) {
       addOperation( operation )
       this
    }
}