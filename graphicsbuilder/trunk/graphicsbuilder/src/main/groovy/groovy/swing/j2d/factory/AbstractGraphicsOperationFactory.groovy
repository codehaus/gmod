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

package groovy.swing.j2d.factory

import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.Grouping
import groovy.swing.j2d.OutlineProvider
import groovy.swing.j2d.ShapeProvider
import groovy.swing.j2d.Transformable
import groovy.swing.j2d.Transformation
import groovy.swing.j2d.TransformationGroup
import groovy.swing.j2d.operations.AreaGraphicsOperation
import groovy.swing.j2d.operations.GroupGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractGraphicsOperationFactory extends AbstractFactory {
    public void setParent( FactoryBuilderSupport builder, Object parent, Object child ){
       if( child instanceof Transformation ){
          if( parent instanceof TransformationGroup ){
             parent.addTransformation(child)
             return
          }else{
             throw new IllegalArgumentException("Transforms are not allowed outside a 'transformations' node")
          }
       }
       if( child instanceof TransformationGroup ){
          if( parent instanceof Transformable ){
             parent.transformationGroup = child
             return
          }else{
             throw new IllegalArgumentException("$parent does not support transformations")
          }
       }
       if( child instanceof ShapeProvider && parent instanceof AreaGraphicsOperation ){
          parent.addOperation( child )
          return
       }
       if( parent instanceof Grouping ){
          parent.addOperation( child )
          return
       }else{
          throw new IllegalArgumentException("$parent does not support nesting of other operations")
       }
   }
}