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

package groovy.swing.j2d.impl;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class MethodInvokingGraphicsOperation extends AbstractGraphicsOperation implements
      VerifiableGraphicsOperation {
   private String methodName;
   private Map optionalArguments;

   public MethodInvokingGraphicsOperation( String name, String methodName, String[] args ) {
      this( name, methodName, args, new String[0] );
   }

   public MethodInvokingGraphicsOperation( String name, String methodName, String[] args,
         String[] optional ) {
      super( name, args );
      this.methodName = methodName;
      optionalArguments = new HashMap();
      for( int i = 0; i < optional.length; i++ ){
         optionalArguments.put( optional[i], new NullValue() );
      }
   }

   public MethodInvokingGraphicsOperation( String methodName, String[] args ) {
      this( methodName, methodName, args, new String[0] );
   }

   public MethodInvokingGraphicsOperation( String methodName, String[] args, String[] optional ) {
      this( methodName, methodName, args, optional );
   }

   public void execute( Graphics2D g, ImageObserver observer ) {
      setupProperties();
      executeOptional( g, observer );
      invokeGraphingMethod( g, methodName );
      afterExecute( g );
   }

   public String getMethodName() {
      return methodName;
   }

   public Object getProperty( String name ) {
      Object value1 = super.getProperty( name );
      Object value2 = optionalArguments.get( name );
      return value1 == null ? (value2 instanceof NullValue ? null : value2)
            : value1 instanceof NullValue ? null : value1;
   }

   public void setOptionalArguments( Map optionalArguments ) {
      this.optionalArguments = optionalArguments;
   }

   public void setProperty( String name, Object value ) {
      if( optionalArguments.containsKey( name ) ){
         optionalArguments.put( name, value );
      }else{
         super.setProperty( name, value );
      }
   }

   public void verify() {
      for( Iterator entries = getParameters().entrySet()
            .iterator(); entries.hasNext(); ){
         Map.Entry entry = (Map.Entry) entries.next();
         if( entry.getValue() instanceof NullValue ){
            throw new IllegalStateException( "Property '" + entry.getKey() + "' for '" + methodName
                  + "()' has no value" );
         }
      }
   }

   protected void afterExecute( Graphics2D g ) {
   }

   protected void executeOptional( Graphics2D g, ImageObserver observer ) {
   }

   protected final Map getOptionalArguments() {
      return optionalArguments;
   }

   protected final void invokeGraphingMethod( Graphics2D g, String methodName ) {
      Map args = getParameters();
      Object[] arguments = new Object[args.size()];
      int i = 0;
      for( Iterator keys = args.keySet()
            .iterator(); keys.hasNext(); ){
         arguments[i++] = getParameterValue( (String) keys.next() );
      }
      InvokerHelper.invokeMethod( g, methodName, arguments );
   }

   protected void setupProperties() {
   }
}