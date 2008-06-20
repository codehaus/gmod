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

import org.codehaus.groovy.reflection.CachedClass
import org.codehaus.groovy.reflection.ReflectionCache
import org.codehaus.groovy.ast.ClassNode

/**
 * 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ProxyingMetaClass implements MetaClass {
   private ProxyHandler proxyHandler
   private Class proxyClass
   private ClassNode classNode
   
   private Map metaMethodCache = [:]
   private List flatMethodCache = []
   
   ProxyingMetaClass( ProxyHandler proxyHandler, proxyClass ) {
      this.proxyHandler = proxyHandler
      this.proxyClass = proxyClass
      this.classNode = new ClassNode(proxyClass)
      populateMethods()
   }
    
   public Object getAttribute( Class sender, Object receiver, String messageName, boolean useSuper ) {
      proxyHandler.groovyObjectGetProperty( receiver, messageName )
   }

   public ClassNode getClassNode() {
      return classNode
   }

   public List getMetaMethods() {
      // TODO Auto-generated method stub
      return null;
   }

   public List getMethods() {
      // TODO Auto-generated method stub
      return null;
   }

   public List getProperties() {
      // TODO Auto-generated method stub
      return null;
   }

   public Object getProperty( Class sender, Object receiver, String property, boolean isCallToSuper,
         boolean fromInsideClass ) {
      proxyHandler.groovyObjectGetProperty( property )
   }

   public void initialize() {
      // TODO Auto-generated method stub

   }

   public Object invokeMethod( Class sender, Object receiver, String methodName, Object[] arguments,
         boolean isCallToSuper, boolean fromInsideClass ) {
      proxyHandler.invokeCustom( methodName, arguments )
   }

   public Object invokeMissingMethod( Object instance, String methodName, Object[] arguments ) {
      proxyHandler.groovyObjectMethodMissing( methodName, arguments )
   }

   public Object invokeMissingProperty( Object instance, String propertyName, Object optionalValue, boolean isGetter ) {
      isGetter ? proxyHandler.groovyObjectGetPropertyMissing( propertyName ) : proxyHandler.groovyObjectSetPropertyMissing( propertyName, optionalValue )
   }

   public MetaMethod pickMethod( String methodName, Class[] arguments ) {
      // TODO Auto-generated method stub
      return null;
   }

   public int selectConstructorAndTransformArguments( int numberOfConstructors, Object[] arguments ) {
      // TODO Auto-generated method stub
      return 0;
   }

   public void setAttribute( Class sender, Object receiver, String messageName, Object messageValue, boolean useSuper,
         boolean fromInsideClass ) {
      proxyHandler.groovyObjectSetProperty( messageName, messageValue )
   }

   public void setProperty( Class sender, Object receiver, String property, Object value, boolean isCallToSuper,
         boolean fromInsideClass ) {
      proxyHandler.groovyObjectSetProperty( property, value )
   }

   public Object getAttribute( Object object, String attribute ) {
      proxyHandler.groovyObjectGetProperty( attribute )
   }

   public MetaMethod getMetaMethod( String name, Object[] args ) {
      // TODO Auto-generated method stub
      return null;
   }

   public MetaProperty getMetaProperty( String name ) {
      // TODO Auto-generated method stub
      return null;
   }

   public Object getProperty( Object object, String property ) {
      proxyHandler.groovyObjectGetProperty( property )
   }

   public MetaMethod getStaticMetaMethod( String name, Object[] args ) {
      // TODO Auto-generated method stub
      return null;
   }

   public Class getTheClass() {
      // TODO Auto-generated method stub
      return null;
   }

   public MetaProperty hasProperty( Object obj, String name ) {
      proxyHandler.isProperty( name )
   }

   public Object invokeConstructor( Object[] arguments ) {
      throw new UnsupportedOperationException("Can't invoke a constructor on proxy $theClass")
   }

   public Object invokeMethod( Object object, String methodName, Object[] arguments ) {
      return proxyHandler.invokeCustom( methodName, arguments )
   }

   public Object invokeMethod( Object object, String methodName, Object arguments ) {
      return proxyHandler.invokeCustom( methodName, [arguments] as Object[] )
   }

   public Object invokeStaticMethod( Object object, String methodName, Object[] arguments ) {
      return null
   }

   public List respondsTo( Object obj, String name ) {
      return []
   }

   public List respondsTo( Object obj, String name, Object[] argTypes ) {
      return [proxyHandler.getMethod( new MethodKey(methodName,TypeUtils.castArgumentsToClassArray(arguments)) )]
   }

   public void setAttribute( Object object, String attribute, Object newValue ) {
      proxyHandler.groovyObjectSetProperty( attribute, newValue )
   }

   public void setProperty( Object object, String property, Object newValue ) {
      proxyHandler.groovyObjectSetProperty( attribute, newValue )
   }
   
   // -------------------------------
   
   private void populateMethods() {
      
      proxyClass.methods.each { method ->
         Map methods = metaMethodCache.get(method.name,[:])
         def key = new MethodKey(method)
         methods[] = new ProxiedMetaMethod(key,proxyHandler,ReflectionCache.getCachedClass(proxyClass))
      }
   }
}