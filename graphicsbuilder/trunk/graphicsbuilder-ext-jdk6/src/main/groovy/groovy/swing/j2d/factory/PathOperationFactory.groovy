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
import groovy.swing.j2d.impl.ContextualGraphicsOperation
import groovy.swing.j2d.impl.PathOperation
import groovy.swing.j2d.operations.PathGraphicsOperation
import groovy.util.AbstractFactory
import groovy.util.FactoryBuilderSupport

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class PathOperationFactory extends AbstractFactory {
   private Class operationClass

   public PathOperationFactory( Class operationClass ) {
      this.operationClass = operationClass
   }

   public boolean isLeaf() {
      return true
   }

   public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
         Map properties ) throws InstantiationException, IllegalAccessException {
      if( FactoryBuilderSupport.checkValueIsTypeNotString( value, name, operationClass ) ){
         return value
      }else{
         return operationClass.newInstance()
      }
   }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
      if( parent instanceof ContextualGraphicsOperation ){
         GraphicsOperation delegate = ((ContextualGraphicsOperation) parent).getDelegate()
         if( delegate instanceof PathGraphicsOperation ){
            ((PathGraphicsOperation) delegate).addPathOperation( (PathOperation) child )
         }
      }else if( parent instanceof PathGraphicsOperation ){
         ((PathGraphicsOperation) parent).addPathOperation( (PathOperation) child )
      }
   }
}