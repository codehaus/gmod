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

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.ConversionHandler
import java.lang.reflect.Method

/**
 * General adapter for maps that support "overloaded methods" via MethodKey to any Java interface.<br/>
 * Based on org.codehaus.groovy.runtime.ConvertedMap by <a href="mailto:blackdrag@gmx.org">Jochen Theodorou</a> 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ConvertedMultiMethodMap extends ConversionHandler {
   protected ConvertedMultiMethodMap( Map map ) {
      super( map )
   }
  
   public Object invokeCustom( Object proxy, Method method, Object[] args )
   throws Throwable {
      Map map = getDelegate()
      Closure cl = map.get( new MethodKey(method) )
      return method.parameterTypes.length == 0 ? cl.call() : cl.call(args)
   }
  
   public String toString() {
      Map map = getDelegate()
      Closure cl = map.get( new MethodKey("toString") )
      return cl ? cl.call() : DefaultGroovyMethods.toString(map)
   }
}