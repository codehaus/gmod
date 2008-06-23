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

import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import java.lang.reflect.Proxy

/**
 * Holds the required data to create a proxy.<br/>
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ProxiedExpando extends ProxyObject {
   private Expando __PROXY__expando
   
   ProxiedExpando( Expando expando ) {
      this.@__PROXY__expando = expando
   }
  
   protected def addMethodDefinition( ProxyMethodKey name, Closure body ) {
      this.@__PROXY__expando[name] = body
   }   
   
   protected def addMethodDefinition( String name, Closure body ) {
      this.@__PROXY__expando[name] = body
      //this.@__PROXY__expando[new ProxyMethodKey(name, body.parameterTypes)] = body
   }
   
   protected def addMethodDefinition( Class returnType, String name, Closure body ) {
      this.@__PROXY__expando[name] = body
      //this.@__PROXY__expando[new ProxyMethodKey(returnType, name, body.parameterTypes)] = body
   }
   
   protected void assignDelegateToMethodBodies() {
      this.@__PROXY__expando.properties.each { k, c -> if(c instanceof Closure) c.delegate = this }
   }
  
   protected def makeProxy( List<Class> types ) {
      // only handles interfaces for the time being
      if( /*types.any{it.isInstance(this.@__PROXY__expando)} ||*/ types.any{!(it.isInterface())} ){
         throw new GroovyCastException("Can't create proxy with $types")
      }
      Proxy.newProxyInstance( types[0].getClassLoader(),
                    types as Class[],
                    new ConvertedExpando(this.@__PROXY__expando,
                                          createProxyClass(types))) 
   }
}
