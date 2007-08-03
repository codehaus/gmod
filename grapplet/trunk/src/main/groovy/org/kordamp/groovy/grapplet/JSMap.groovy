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

package org.kordamp.groovy.grapplet

import netscape.javascript.JSObject

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class JSMap extends GroovyJSObject {
   def closures = [:]

   JSMap( JSObject jsObject ){
      super( jsObject )
   }

   Object getProperty( String name ) {
      if( closures[name] ){
         return closures[name]
      }else{
         def member = getJsObject().getMember( name )
         if( member instanceof JSObject && name != "location" ){
            member = GroovyJSObject.wrap( name, member )
         }
         return member
      }
   }

   void setProperty( String name, Object value ) {
      if( value instanceof Closure ){
         closures[name] = value
      }else{
         getJSObject().setMember( name, value )
      }
   }

   Object invokeMethod( String name, args ) {
      if( closures[name] ){
         return closures[name].call(args)
      }else{
         Object[] arguments = EMPTY_ARGS
         if( args ){
            if( args.getClass().isArray() ){
               arguments = args as Object[]
            }else{
               arguments = [ args ] as Object[]
            }
         }
         Object returnValue = null
         try{
            returnValue = getJSObject().call( name, arguments )
         }catch( Exception e ){
            e.printStackTrace()
            returnValue = e
         }
         if( returnValue instanceof JSObject ){
            return GroovyJSObject.wrap( "result", returnValue )
         }else{
            return returnValue
         }
      }
   }
}