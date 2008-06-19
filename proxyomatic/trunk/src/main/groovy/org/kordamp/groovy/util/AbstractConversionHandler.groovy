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
package org.kordamp.groovy.util

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.ConversionHandler
import java.lang.reflect.Method

/**
 * General adapter for maps that support "overloaded methods" via MethodKey to any Java interface.<br/>
 * Based on org.codehaus.groovy.runtime.ConvertedMap by <a href="mailto:blackdrag@gmx.org">Jochen Theodorou</a> 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractConversionHandler extends ConversionHandler {
   private static final MethodKey INVOKE_METHOD = new MethodKey("invokeMethod",[String,Object] as Class[])
   private static final MethodKey GET_PROPERTY = new MethodKey("getProperty",[String] as Class[])
   private static final MethodKey SET_PROPERTY = new MethodKey("setProperty",[String,Object] as Class[])
   private static final MethodKey GET_METACLASS = new MethodKey(MetaClass,"getMetaClass")
   private static final MethodKey SET_METACLASS = new MethodKey("setMetaClass",[MetaClass] as Class[])

   private static final MethodKey METHOD_MISSING = new MethodKey("methodMissing",[String,Object] as Class[])
   private static final MethodKey GET_PROPERTY_MISSING = new MethodKey("propertyMissing",[String] as Class[])
   private static final MethodKey SET_PROPERTY_MISSING = new MethodKey("propertyMissing",[String,Object] as Class[])

   private static final List GROOVY_OBJECT_METHODS = [
      GET_PROPERTY, SET_PROPERTY, GET_METACLASS, SET_METACLASS
   ]

   protected AbstractConversionHandler( Object delegate ) {
      super( delegate )
   }
  
   public Object invokeCustom( Object proxy, Method method, Object[] args ) throws Throwable {
      // 1st check for GroovyObject methods
      def methodKey = new MethodKey(method)
      if( GROOVY_OBJECT_METHODS.contains(methodKey) ){
         // check if 'overridden'
         def methodImpl = getMethod(methodKey) 
         return isValidMethodImpl(methodImpl) ? methodImpl(*args) : callGroovyObjectMethod(proxy,methodKey,args)
      }

      // 2nd check if "invokeMethod" is provided
      def invokeMethodImpl = getMethod(INVOKE_METHOD)
      if( isValidMethodImpl(invokeMethodImpl) ) {
         return invokeMethodImpl(method.name,args)
      }

      // 3rd try the method itself
      def methodImpl = getMethod(method)
      if( !isValidMethodImpl(methodImpl) ) {
         // 4th try "methodMissing"
         return groovyObjectMethodMissing(proxy,method.name,args)
      }
      return method.parameterTypes.length == 0 ? methodImpl() : methodImpl(*args)
   }
  
   public String toString() {
      def toStringMethod = getMethod("toString")
      return toStringMethod ? toStringMethod() : DefaultGroovyMethods.toString(getDelegate())
   }

   protected abstract Object getMethod( MethodKey methodKey )

   protected final Object getMethod( Method method ) {
      return getMethod( new MethodKey(method) )
   }

   protected final Object getMethod( String name ) {
      return getMethod( new MethodKey(name) )
   }

   protected boolean isValidMethodImpl( cl ) {
      cl && cl instanceof Closure
   }

   private String methodSignature( String name, Object[] args ) {
      // TODO handle arrays
      def types = args.inject([]) { it == null ? Object : it.getClass() }
      return "$name(${types.join(',')})"
   }

   // --- GroovyObject interface --

   protected Object callGroovyObjectMethod( Object proxy, MethodKey methodKey, Object[] args ) {
      switch( methodKey.name ) {
         case "getProperty":
            return groovyObjectGetProperty(proxy,*args)
         case "setProperty":
            return groovyObjectSetProperty(proxy,*args)
         case "getMetaClass":
            return groovyObjectGetMetaClass(proxy)
         case "setMetaClass":
            return groovyObjectSetMetaClass(proxy,*args)
      }
   }

   protected Object groovyObjectGetProperty( Object proxy, String name ) {
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

      return groovyObjectGetPropertyMissing(proxy,name)
   }

   protected void groovyObjectSetProperty( Object proxy, String name, Object value ) {
      // check if overridden
      def methodImpl = getMethod(SET_PROPERTY) 
      if( isValidMethodImpl(methodImpl) ) {
         methodImpl(name,value)
      }

      // check if getter is available
      methodImpl = getMethod("set${name[0].toUpperCase()}${name[1..-1]}".toString()) 
      if( isValidMethodImpl(methodImpl) ) {
         methodImpl(name,value)
      }

      groovyObjectSetPropertyMissing(proxy,name,value)
   }

   protected MetaClass groovyObjectGetMetaClass( Object proxy ) {
      return null
   }

   protected void groovyObjectSetMetaClass( Object proxy, MetaClass metaClass ) {
      // metaClass should be read-only?
   }

   protected Object groovyObjectMethodMissing( Object proxy, String name, Object args ) {
      // check if overridden
      def methodImpl = getMethod(METHOD_MISSING) 
      if( isValidMethodImpl(methodImpl) ) {
         return methodImpl(name,args)
      }

      throw new UnsupportedOperationException("Method ${methodSignature(name,args)} is not implemented")
   }

   protected Object groovyObjectGetPropertyMissing( Object proxy, String name ) {
      // check if overridden
      def methodImpl = getMethod(GET_PROPERTY_MISSING) 
      if( isValidMethodImpl(methodImpl) ) {
         return methodImpl(name)
      }
      throw new MissingPropertyException( name, proxy.getClass() )
   }

   protected void groovyObjectSetPropertyMissing( Object proxy, String name, Object value ) {
      // check if overridden
      def methodImpl = getMethod(SET_PROPERTY_MISSING) 
      if( isValidMethodImpl(methodImpl) ) {
         methodImpl(name,value)
      }
      throw new MissingPropertyException( name, proxy.getClass() )
   }
}
