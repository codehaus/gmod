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
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractProxyMetaClassTestCase extends GroovyTestCase {
   void testGetPropertyDefinedWithPropertiesNode() {
      def foo = proxyFromFooWithPropertiesNode()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
   }
   
   void testGetPropertyDefinedWithGetPropertyMethod() {
      def foo = proxyFromFooWithGetPropertyMethod()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
   }

   void testGetPropertyWithPropertyMissing() {
      def foo = proxyFromFooWithPropertyMissing_get()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
   }
   
   void testSetPropertyDefinedWithPropertiesNode() {
      def foo = proxyFromFooWithPropertiesNode()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
      foo.foo = "Bar"
      assertEquals( "proxy.foo did not return 'Bar'", "Bar", foo.foo )
   }
   
   void testSetPropertyDefinedWithSetPropertyMethod() {
      def foo = proxyFromFooWithSetPropertyMethod()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
      foo.foo = "Bar"
      assertEquals( "proxy.foo did not return 'Bar'", "Bar", foo.foo )
   }

   void testSetPropertyWithPropertyMissing() {
      def foo = proxyFromFooWithPropertyMissing_set()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
      foo.foo = "Bar"
      assertEquals( "proxy.foo did not return 'Bar'", "Bar", foo.foo )
   }
}