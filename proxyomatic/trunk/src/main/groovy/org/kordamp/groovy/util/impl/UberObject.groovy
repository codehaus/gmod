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

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * Represents a super class instance of a ProxyObject.<br/>
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class UberObject implements GroovyInterceptable {
   private Class type
   private ProxyObject target
  
   UberObject( ProxyObject target, Class type ) {
      this.target = target
      this.type = type
   }
  
   public Object invokeMethod( String methodName, Object args ) {
      def metaRegistry = GroovySystem.metaClassRegistry
      def superclass = type.superclass ?: Object
      MetaClass metaClass = metaRegistry.getMetaClass(superclass)
      return metaClass.invokeMethod(target, methodName, InvokerHelper.asArray(args))
   }
}