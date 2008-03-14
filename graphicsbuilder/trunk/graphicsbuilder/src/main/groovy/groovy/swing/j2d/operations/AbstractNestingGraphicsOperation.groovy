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

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.impl.ExtPropertyChangeEvent
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractNestingGraphicsOperation extends AbstractDisplayableGraphicsOperation {
    public static optional = AbstractDisplayableGraphicsOperation.optional + ['borderColor','borderWidth','fill']
    
    protected def gcopy
    protected OperationGroup operationGroup = new OperationGroup()
    
    def borderColor
    def borderWidth
    def fill

    AbstractNestingGraphicsOperation( String name ) {
        super( name )
        operationGroup.addPropertyChangeListener(this)
    }

    protected final void doExecute( GraphicsContext context ) {
       executeBeforeAll( context )
       if( !operationGroup.empty ){
          if( !executeBeforeNestedOperations( context ) ) return
          operations.each { o -> executeNestedOperation(context,o) }
          if( !executeAfterNestedOperations( context ) ) return
       }
       executeOperation( context )
       executeAfterAll( context )
    }

    public void addOperation( GraphicsOperation operation ) {
        operationGroup.addOperation( operation )
    }

    public void removeOperation( GraphicsOperation operation ) {
        operationGroup.removeOperation( operation )
    }

    public List getOperations() {
        operationGroup.operations
    }
    
    public OperationGroup getOps() {
        operationGroup
    }
    
    public abstract BufferedImage getImage()

    public void propertyChange( PropertyChangeEvent event ){
       if( event.source == operationGroup ){
          firePropertyChange( new ExtPropertyChangeEvent(this,event) )
       }else{
          super.propertyChange( event )
       }
    }

    /* ===== OPERATOR OVERLOADING ===== */

    public AbstractNestingGraphicsOperation leftShift( GraphicsOperation operation ) {
       addOperation( operation )
       this
    }

    /* ===== PROTECTED ===== */

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
    
    protected void executeBeforeAll( GraphicsContext context ) {

    }

    protected void executeAfterAll( GraphicsContext context ) {

    }
}
