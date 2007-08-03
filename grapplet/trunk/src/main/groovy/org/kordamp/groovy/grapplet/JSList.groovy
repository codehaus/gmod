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
class JSList extends GroovyJSObject implements List {
   JSList( JSObject jsObject ){
      super( jsObject )
   }

   // implement the Lits interface

   public boolean add( Object element ) {
      getJSObject().call( "push", [element] as Object[] )
      true
   }

   public void add( int index, Object element ) {
      getJSObject().call( "splice", [index, 0, element] as Object[] )
   }

   public boolean addAll( Collection collection ) {
      if( collection ){
         getJSObject().call( "push", collection as Object[] )
         return true
      }
      return false
   }

   public boolean addAll( int index, Collection collection ) {
      if( collection ){
         collection.add( 0, index )
         collection.add( 1, 0 )
         getJSObject().call( "splice", collection as Object[] )
         return true
      }
      return false
   }

   public void clear() {
      getJSObject().setMember( "length", 0 )
   }

   public boolean contains( Object object ) {
      def jsObject = getJSObject()
      (0..<getLength()).each{
         def element = jsObject.getMember( it as String )
         if( element == object ) return true
      }
      return false
   }

   public boolean containsAll( Collection collection ) {
      collection.each{
         if( !contains(it) ) return false
      }
      return true
   }

   public Object get( int index ) {
      getJSObject().getMember( index as String )
   }

   public int indexOf( Object object ) {
      def jsObject = getJSObject()
      (0..<getLength()).each{
         def element = jsObject.getMember( it as String )
         if( element == object ) return it as int
      }
      return -1
   }

   public boolean isEmpty() {
      getLength() == 0
   }

   public Iterator iterator() {
      return new JSListIterator( array: getJSObject(), cursor: 0 )
   }

   public int lastIndexOf( Object object ) {
      def jsObject = getJSObject()
      (getLength()..0).each{
         def element = jsObject.getMember( it as String )
         if( element == object ) return it as int
      }
      return -1
   }

   public ListIterator listIterator() {
      return new JSListIterator( array: getJSObject(), cursor: 0 )
   }

   public ListIterator listIterator( int index ) {
      if( index >= 0 && index < getLength() ){
         return new JSListIterator( array: getJSObject(), cursor: index )
      }else{
         throw IllegalArgumentException("[${index} < 0 || ${index} >= ${getLength()}]")
      }
   }

   public boolean remove( Object object ) {
      return false;
   }

   public Object remove( int index ) {
      return null;
   }

   public boolean removeAll( Collection collection ) {
      return false;
   }

   public boolean retainAll( Collection collection ) {
      return false;
   }

   public Object set( int index, Object element ) {
      def previousValue = getJSObject().getMember(index as String)
      getJSObject().setMember( index as String, element )
      return previousValue
   }

   public int size() {
      getLength()
   }

   public List subList( int fromIndex, int toIndex ) {
      return null;
   }

   public Object[] toArray() {
      return null;
   }

   public Object[] toArray( Object[] a ) {
      return null;
   }

   private int getLength(){
      getJSObject().getMember("length") as int
   }
}