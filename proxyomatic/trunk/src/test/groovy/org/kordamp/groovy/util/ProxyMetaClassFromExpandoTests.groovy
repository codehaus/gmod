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
import static org.kordamp.groovy.util.ProxyOMatic.methodKey

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ProxyMetaClassFromExpandoTests extends AbstractProxyMetaClassTestCase {
   def proxyFromFooWithPropertiesNode() {
      def expando = new Expando()
      expando.properties = { ->
         foo("Foo")
      }
      proxy( Foo, expando )
   }
   
   def proxyFromFooWithGetPropertyMethod() {
      def expando = new Expando()
      expando.getProperty = { String name -> "Foo" }
      proxy( Foo, expando )
   }
   
   def proxyFromFooWithPropertyMissing_get() {
      def expando = new Expando()
      expando.propertyMissing = { String name -> "Foo" }
      proxy( Foo, expando )
   }
   
   def proxyFromFooWithSetPropertyMethod() {
      def map = [foo:"Foo"]
      def expando = new Expando()
      expando.getProperty = { String name -> map[name] }
      expando.setProperty = { String name, value -> map[name] = value }
      proxy( Foo, expando )
   }
   
   def proxyFromFooWithPropertyMissing_set() {
      // TODO figure this out !!!
      def map = [foo:"Foo"]
      def impl = [:]
      impl[methodKey('propertyMissing',[String])] = { String name -> map[name] }
      impl[methodKey('propertyMissing',[String,Object])] = { String name, value -> map[name] = value }
      proxy( Foo, impl )
      /*
      def map = [foo:"Foo"]
      def expando = new Expando()
      expando[methodKey('propertyMissing',[String])] = { String name -> map[name] }
      expando[methodKey('propertyMissing',[String,Object])] = { String name, value -> map[name] = value }
      proxy( Foo, expando )
      */
   }
   
   void testGetPropertyDefinedWithExpandoKey() {
      def expando = new Expando()
      expando.foo = "Foo"
      def foo = proxy( Foo, expando )
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
   }
   
   void testSetPropertyDefinedWithExpandoKey() {
      def expando = new Expando()
      expando.foo = "Foo"
      def foo = proxy( Foo, expando )
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
      foo.foo = "Bar"
      assertEquals( "proxy.foo did not return 'Bar'", "Bar", foo.foo )
   }
}