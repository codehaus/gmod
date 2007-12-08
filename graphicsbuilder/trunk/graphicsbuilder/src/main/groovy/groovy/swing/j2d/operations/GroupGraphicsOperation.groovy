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

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.Grouping
import groovy.swing.j2d.Transformable
import groovy.swing.j2d.Transformation
import groovy.swing.j2d.TransformationGroup

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GroupGraphicsOperation extends AbstractGraphicsOperation implements
   Transformable, Grouping {
    protected static optional = ['borderColor','borderWidth','fill']

    TransformationGroup transformations
    private List operations = []

    // properties
    def borderColor
    def borderWidth
    def fill

    public GroupGraphicsOperation( String name ) {
        super( name )
    }

    public final void execute( GraphicsContext context ) {
        def g = context.g
        if( transformations ){
           context.g = context.g.create()
           context.g.transform( transformations.getTransform() )
        }
        doExecute( context )
        if( transformations ){
           context.g.dispose()
           context.g = g
        }
    }

    // Transformable
    public void setTransformationGroup( TransformationGroup transformationGroup ) {
        transformations = transformationGroup
    }
   
    public TransformationGroup getTransformationGroup() {
        transformations
    }
    
    // Grouping
    public void addOperation( GraphicsOperation operation ) {
        operations << operation
    }
    public void removeOperation( GraphicsOperation operation ) {
       operations.remove( operation )
    }
    public List getOperations() {
       operations
    }

    protected boolean executeBeforeNestedOperations( GraphicsContext context ) {
        true
    }

    protected void executeNestedOperation( GraphicsContext context, GraphicsOperation go ) {
       if( go.metaClass.hasProperty(go,"borderColor") && borderColor != null ) go.borderColor = borderColor
       if( go.metaClass.hasProperty(go,"borderWidth") && borderWidth ) go.borderWidth = borderWidth
       if( go.metaClass.hasProperty(go,"fill") && fill != null ) go.fill = fill
       go.execute( context )
    }

    protected boolean executeAfterNestedOperations( GraphicsContext context ) {
        true
    }

    protected void executeOperation( GraphicsContext context ) {
        // empty
    }

    private void doExecute( GraphicsContext context ) {
       if( operations ){
          if( !executeBeforeNestedOperations( context ) ) return
          operations.each { o -> executeNestedOperation(context,o) }
          if( !executeAfterNestedOperations( context ) ) return
       }
       executeOperation( context )
    }
}