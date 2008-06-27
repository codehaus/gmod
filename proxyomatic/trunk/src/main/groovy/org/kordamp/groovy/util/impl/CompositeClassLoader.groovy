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

import sun.misc.CompoundEnumeration

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class CompositeClassLoader extends ClassLoader {
   private List classLoaders = []
   
   CompositeClassLoader( List classLoaders ) {
      super( ClassLoader.getSystemClassLoader() )
      this.classLoaders.addAll( classLoaders )
   }
    
   public void addClassLoader( ClassLoader classLoader ) {
      if( !classLoaders.contains(classLoader) ){
         classLoaders << classLoader
      }
   }
   
   public URL getResource( String name ) {
      URL url = null
      for( classLoader in classLoader ){
         try {
            url = classLoader.getResource(name)
         }catch( Exception e ){
            // ignore
         }
      }
      return url ?: super.getResource(name)
   }

   public InputStream getResourceAsStream( String name ) {
      InputStream stream = null
      for( classLoader in classLoader ){
         try {
            stream = classLoader.getResourceAsStream(name)
         }catch( Exception e ){
            // ignore
         }
      }
      return stream ?: super.getResourceAsStream()
   }

   public Enumeration getResources( String name ) throws IOException {
      def resources = []
      for( classLoader in classLoader ){
         try {
            def rse = classLoader.getResources(name)
            if( rse ) resources << rse
         }catch( Exception e ){
            // ignore
         }
      }
      resources << super.getResources(name)
      return new CompoundEnumeration( resources as Enumeration[] )
   }

   public Class loadClass( String name ) throws ClassNotFoundException {
      for( classLoader in classLoader ){
         try {
            return classLoader.loadClass(name)
         }catch( ClassNotFoundException e ){
            // ignore
         }
      }
      return super.loadClass(name)
   }
   
   /*
   protected Class<?> findClass(String name) throws ClassNotFoundException {
      for( classLoader in classLoader ){
         try {
            return classLoader.findClass(name)
         }catch( ClassNotFoundException e ){
            // ignore
         }
      }
      return super.findClass(name)
   }
   */
}
