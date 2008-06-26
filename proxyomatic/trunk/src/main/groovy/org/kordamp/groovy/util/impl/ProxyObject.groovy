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

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * Holds the required data to create a proxy.<br/>
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class ProxyObject {
   protected Map __PROXY__properties = [:]
   protected boolean __PROXY__realized
   protected boolean __PROXY__handlingStatics
   protected boolean __PROXY__handlingProperties

   private def __PROXY__proxy
   private ProxyObject self
   //private UberObject uber
  
   ProxyObject() {
      self = this
   }

   def getProperty( String name ) {
      switch( name ) {
         case "__PROXY__properties": 
            return this.@__PROXY__properties
         case "__PROXY__realized": 
            return this.@__PROXY__realized
         case "__PROXY__handlingStatics": 
            return this.@__PROXY__handlingStatics
         case "__PROXY__handlingProperties": 
            return this.@__PROXY__handlingProperties
         case "self": 
            return this.@self
      }
      
      if( !this.@__PROXY__realized ) {
         return this.@__PROXY__properties[name]
      }
      return InvokerHelper.invokeMethod( this.@__PROXY__proxy, "getProperty", name )
   }
  
   void setProperty( String name, Object value ) {
      if( value?.class?.isArray() ) value = value[0]
      switch( name ) {
         case "__PROXY__properties": 
            this.@__PROXY__properties = value
            break
         case "__PROXY__realized": 
            this.@__PROXY__realized = value
            break
         case "__PROXY__handlingStatics": 
            this.@__PROXY__handlingStatics = value
            break
         case "__PROXY__handlingProperties": 
            this.@__PROXY__handlingProperties = value
            break
         case "self":
            throw new GroovyRuntimeException("self is a read-only property")  
      }
      
      if( !this.@__PROXY__realized ){
         this.@__PROXY__properties[name] = value
      }
      InvokerHelper.invokeMethod( this.@__PROXY__proxy, "setProperty", [name,value] )
   }
  
   final def methodMissing( String name, value ) {
      if( !this.@__PROXY__realized ) {
         if( this.@__PROXY__handlingProperties ){
            // name will be propertyName
            // value will be propertyValue
            if( value?.class?.isArray() ) {
               value = value.length > 0 ? value[0] : null
            }
            this.@__PROXY__properties[name] = value
         }else if( this.@__PROXY__handlingStatics ){

         }else{
            switch( value.length ){
               case 1:
                  return addMethodDefinition( name, value[0] )
               case 2:
                  return addMethodDefinition( (Class) value[0], name, (Closure) value[1] )
               default:
                  throw new GroovyRuntimeException("Illegal proxy method declaration [$name, argCount:$value.length]")
            }   
         }
      }else{
         return InvokerHelper.invokeMethod( this.@__PROXY__proxy, name, value )
      }
   }
   
   final def statics( Closure closure ) {
      if( !this.@__PROXY__realized ) {
         if( this.@__PROXY__handlingStatics ) {
             // nested statics{} is not allowed!
             throw new GroovyRuntimeException("Can't nest statics definitions")
         }
         this.@__PROXY__handlingStatics = true
         closure.delegate = this
         closure()
         this.@__PROXY__handlingStatics = false
      }
   }
  
   final def properties( Closure closure ) {
      if( !this.@__PROXY__realized ) {
         if( this.@__PROXY__handlingProperties ) {
             // nested properties{} is not allowed!
             throw new GroovyRuntimeException("Can't nest properties definitions")
         }
         this.@__PROXY__handlingProperties = true
         closure.delegate = this
         closure()
         this.@__PROXY__handlingProperties = false
      }
   }
  
   final def realize( Class type, List<Class> extraTypes ) {
      if( !this.@__PROXY__realized ){
         assignDelegateToMethodBodies()
         def types = extraTypes ? extraTypes << type : [type]
         // TODO wait til Metaclass is properly wired
         injectGroovyObjectInterface(types)
         this.@__PROXY__proxy = makeProxy( types )
         this.@__PROXY__realized = true
      }
      return this.@__PROXY__proxy
   }
   
   protected abstract def addMethodDefinition( ProxyMethodKey name, Closure body ) 
   
   protected abstract def addMethodDefinition( String name, Closure body )
   
   protected abstract def addMethodDefinition( Class returnType, String name, Closure body )
   
   protected abstract void assignDelegateToMethodBodies()
  
   protected abstract def makeProxy( List<Class> types )

   private void injectGroovyObjectInterface( types ) {
      def count = 0
      types.each { type ->
         if( GroovyObject.class.isAssignableFrom(type) ){
            count++
         }
      }

      if( count != types.size() ) {
         types << GroovyObject
      }
   }
 
   protected Class createProxyClass( types ) {
      def buffer = new StringBuffer()
      types.each { type ->
         buffer.append("import ")
         buffer.append(TypeUtils.getName(type))
         buffer.append("\n")
      }
      buffer.append("\n")

      buffer.append("abstract class ")
      def classname = TypeUtils.getShortName(types[0]) + "_proxy" + System.nanoTime()
      buffer.append(classname)
      
      if( !types[0].isInterface() ){
         buffer.append(" extends ")
         buffer.append(TypeUtils.getShortName(types[0]))
         if( types.size() > 1 ){
            buffer.append(" implements ")
            def shortTypes = types[1..-1].collect { TypeUtils.getShortName(it == null ? Object : it) }
            buffer.append(shortTypes.join(", "))
         }
      }else{
         buffer.append(" implements ")
         def shortTypes = types.collect { TypeUtils.getShortName(it == null ? Object : it) }
         buffer.append(shortTypes.join(", "))
      }
      
      buffer.append(" {}")
      buffer.append("\n")
      buffer.append("klass = ")
      buffer.append(classname)

      ClassLoader cl = null
      types.each { type ->
         if( !cl ) cl = type.classLoader
      }
      
      //GroovyShell shell = new GroovyShell( cl ?: getClassLoader() )
      GroovyShell shell = new GroovyShell( (ClassLoader) types[0].classLoader )
      //GroovyShell shell = new GroovyShell( ProxyObject.class.getClassLoader() )
      shell.evaluate( buffer.toString() )
   }
}