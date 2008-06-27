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

import static org.kordamp.groovy.util.ProxyOMatic.proxy

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ProxiesFromClosureTests extends AbstractProxiesTestCase {
   void testBuildProxyFromFooz() {
      def foo = proxy( Fooz ) {
         foo { -> "Foo" }
         foo { String n -> "Foo$n".toString() }
      }
       
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertTrue( "proxy is not of type Fooz", foo instanceof Fooz )
      assertEquals( "proxy.foo() did not return 'Foo'", "Foo", foo.foo() )
      assertEquals( "proxy.foo('Bar') did not return 'FooBar'", "FooBar", foo.foo('Bar') )
   } 
    
   def proxyFromFoo() {
      proxy( Foo ) {
         foo { -> "Foo" }
      }
   }
   
   def proxyFromFooBar() {
      proxy( FooBar ) {
         foo { -> "Foo" }
         bar { -> "Bar" }
         foobar { -> "FooBar" }
      }
   }
   
   def proxyFromFooBarWithSelfMethodCalls() {
      proxy( FooBar ) {
         foo { -> "Foo" }
         bar { -> "Bar" }
         foobar { -> foo() + bar() }
      }
   }
   
   def proxyFromFooAndBar() {
      proxy( Foo, [Bar] ) {
         foo { -> "Foo" }
         bar { -> "Bar" }
      }
   }

   def proxyFromFooImcompleteImpl() {
      proxy( Foo ) {}
   }

   def proxyFromFooWithInvokeMethod() {
      proxy( Foo ) {
         invokeMethod { String name, args -> "Foo" }
      }
   }

   def proxyFromFooWithMethodMissing() {
      proxy( Foo ) {
         methodMissing { String name, args -> "Foo" }
      }
   }
   
   def proxyFromFooAndList() {
      proxy( Foo, [List] ) {
         foo { -> "Foo" }
      }
   }
}