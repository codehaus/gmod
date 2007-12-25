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

import java.awt.Shape
import java.awt.geom.AffineTransform
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TransformationGroup extends ObservableSupport implements Transformation {
    private static final String OLDVALUE = "_OLD_VALUE_"
    private static final String NEWVALUE = "_NEW_VALUE_"

    private List transformations = []

    public void addTransformation( Transformation transformation ) {
        if( !transformation ) return
        transformations << transformation
        transformation.addPropertyChangeListener( this )
    }

    public void removeTransformation( Transformation transformation ) {
        if( !transformation ) return
        transformation.removePropertyChangeListener( this )
        transformations.remove( transformation )
    }

    public boolean isEmpty() {
       return transformations.isEmpty()
    }

    public void propertyChange( PropertyChangeEvent event ) {
       firePropertyChange( "transform", OLDVALUE, NEWVALUE )
    }

    public AffineTransform getTransform() {
       return null
    }

    public Shape apply( Shape shape ) {
       if( isEmpty() ) return shape
       def transform = new AffineTransform()
       transformations.each { transformation ->
          def t = transformation.transform
          if( transformation instanceof TransformationGroup ){
             shape = transformation.apply( shape )
          }else if( t.isIdentity() ){
             // assume that it is a freeze transform
             if( !transform.isIdentity() ){
                // is there
                shape = transform.createTransformedShape( shape )
                transform = t.clone()
             }
          }else{
             transform.concatenate( t )
          }
       }

       if( !transform.isIdentity() ){
          return transform.createTransformedShape( shape )
       }else{
          return shape
       }
    }
}