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

import groovy.swing.j2d.GraphicsOperation;
import groovy.swing.j2d.GraphicsBuilder;
import groovy.swing.j2d.impl.ContextualGraphicsOperation;
import groovy.swing.j2d.impl.FillSupportGraphicsOperation;
import groovy.swing.j2d.impl.FillingGraphicsOperation;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

import java.util.List;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractGraphicsOperationFactory extends AbstractFactory {
   public void onNodeCompleted( FactoryBuilderSupport builder, Object parent, Object node ) {
      if( parent instanceof FillSupportGraphicsOperation
            && node instanceof FillingGraphicsOperation ){
         Object fillValue = InvokerHelper.getProperty( parent, "fill" );
         if( fillValue != null && fillValue instanceof Boolean
               && !((Boolean) fillValue).booleanValue() ){
            return;
         }
         InvokerHelper.setProperty( parent, "fill", Boolean.TRUE );
      }
      if( parent instanceof ContextualGraphicsOperation ){
         ((ContextualGraphicsOperation) parent).addOperation( (GraphicsOperation) node );
      }else{
         List operations = ((GraphicsBuilder) builder).getOperations();
         if( operations != null && node instanceof GraphicsOperation ){
            operations.add( node );
         }
      }
   }
}