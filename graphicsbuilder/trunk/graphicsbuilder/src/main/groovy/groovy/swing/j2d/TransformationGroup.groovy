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

package groovy.swing.j2d

import java.awt.geom.AffineTransform
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.impl.ObservableSupport

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TransformationGroup extends ObservableSupport {
    private List transformations = []
    private AffineTransform transform = new AffineTransform()

    public void addTransformation( Transformation transformation ) {
        if( !transformation ) return
        transformations << transformation
        transformation.addPropertyChangeListener( this )
    }

    public void removeOperation( Transformation transformation ) {
        if( !transformation ) return
        transformation.removePropertyChangeListener( this )
        transformations.remove( transformation )
    }

    public List getTransformations() {
        transformations
    }

    public AffineTransform getTransform() {
        if( this.@transform.isIdentity() ){
           calculateTransform()
        }
        this.@transform
    }

    public void propertyChange( PropertyChangeEvent event ) {
       firePropertyChange( "transform", this.@transform, calculateTransform() )
    }

    private void calculateTransform(){
        this.@transform = new AffineTransform()
        transformations.each { t -> this.@transform.concatenate(t.transform) }
    }
}