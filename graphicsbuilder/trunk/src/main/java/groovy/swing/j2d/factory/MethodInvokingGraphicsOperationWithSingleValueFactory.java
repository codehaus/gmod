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

import groovy.swing.j2d.impl.MethodInvokingGraphicsOperation;
import groovy.util.FactoryBuilderSupport;

import java.util.Map;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class MethodInvokingGraphicsOperationWithSingleValueFactory extends AbstractGraphicsOperationFactory {
   private String methodName;
   private String parameter;
   private Class parameterClass;

   public MethodInvokingGraphicsOperationWithSingleValueFactory( String methodName, String parameter,
         Class parameterClass ) {
      this.methodName = methodName;
      this.parameter = parameter;
      this.parameterClass = parameterClass;
   }

   public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
         Map properties ) throws InstantiationException, IllegalAccessException {
      MethodInvokingGraphicsOperation go = new MethodInvokingGraphicsOperation( methodName,
            new String[] { parameter } );
      if( value != null && parameterClass.isAssignableFrom( value.getClass() ) ){
         go.setProperty( parameter, value );
      }
      return go;
   }
}