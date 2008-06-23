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
abstract class AbstractObjectMethodsTestCase extends GroovyTestCase {
   void testToStringOverridden() {
      def proxy = proxyFooOverrideToString()
      
      assertNotNull( "proxy is null", proxy )
      assertTrue( "proxy is not of type Foo", proxy instanceof Foo )
      assertEquals( "proxy.toString() did not return 'FOO'", "FOO", proxy.toString() )
   }

   void testHashCodeOverridden() {
      def proxy1 = proxyFooOverrideHashCode(true)
      def proxy2 = proxyFooOverrideHashCode(true)
      def proxy3 = proxyFooOverrideHashCode(false)
      
      assertNotNull( "proxy1 is null", proxy1 )
      assertNotNull( "proxy2 is null", proxy2 )
      assertNotNull( "proxy3 is null", proxy3 )
      assertTrue( "proxy1 is not of type Foo", proxy1 instanceof Foo )
      assertTrue( "proxy2 is not of type Foo", proxy2 instanceof Foo )
      assertTrue( "proxy3 is not of type Foo", proxy3 instanceof Foo )
      assertTrue( "proxy1.hashCode() != proxy1.hashCode()", proxy1.hashCode() == proxy1.hashCode() )
      assertTrue( "proxy2.hashCode() != proxy2.hashCode()", proxy2.hashCode() == proxy2.hashCode() )
      assertTrue( "proxy2.hashCode() != proxy1.hashCode()", proxy2.hashCode() == proxy1.hashCode() )
      assertTrue( "proxy2.hashCode() != 42", proxy2.hashCode() == 42 )
      assertFalse( "proxy2.hashCode() == proxy3.hashCode()", proxy2.hashCode() == proxy3.hashCode() )
   }
   
   /*
   void testEqualsOverridden() {
      def proxy1 = proxyFooOverrideEquals(true)
      def proxy2 = proxyFooOverrideEquals(true)
      def proxy3 = proxyFooOverrideEquals(false)
      
      assertNotNull( "proxy1 is null", proxy1 )
      assertNotNull( "proxy2 is null", proxy2 )
      assertNotNull( "proxy3 is null", proxy3 )
      assertTrue( "proxy1 is not of type Foo", proxy1 instanceof Foo )
      assertTrue( "proxy2 is not of type Foo", proxy2 instanceof Foo )
      assertTrue( "proxy3 is not of type Foo", proxy3 instanceof Foo )
      assertTrue( "proxy1 != proxy1", proxy1 == proxy1 )
      assertTrue( "proxy2 != proxy2", proxy2 == proxy2 )
      assertTrue( "proxy2 != proxy1", proxy2 == proxy1 )
      assertFalse( "proxy2 == proxy3", proxy2 == proxy3 )
   }
   */
}