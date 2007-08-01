/*
 * Copyright 2007 the original author or authors.
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

package groovy.grapplet

import netscape.javascript.JSObject

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GroovyJSObject {
   JSObject jsObject
   private final Object[] EMPTY_ARGS = new Object[0]
   private static final arrayPattern = ~/function Array\(\) \{\s*\[native code\]\s*}/

   GroovyJSObject( JSObject jsObject ){
      this.jsObject = jsObject
   }

   JSObject getJSObject(){ jsObject }

   static def wrap( String name, member ){
      // by an unknown reason window.location can't be wrapped
      if( member instanceof JSObject && name != "location" ){
         member = isArray(member)? new JSList(member) : new JSMap(member)
      }
      return member
   }

   String toString(){
       jsObject.toString()
   }

   static def isArray( jsObject ){
      arrayPattern.matcher( jsObject.getMember("constructor").toString() ).matches()
   }
}