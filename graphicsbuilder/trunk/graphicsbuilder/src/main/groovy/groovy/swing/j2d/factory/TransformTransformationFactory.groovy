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

import java.awt.geom.AffineTransform
import groovy.swing.j2d.operations.Transformation
import groovy.swing.j2d.operations.TransformationGroup
import groovy.swing.j2d.operations.transformations.TransformTransformation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TransformTransformationFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
          Map properties ) throws InstantiationException, IllegalAccessException {
        if( value != null ){
            if( TransformTransformation.class.isAssignableFrom( value.getClass() ) ){
                return value
            }else if( value instanceof AffineTransform ){
               return new TransformTransformation( transform: value )
            }
        }

        return new TransformTransformation()
    }

    public boolean isLeaf(){
       return true
    }

    public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
       if( !(parent instanceof TransformationGroup) ){
          throw new IllegalArgumentException("Transforms are not allowed outside a 'transformations' node")
       }
       parent.addTransformation(child)
    }
}
