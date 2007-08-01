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
class JSListIterator implements ListIterator {
   JSObject array
   int cursor = 0
   int lastRet = -1

   boolean hasNext() {
      return cursor != getLength()
   }

   Object next() {
      if( cursor < getLength() ){
         def value = array.getMember(cursor as String)
         lastRet = cursor
         cursor += 1
         return value
      }
      throw new NoSuchElementException()
   }

   void remove() {
      if( lastRet == -1 ) throw new IllegalStateException()
      array.call( "splice", lastRet, 1 )
      if( lastRet < cursor ) cursor -= 1
      if( cursor < 0 ) cursor = 0
      lastRet = -1
   }

   boolean hasPrevious(){
      return cursor != 0
   }

   Object previous(){
      if( cursor > 0 ){
         int i = cursor - 1
         def previous = array.getMember(i as String)
         lastRet = cursor = i
         return previous
      }
      throw new NoSuchElementException()
   }

   int nextIndex(){
      cursor
   }

   int previousIndex(){
      cursor - 1
   }

   void set(Object o){
      if( lastRet == -1 )
 		 throw new IllegalStateException()
      if( lastRet < getLength() ){
         array.setMember( lastRet as String, o )
      }else{
         throw new ConcurrentModificationException()
      }
   }

   void add(Object o){
      if( cursor < getLength() ){
         array.setMember( cursor as String, o )
         cursor += 1
         lastRet = -1
      }else{
         throw new ConcurrentModificationException()
      }
   }

   private int getLength(){
      array.getMember("length") as int
   }
}