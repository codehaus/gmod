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

package groovy.swing.j2d.impl

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import java.beans.PropertyChangeListener

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractNestingGraphicsOperation extends AbstractGraphicsOperation {
    private List operations = []
    private def g

    AbstractNestingGraphicsOperation( String name ) {
        super( name )
    }

    public void execute( GraphicsContext context ) {
       executeBeforeAll( context )
       if( operations ){
          if( !executeBeforeNestedOperations( context ) ) return
          operations.each { o -> executeNestedOperation(context,o) }
          if( !executeAfterNestedOperations( context ) ) return
       }
       executeOperation( context )
       executeAfterAll( context )
    }

    public void addPropertyChangeListener( PropertyChangeListener listener ) {
       super.addPropertyChangeListener( listener )
       operations.each { o -> o.addPropertyChangeListener( listener ) }
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
       super.removePropertyChangeListener( listener )
       operations.each { o -> o.removePropertyChangeListener( listener ) }
    }

    public void addOperation( GraphicsOperation operation ) {
        if( !operation ) return
        operations << operation
        operation.addPropertyChangeListener( this )
    }

    public void removeOperation( GraphicsOperation operation ) {
        if( !operation ) return
        operations.remove( operation )
        operation.removePropertyChangeListener( this )
    }

    public List getOperations() {
       operations
    }

    protected boolean executeBeforeNestedOperations( GraphicsContext context ) {
        true
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
       go.execute( context )
    }

    protected boolean executeAfterNestedOperations( GraphicsContext context ) {
        true
    }

    protected void executeOperation( GraphicsContext context ) {
        // empty
    }

    /*
    protected void setPropertyOnNestedOperation( GraphicsOperation go, String property ) {
       def value = this."$property"
       if( go.metaClass.hasProperty(go,property) && value != null && go."$property" == null )
          go."$property" = value
    }
    */
}