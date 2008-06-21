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

/**
 * General adapter for maps that support "overloaded methods" via MethodKey to any Java interface.<br/>
 * Based on org.codehaus.groovy.runtime.ConvertedMap by <a href="mailto:blackdrag@gmx.org">Jochen Theodorou</a> 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ConvertedMultiMethodMap extends AbstractConversionHandler {
   protected ConvertedMultiMethodMap( Map map, Class proxyClass ) {
      super( map, proxyClass )
   }

   protected Object getMethod( MethodKey methodKey ) {
      getDelegate().methods[methodKey]
   }

   protected boolean isProperty( String name ) {
      getDelegate().properties.containsKey(name)
   }

   protected Object getPropertyValue( String name ) {
      getDelegate().properties[name]
   }

   protected void setPropertyValue( String name, value ) {
      getDelegate().properties[name] = value
   }

   Map getProperties() {
      getDelegate().properties.asImmutable()
   }
}
