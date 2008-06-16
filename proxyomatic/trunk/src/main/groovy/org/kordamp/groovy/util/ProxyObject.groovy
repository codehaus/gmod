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

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * Holds the required data to create a proxy.<br/>
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class ProxyObject {
   private boolean __realized
   private def __proxy
   private ProxyObject self
   //private UberObject uber
  
   ProxyObject() {
      self = this
   }
  
   final def methodMissing( String name, value ) {
      if( !__realized ) {
         switch( value.length ){
            case 1:
               return addMethodDefinition( name, value[0] )
            case 2:
               return addMethodDefinition( (Class) value[0], name, (Closure) value[1] )
            default:
               throw new GroovyRuntimeException("Illegal proxy method declaration [$name, argCount:$value.length]")
         }   
      }else{
         return InvokerHelper.invokeMethod( __proxy, name, value )
      }
   }
  
   def asType( Class type ) {
      asType( type, null )
   }
  
   final def asType( Class type, List<Class> extraTypes ) {
      if( !__realized ){
         assignDelegateToMethodBodies()
         def types = extraTypes ? extraTypes << type : [type]
         __proxy = makeProxy( types )
         __realized = true
         return __proxy
      }else{
         return __proxy.asType(type)
      }
   }
   
   protected abstract def addMethodDefinition( MethodKey name, Closure body ) 
   
   protected abstract def addMethodDefinition( String name, Closure body )
   
   protected abstract def addMethodDefinition( Class returnType, String name, Closure body )
   
   protected abstract void assignDelegateToMethodBodies()
  
   protected abstract def makeProxy( List<Class> types )
}