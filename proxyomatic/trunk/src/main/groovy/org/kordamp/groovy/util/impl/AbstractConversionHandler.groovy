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

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.ConversionHandler
import java.lang.reflect.Method

/**
 * General adapter for maps that support "overloaded methods" via MethodKey to any Java interface.<br/>
 * Based on org.codehaus.groovy.runtime.ConvertedMap by <a href="mailto:blackdrag@gmx.org">Jochen Theodorou</a> 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractConversionHandler extends ConversionHandler implements ProxyHandler {
   private ProxyingMetaClass proxyingMetaClass 
    
   protected AbstractConversionHandler( Object delegate, Class proxyClass ) {
      super( delegate )
      proxyingMetaClass = new ProxyingMetaClass( this, proxyClass )
   }
  
   public Object invokeCustom( Object proxy, Method method, Object[] args ) throws Throwable {
      return invokeCustom( /*proxy,*/ method.returnType, method.name, method.parameterTypes, args )
   }
   
   public Object invokeCustom( /*Object proxy,*/ String methodName, Object[] args ) {
      return invokeCustom( /*proxy,*/ Object, methodName, TypeUtils.castArgumentsToClassArray(args), args )
   }
   
   public Object invokeCustom( /*Object proxy,*/ Class returnType, String methodName, Class[] parameterTypes, Object[] args ) {
      return invokeCustom( /*proxy,*/ new MethodKey( returnType, methodName, parameterTypes), args )
   }
   
   public Object invokeCustom( /*Object proxy,*/ MethodKey methodKey, Object[] args ) {
      // 1st check for GroovyObject methods
      if( GROOVY_OBJECT_METHODS.contains(methodKey) ){
         // check if 'overridden'
         def methodImpl = getMethod(methodKey) 
         return isValidMethodImpl(methodImpl) ? methodImpl(*args) : callGroovyObjectMethod(methodKey,args)
      }

      // 2nd check if "invokeMethod" is provided
      def invokeMethodImpl = getMethod(INVOKE_METHOD)
      if( isValidMethodImpl(invokeMethodImpl) ) {
         return invokeMethodImpl(methodKey.name,args)
      }

      // 3rd try the method itself
      def methodImpl = getMethod(methodKey)
      if( !isValidMethodImpl(methodImpl) ) {
         // 4th try "methodMissing"
         return groovyObjectMethodMissing(methodKey.name,args)
      }
      return methodKey.parameterTypes.length == 0 ? methodImpl() : methodImpl(*args)
   }
  
   public String toString() {
      def toStringMethod = getMethod("toString")
      return toStringMethod ? toStringMethod() : DefaultGroovyMethods.toString(getDelegate())
   }

   protected abstract Object getMethod( MethodKey methodKey )

   protected abstract boolean isProperty( String name )

   protected abstract Object getPropertyValue( String name )

   protected abstract void setPropertyValue( String name, value )

   protected final Object getMethod( Method method ) {
      return getMethod( new MethodKey(method) )
   }

   protected final Object getMethod( String name ) {
      return getMethod( new MethodKey(name) )
   }
   
   protected boolean isValidMethodImpl( cl ) {
      cl && cl instanceof Closure
   }

   protected String methodSignature( String name, Object[] args ) {
      def types = args.asType(List).collect { TypeUtils.getShortName(it == null ? Object : it.getClass()) }
      return "$name(${types.join(',')})"
   }

   // ---### GroovyObject interface ###---

   protected Object callGroovyObjectMethod( MethodKey methodKey, Object[] args ) {
      switch( methodKey.name ) {
         case "getProperty":
            return groovyObjectGetProperty(*args)
         case "setProperty":
            return groovyObjectSetProperty(*args)
         case "getMetaClass":
            return groovyObjectGetMetaClass()
         case "setMetaClass":
            return groovyObjectSetMetaClass(*args)
      }
   }

   Object groovyObjectGetProperty( String name ) {
      // check if overridden
      def methodImpl = getMethod(GET_PROPERTY) 
      if( isValidMethodImpl(methodImpl) ) {
         return methodImpl(name)
      }

      // check if getter is available
      methodImpl = getMethod("get${name[0].toUpperCase()}${name[1..-1]}".toString()) 
      if( isValidMethodImpl(methodImpl) ) {
         return methodImpl(name)
      }

      // try 'direct' access
      if( isProperty(name) ) {
         return getPropertyValue(name)
      }

      return groovyObjectGetPropertyMissing(name)
   }

   void groovyObjectSetProperty( String name, Object value ) {
      // check if overridden
      def methodImpl = getMethod(SET_PROPERTY) 
      if( isValidMethodImpl(methodImpl) ) {
         methodImpl(name,value)
      }

      // check if getter is available
      methodImpl = getMethod("set${name[0].toUpperCase()}${name[1..-1]}".toString(),(value?value.getClass():Object)) 
      if( isValidMethodImpl(methodImpl) ) {
         methodImpl(name,value)
      }

      // try 'direct' access
      if( isProperty(name) ) {
         setPropertyValue(name,value)
      }

      groovyObjectSetPropertyMissing(name,value)
   }

   MetaClass groovyObjectGetMetaClass() {
      return proxyingMetaClass
   }

   void groovyObjectSetMetaClass( MetaClass metaClass ) {
      // metaClass should be read-only?
   }

   Object groovyObjectMethodMissing( String name, Object args ) {
      // check if overridden
      def methodImpl = getMethod(METHOD_MISSING) 
      if( isValidMethodImpl(methodImpl) ) {
         return methodImpl(name,args)
      }

      throw new UnsupportedOperationException("Method ${methodSignature(name,args)} is not implemented")
   }

   Object groovyObjectGetPropertyMissing( String name ) {
      // check if overridden
      def methodImpl = getMethod(GET_PROPERTY_MISSING) 
      if( isValidMethodImpl(methodImpl) ) {
         return methodImpl(name)
      }
      throw new MissingPropertyException( name, proxyClass )
   }

   void groovyObjectSetPropertyMissing( String name, Object value ) {
      // check if overridden
      def methodImpl = getMethod(SET_PROPERTY_MISSING) 
      if( isValidMethodImpl(methodImpl) ) {
         methodImpl(name,value)
      }
      throw new MissingPropertyException( name, proxyClass )
   }
}
