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
class ProxiesFromExpandoTests extends AbstractProxiesTestCase {
   def proxyFromFoo() {
      def expando = new Expando()
      expando.foo = { -> "Foo" }
      proxy( Foo, expando )
   }

   def proxyFromFooBar() {
      def expando = new Expando()
      expando.foo = { -> "Foo" }
      expando.bar = { -> "Bar" }
      expando.foobar = { -> "FooBar" }
      proxy( FooBar, expando )
   }
   
   def proxyFromFooBarWithSelfMethodCalls() {
      def expando = new Expando()
      expando.foo = { -> "Foo" }
      expando.bar = { -> "Bar" }
      expando.foobar = { -> foo() + bar() }
      proxy( FooBar, expando ) 
   }
   
   def proxyFromFooAndBar() {
      def expando = new Expando()
      expando.foo = { -> "Foo" }
      expando.bar = { -> "Bar" }
      proxy( FooBar, expando )
   }

   def proxyFromFooImcompleteImpl() {
      proxy( Foo, new Expando() )
   }

   def proxyFromFooWithInvokeMethod() {
      def expando = new Expando()
      expando.invokeMethod = { String name, args -> "Foo" }
      proxy( Foo, expando )
   }

   def proxyFromFooWithMethodMissing() {
      def expando = new Expando()
      expando.methodMissing = { String name, args -> "Foo" }
      proxy( Foo, expando ) 
   }
   
   def proxyFromFooAndList() {
      def expando = new Expando()
      expando.foo = { -> "Foo" }
      proxy( Foo, [List], expando )
   }
}
