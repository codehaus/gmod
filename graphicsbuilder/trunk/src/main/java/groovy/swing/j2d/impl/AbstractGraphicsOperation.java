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

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;
import groovy.swing.j2d.GraphicsOperation;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractGraphicsOperation extends GroovyObjectSupport implements
      GraphicsOperation {
   private String name;
   private Map parameters = new LinkedHashMap();

   public AbstractGraphicsOperation( String name, String[] parameters ) {
      this.name = name;
      if( parameters != null ){
         for( int i = 0; i < parameters.length; i++ ){
            this.parameters.put( parameters[i], new NullValue() );
         }
      }
   }

   public Shape getClip( Graphics2D g, ImageObserver observer ) {
      return null;
   }

   public String getName() {
      return name;
   }

   public String[] getParameterNames() {
      return (String[]) parameters.keySet()
            .toArray( new String[parameters.size()] );
   }

   public Object getParameterValue( String name ) {
      Object value = getProperty( name );
      if( value instanceof Closure ){
         value = ((Closure) value).call();
      }
      return value;
   }

   public Object getProperty( String name ) {
      Object value = parameters.get( name );
      return value instanceof NullValue || value == null ? null : value;
   }

   public boolean parameterHasValue( String name ) {
      return getProperty( name ) != null;
   }

   public void setParameterValue( String name, Object value ) {
      setProperty( name, value );
   }

   public void setProperty( String name, Object value ) {
      if( parameters.containsKey( name ) ){
         parameters.put( name, value );
      }else{
         throw new MissingPropertyException( name, getClass() );
      }
   }

   public String toString() {
      return name;
   }

   protected Map getParameters() {
      return parameters;
   }
}