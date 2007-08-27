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

package groovy.swing.j2d.factory;

import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.GraphicsBuilder
import groovy.swing.j2d.impl.ContextualGraphicsOperation
import groovy.swing.j2d.impl.StrokingGraphicsOperation
import groovy.swing.j2d.impl.StrokingAndFillingGraphicsOperation
import groovy.util.AbstractFactory
import groovy.util.FactoryBuilderSupport

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GraphicsOperationBeanFactory extends AbstractGraphicsOperationFactory {
     private Class beanClass
     private boolean leaf

     GraphicsOperationBeanFactory( beanClass, leaf ){
         this.beanClass = beanClass
         this.leaf = leaf
     }

     public boolean isLeaf(){
         return leaf
     }

     public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
             Map properties ) throws InstantiationException, IllegalAccessException {
        return wrap( newBean( builder, name, value, properties ) )
     }

     private GraphicsOperation newBean( FactoryBuilderSupport builder, Object name, Object value,
             Map properties ) throws InstantiationException, IllegalAccessException {
         if( FactoryBuilderSupport.checkValueIsTypeNotString( value, name, beanClass ) ){
             return value
         }else{
             return beanClass.newInstance()
         }
     }
}