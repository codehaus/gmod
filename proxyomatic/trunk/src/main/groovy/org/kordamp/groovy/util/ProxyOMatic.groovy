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

/**
 * ProxyOMatic lets you create dynamic proxies in a pinch!<br/>
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ProxyOMatic {
   /**
    * Creates a dynamic proxy of class <i>type</i> from a Closure, Map or Expando.<br/>
    * 
    * @param type the target class of the dynamic proxy
    * @param source a Closure, Map or Expando that contain method definitions
    * 
    * @return a java.lang.reflect.Proxy backed by <i>source</i>
    * 
    * @throws GroovyCastException if the proxy can't be created
    */ 
   static proxy( Class type, source ) {
      makeProxy( source ).asType( type, [] )
   }
    
   /**
    * Creates a dynamic proxy of class <i>type</i> from a Closure, Map or Expando.<br/>
    * Additional types may be injected if <i>extraTypes</i> is not empty.
    * 
    * @param type the target class of the dynamic proxy
    * @param extraTypes additional types to be injected into the final proxy
    * @param source a Closure, Map or Expando that contain method definitions
    * 
    * @return a java.lang.reflect.Proxy backed by <i>source</i>
    * 
    * @throws GroovyCastException if the proxy can't be created
    */   
   static proxy( Class type, List<Class> extraTypes, source ) {
      makeProxy( source ).asType( type, extraTypes )
   }

   /**
    * Creates a dynamic proxy of class <i>type</i> from a Closure, Map or Expando..<br/>
    * Additional types may be injected if <i>extraTypes</i> is not empty.
    * 
    * @param type the target class of the dynamic proxy
    * @param extraTypes additional types to be injected into the final proxy
    * @param source a Closure, Map or Expando that contain method definitions
    * 
    * @return a java.lang.reflect.Proxy backed by <i>source</i>
    * 
    * @throws GroovyCastException if the proxy can't be created
    */     
   static proxy( Class type, Class[] extraTypes, source ) {
      makeProxy( source ).asType( type, extraTypes as List<Class> )
   }
   
   /**
    * Private constructor to discourage instantiation.
    */
   private ProxyOMatic() {
      
   }
   
   private static makeProxy( Closure closure ) {
      def proxyObject = new ProxiedClosure()
      closure.delegate = proxyObject
      closure.call()
      proxyObject
   }
  
   private static makeProxy( Map map ) {
      def proxyObject = new ProxiedMap()
      map.each { name, value ->
         if( value instanceof Closure ){
            proxyObject.addMethodDefinition( name, value )
         }
      }
      proxyObject
   }
   
   private static makeProxy( Expando expando ) {
      def proxyObject = new ProxiedExpando(expando)
      proxyObject
   }
}
