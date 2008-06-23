/*
 * Copyright 2008 the original author or authors.
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
 * limitations under the License.
 */
package org.kordamp.groovy.util.impl

import java.lang.reflect.Method

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
interface ProxyHandler {
   ProxyMethodKey INVOKE_METHOD = new ProxyMethodKey("invokeMethod",[String,Object] as Class[])
   ProxyMethodKey GET_PROPERTY = new ProxyMethodKey("getProperty",[String] as Class[])
   ProxyMethodKey SET_PROPERTY = new ProxyMethodKey("setProperty",[String,Object] as Class[])
   ProxyMethodKey GET_METACLASS = new ProxyMethodKey(MetaClass,"getMetaClass")
   ProxyMethodKey SET_METACLASS = new ProxyMethodKey("setMetaClass",[MetaClass] as Class[])
   
   ProxyMethodKey METHOD_MISSING = new ProxyMethodKey("methodMissing",[String,Object] as Class[])
   ProxyMethodKey GET_PROPERTY_MISSING = new ProxyMethodKey("propertyMissing",[String] as Class[])
   ProxyMethodKey SET_PROPERTY_MISSING = new ProxyMethodKey("propertyMissing",[String,Object] as Class[])

   List GROOVY_OBJECT_METHODS = [
      GET_PROPERTY, SET_PROPERTY, GET_METACLASS, SET_METACLASS
   ]

   Object invokeCustom( String methodName, Object[] args )
   
   Object invokeCustom( Class returnType, String methodName, Class[] parameterTypes, Object[] args )
   
   Object invokeCustom( ProxyMethodKey methodKey, Object[] args )  
   
   Object getMethod( ProxyMethodKey methodKey )

   boolean isProperty( String name )

   Map getProperties()

   //Object getPropertyValue( String name )

   //void setPropertyValue( String name, value )

   //Object getMethod( Method method ) 

   //Object getMethod( String name ) 

   //boolean isValidMethodImpl( cl ) 

   // ---### GroovyObject interface ###---
   
   Object doGetAttribute( String name )

   void doSetAttribute( String name, Object value )

   Object doGetProperty( String name )

   void doSetProperty( String name, Object value )

   MetaClass doGetMetaClass()

   void doSetMetaClass( MetaClass metaClass )

   Object doMethodMissing( String name, Object args )

   Object doGetPropertyMissing( String name )

   void doSetPropertyMissing( String name, Object value )
}
