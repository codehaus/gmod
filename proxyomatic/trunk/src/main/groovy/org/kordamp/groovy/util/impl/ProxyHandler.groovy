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
   MethodKey INVOKE_METHOD = new MethodKey("invokeMethod",[String,Object] as Class[])
   MethodKey GET_PROPERTY = new MethodKey("getProperty",[String] as Class[])
   MethodKey SET_PROPERTY = new MethodKey("setProperty",[String,Object] as Class[])
   MethodKey GET_METACLASS = new MethodKey(MetaClass,"getMetaClass")
   MethodKey SET_METACLASS = new MethodKey("setMetaClass",[MetaClass] as Class[])
   
   MethodKey METHOD_MISSING = new MethodKey("methodMissing",[String,Object] as Class[])
   MethodKey GET_PROPERTY_MISSING = new MethodKey("propertyMissing",[String] as Class[])
   MethodKey SET_PROPERTY_MISSING = new MethodKey("propertyMissing",[String,Object] as Class[])

   List GROOVY_OBJECT_METHODS = [
      GET_PROPERTY, SET_PROPERTY, GET_METACLASS, SET_METACLASS
   ]

   Object invokeCustom( String methodName, Object[] args )
   
   Object invokeCustom( Class returnType, String methodName, Class[] parameterTypes, Object[] args )
   
   Object invokeCustom( MethodKey methodKey, Object[] args )  
   
   Object getMethod( MethodKey methodKey )

   boolean isProperty( String name )

   Map getProperties()

   //Object getPropertyValue( String name )

   //void setPropertyValue( String name, value )

   //Object getMethod( Method method ) 

   //Object getMethod( String name ) 

   //boolean isValidMethodImpl( cl ) 

   // ---### GroovyObject interface ###---

   Object groovyObjectGetProperty( String name )

   void groovyObjectSetProperty( String name, Object value )

   MetaClass groovyObjectGetMetaClass()

   void groovyObjectSetMetaClass( MetaClass metaClass )

   Object groovyObjectMethodMissing( String name, Object args )

   Object groovyObjectGetPropertyMissing( String name )

   void groovyObjectSetPropertyMissing( String name, Object value )
}
