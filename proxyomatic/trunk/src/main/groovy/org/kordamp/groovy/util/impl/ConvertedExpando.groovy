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
 * General adapter for Expando to any Java interface.<br/>
 * Based on org.codehaus.groovy.runtime.ConvertedMap by <a href="mailto:blackdrag@gmx.org">Jochen Theodorou</a> 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ConvertedExpando extends AbstractConversionHandler {
   private Map extraProperties 
    
   protected ConvertedExpando( Expando expando, Map extraProperties, Class proxyClass ) {
      super( expando, proxyClass )
      this.extraProperties = extraProperties
   }

   Object getMethod( ProxyMethodKey methodKey ) {
      def method = getDelegate()[methodKey.name]
      if( method ) {
         return method
      }
      getDelegate().getProperties()[methodKey]
   }

   boolean isProperty( String name ) {
      // TODO handle PME ??
      if( getDelegate().properties.containsKey(name) ) {
         return !(getDelegate()[name] instanceof Closure)
      }
      return extraProperties.containsKey(name)
   }

   protected Object getPropertyValue( String name ) {
      getDelegate()[name]
      if( getDelegate().getProperties().containsKey(name) ) {
         return getDelegate()[name]
      }
      extraProperties[name]
   }

   protected void setPropertyValue( String name, value ) {
      if( getDelegate().getProperties().containsKey(name) ) {
         getDelegate()[name] = value
         return
      }
      extraProperties[name] = value
   }

   Map getProperties() {
      def m = getDelegate().getProperties().inject([:]) { map, e ->
         if( !(e.value instanceof Closure) ){
            map[e.key] = e.value
         }
         map
      }
      extraProperties.inject(m) { map, e ->
         map[e.key] = e.value
         map
      }
   }
}
