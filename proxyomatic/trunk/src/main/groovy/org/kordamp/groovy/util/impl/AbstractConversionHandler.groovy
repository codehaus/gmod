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
 * General adapter for maps that support "overloaded methods" via ProxyMethodKey to any Java interface.<br/>
 * Based on org.codehaus.groovy.runtime.ConvertedMap by <a href="mailto:blackdrag@gmx.org">Jochen Theodorou</a> 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractConversionHandler extends ConversionHandler implements ProxyHandler {
   private RealizedProxyMetaClass realizedProxyMetaClass 
    
   protected AbstractConversionHandler( Object delegate, Class proxyClass ) {
      super( delegate )
      realizedProxyMetaClass = new RealizedProxyMetaClass( proxyClass, this )
   }
  
   public Object invokeCustom( Object proxy, Method method, Object[] args ) throws Throwable {
      return invokeCustom( /*proxy,*/ method.returnType, method.name, method.parameterTypes, args )
   }
   
   public Object invokeCustom( /*Object proxy,*/ String methodName, Object[] args ) {
      return invokeCustom( /*proxy,*/ Object, methodName, TypeUtils.castArgumentsToClassArray(args), args )
   }
   
   public Object invokeCustom( /*Object proxy,*/ Class returnType, String methodName, Class[] parameterTypes, Object[] args ) {
      return invokeCustom( /*proxy,*/ new ProxyMethodKey( returnType, methodName, parameterTypes), args )
   }
   
   public Object invokeCustom( /*Object proxy,*/ ProxyMethodKey methodKey, Object[] args ) {
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
         return doMethodMissing(methodKey.name,args)
      }
      return methodKey.parameterTypes.length == 0 ? methodImpl() : methodImpl(*args)
   }
  
   public String toString() {
      def toStringMethod = getMethod("toString")
      return toStringMethod ? toStringMethod() : DefaultGroovyMethods.toString(getDelegate())
   }

   protected abstract Object getMethod( ProxyMethodKey methodKey )

   protected abstract boolean isProperty( String name )

   protected abstract Object getPropertyValue( String name )

   protected abstract void setPropertyValue( String name, value )

   protected final Object getMethod( Method method ) {
      return getMethod( new ProxyMethodKey(method) )
   }

   protected final Object getMethod( String name ) {
      return getMethod( new ProxyMethodKey(name) )
   }
   
   protected final Object getMethod( String name, Class[] parameterTypes ) {
      return getMethod( new ProxyMethodKey(name,parameterTypes) )
   }
   
   protected boolean isValidMethodImpl( cl ) {
      cl && cl instanceof Closure
   }

   protected String methodSignature( String name, Object[] args ) {
      def types = args.asType(List).collect { TypeUtils.getShortName(it == null ? Object : it.getClass()) }
      return "$name(${types.join(',')})"
   }

   // ---### GroovyObject interface ###---

   protected Object callGroovyObjectMethod( ProxyMethodKey methodKey, Object[] args ) {
      switch( methodKey.name ) {
         case "getProperty":
            return doGetProperty(*args)
         case "setProperty":
            return doSetProperty(*args)
         case "getMetaClass":
            return doGetMetaClass()
         case "setMetaClass":
            return doSetMetaClass(*args)
      }
   }

   Object doGetAttribute( String name ) {
      if( isProperty(name) ) {
         return getPropertyValue(name)
      }

      throw new MissingFieldException( name, realizedProxyMetaClass.theClass )
   }   
   
   void doSetAttribute( String name, Object value ) {
      if( isProperty(name) ) {
         setPropertyValue(name,value)
      }

      throw new MissingFieldException( name, realizedProxyMetaClass.theClass )
   }   
   
   Object doGetProperty( String name ) {
      // check for 'metaClass' or 'class'
      if( name == 'metaClass' ) {
         return doGetMetaClass()
      }else if( name == "class" ) {
         return doGetMetaClass().theClass
      }

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

      return doGetPropertyMissing(name)
   }

   void doSetProperty( String name, Object value ) {
      // check for 'metaClass' or 'class'
      if( name == 'metaClass' ) {
         doSetMetaClass( (MetaClass) value )
         return
      }else if( name == "class" ) {
         throw new GroovyRuntimeException("'class' is a read-only property")
      }

      // check if overridden
      def methodImpl = getMethod(SET_PROPERTY) 
      if( isValidMethodImpl(methodImpl) ) {
         methodImpl(name,value)
         return
      }

      // check if getter is available
      def parameterTypes = value?[value.getClass()]:[Object]
      methodImpl = getMethod("set${name[0].toUpperCase()}${name[1..-1]}".toString(), parameterTypes as Class[]) 
      if( isValidMethodImpl(methodImpl) ) {
         methodImpl(name,value)
         return
      }

      // try 'direct' access
      if( isProperty(name) ) {
         setPropertyValue(name,value)
         return
      }

      doSetPropertyMissing(name,value)
   }

   MetaClass doGetMetaClass() {
      return realizedProxyMetaClass
   }

   void doSetMetaClass( MetaClass metaClass ) {
      // metaClass should be read-only?
   }

   Object doMethodMissing( String name, Object args ) {
      // check if overridden
      def methodImpl = getMethod(METHOD_MISSING) 
      if( isValidMethodImpl(methodImpl) ) {
         return methodImpl(name,args)
      }

      throw new UnsupportedOperationException("Method ${methodSignature(name,args)} is not implemented")
   }

   Object doGetPropertyMissing( String name ) {
      // check if overridden
      def methodImpl = getMethod(GET_PROPERTY_MISSING) 
      if( isValidMethodImpl(methodImpl) ) {
         return methodImpl(name)
      }
      throw new MissingPropertyException( name, realizedProxyMetaClass.theClass )
   }

   void doSetPropertyMissing( String name, Object value ) {
      // check if overridden
      def methodImpl = getMethod(SET_PROPERTY_MISSING) 
      if( isValidMethodImpl(methodImpl) ) {
         methodImpl(name,value)
         return
      }
      throw new MissingPropertyException( name, realizedProxyMetaClass.theClass )
   }
}
