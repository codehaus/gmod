/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
import org.codehaus.groovy.reflection.*
import org.codehaus.groovy.runtime.*
import org.codehaus.groovy.runtime.metaclass.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class RealizedProxyMetaClass extends MetaClassImpl {
   private final ProxyHandler proxyHandler
   private Map metaMethodCache = [:]
   private Map metaPropertyCache = [:]
   private List classMethods = []
    
   public RealizedProxyMetaClass( Class theClass, ProxyHandler proxyHandler ) {
      super( theClass )
      this.proxyHandler = proxyHandler
      initialize()
   }

   public synchronized void initialize() {
      if( !isInitialized() ) {
         populateMethods()
         populateProperties()
      }
      super.initialize()
   }

   // ---=== MutableMetaClass ===---
   
   public boolean isModified(){
      return false
   }

   public void addNewInstanceMethod( Method method ) {
      throw new UnsupportedOperationException("Can't modify metaClass of ${theClass.name}")
   }

   public void addNewStaticMethod( Method method ) {
      throw new UnsupportedOperationException("Can't modify metaClass of ${theClass.name}")
   }

   public void addMetaMethod( MetaMethod metaMethod ) {
      throw new UnsupportedOperationException("Can't modify metaClass of ${theClass.name}")
   }

   public void addMetaBeanProperty( MetaBeanProperty metaBeanProperty ){
      if( !isInitialized() ){
         super.addMetaBeanProperty( metaBeanProperty )
      }else{
         throw new UnsupportedOperationException("Can't modify metaClass of ${theClass.name}")
      }
   }
   
   public Object getAttribute( Class sender, Object receiver, String messageName, boolean useSuper ) {
      proxyHandler.doGetAttribute( messageName )
   }
   
   public void setAttribute( Class sender, Object receiver, String messageName, Object messageValue, boolean useSuper,
         boolean fromInsideClass ) {
      proxyHandler.doSetAttribute( messageName, messageValue )
   }
   
   public Object getAttribute( Object object, String attribute ) {
      proxyHandler.doGetAttribute( attribute )
   }
   
   public void setAttribute( Object object, String attribute, Object newValue ) {
      proxyHandler.doSetAttribute( attribute, newValue )
   }
   
   public Object getProperty( Class sender, Object receiver, String property, boolean isCallToSuper,
         boolean fromInsideClass ) {
      if( proxyHandler.isProperty(property) ){
         return proxyHandler.doGetProperty( property )
      }
      super.getProperty( sender, receiver, property, isCallToSuper, fromInsideClass )
   }
   
   public void setProperty( Class sender, Object receiver, String property, Object value, boolean isCallToSuper,
         boolean fromInsideClass ) {
      if( proxyHandler.isProperty(property) ){
         proxyHandler.doSetProperty( property, value )
         return
      }
      super.setProperty( sender, receiver, property, value, isCallToSuper, fromInsideClass )
   }
   
   public Object getProperty( Object object, String property ) {
      if( proxyHandler.isProperty(property) ){
         return proxyHandler.doGetProperty( property )
      }
      super.getProperty( object, property )
   }
   
   public void setProperty( Object object, String property, Object newValue ) {
      if( proxyHandler.isProperty(property) ){
         proxyHandler.doSetProperty( property, newValue )
      }
      super.setProperty( object, property, newValue )
   }
   
   public List getMetaMethods() {
      super.getMetaMethods() + classMethods
   }
   
   public List getMethods() {
      super.getMethods() + classMethods
   }
   
   public List getProperties() {
      super.getProperties() + metaPropertyCache.values()
   }
   
   public MetaProperty getMetaProperty( String name ) {
      def metaProperty = metaPropertyCache[name]
      if( metaProperty ) return metaProperty
      return super.getMetaProperty( name )
   }
   
   public MetaMethod pickMethod( String methodName, Class[] arguments ) {
      def key = new ProxyMethodKey(name, arguments)
      def metaMethod = metaMethodCache.get(name)?.get(key)
      if( metaMethod ) {
         return metaMethod
      }
      return super.pickMethod( methodName, arguments )
   }
   
   public Object invokeConstructor( Object[] arguments ) {
      throw new UnsupportedOperationException("Can't invoke a constructor on proxy $theClass")
   }
   
   public Object invokeMethod( Object object, String methodName, Object[] arguments ) {
      if( isProxyMetaMethod(methodName,arguments) ){
         return proxyHandler.invokeCustom( methodName, arguments )
      }
      return super.invokeMethod( object, methodName, arguments )
   }

   public Object invokeMethod( Object object, String methodName, Object arguments ) {
      if( isProxyMetaMethod(methodName,arguments) ){
         return proxyHandler.invokeCustom( methodName, [arguments] as Object[] )
      }
      return super.invokeMethod( object, methodName, arguments )
   }
   
   public Object invokeMethod( Class sender, Object receiver, String methodName, Object[] arguments,
         boolean isCallToSuper, boolean fromInsideClass ) {
      if( isProxyMetaMethod(methodName,arguments) ){
         return proxyHandler.invokeCustom( methodName, arguments )
      }
      return super.invokeMethod( sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass )
   }

   public Object invokeMissingMethod( Object instance, String methodName, Object[] arguments ) {
      proxyHandler.doMethodMissing( methodName, arguments )
   }

   public Object invokeMissingProperty( Object instance, String propertyName, Object optionalValue, boolean isGetter ) {
      isGetter ? proxyHandler.doGetPropertyMissing( propertyName ) : proxyHandler.doSetPropertyMissing( propertyName, optionalValue )
   }
   
   // -----------------------------------
   
   private void populateMethods() {
      def cachedClass = ReflectionCache.getCachedClass(theClass)
      theClass.methods.each { method ->
         if( method.name =~ /\$/ ) {
            // skip it
            return
         }
         Map methods = metaMethodCache.get(method.name,[:])
         def key = new ProxyMethodKey(method)
         def metaMethod = new ProxyMetaMethod( key, proxyHandler, cachedClass )
         methods[key] = metaMethod
         classMethods << metaMethod
      }
   }
   
   private void populateProperties() {
      proxyHandler.getProperties().each { name, value ->
         metaPropertyCache[name] = new ProxyMetaProperty( name, value != null ? value.getClass() : Object, proxyHandler )
      }
      // TODO consider read-only properties
      // TODO consider write-only properties
   }
   
   private boolean isProxyMetaMethod( String methodName, Object args ){
      def key = new ProxyMethodKey(methodName, args != null ? args.getClass() : Object )
      return metaMethodCache.get(methodName)?.get(key) != null
   }
   
   private boolean isProxyMetaMethod( String methodName, Object[] args ){
      def key = new ProxyMethodKey(methodName, TypeUtils.castArgumentsToClassArray(args) )
      return metaMethodCache.get(methodName)?.get(key) != null
   }
}
