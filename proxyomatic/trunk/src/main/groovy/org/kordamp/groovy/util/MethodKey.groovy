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

import java.lang.reflect.Method

/**
 * 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class MethodKey {
   private String name
   private Class returnType
   private Class[] parameterTypes
  
   private static final Class[] EMPTY_PARAMETER_TYPES = new Class[0]
  
   /**
    * Creates a new MethodKey with default return type and no parameters.<br/>
    * The method signature would be<br/><br/>
    * <pre>  Object name()</pre>
    * 
    * @param name the name of the method
    */
   MethodKey( String name ) {
     this( Object, name, EMPTY_PARAMETER_TYPES )
   }
  
   /**
    * Creates a new MethodKey with default return type and the specified parameters.<br/>
    * Assuming parameterTypes = [String,Integer] the method signature would be<br/><br/>
    * <pre>  Object name( String, Integer )</pre>
    * 
    * @param name the name of the method
    * @param parameterTypes the parameter types (may be null or empty)
    */
   MethodKey( String name, Class[] parameterTypes ) {
     this( Object, name, parameterTypes )
   }
  
   /**
    * Creates a new MethodKey with the specified return type and no parameters.<br/>
    * Assuming returnType = Integer the method signature would be<br/><br/>
    * <pre>  String name()</pre>
    * 
    * @param returnType the return type of the method
    * @param name the name of the method
    */ 
   MethodKey( Class returnType, String name ) {
      this( returnType, name, EMPTY_PARAMETER_TYPES )
   }
  
   /**
    * Creates a new MethodKey with the specified return type and parameters.<br/>
    * Assuming returnType = Integer and parameterTypes = [String,Integer] the method signature would be<br/><br/>
    * <pre>  String name( String, Integer )</pre>
    * 
    * @param returnType the return type of the method
    * @param name the name of the method
    * @param parameterTypes the parameter types (may be null or empty)
    */   
   MethodKey( Class returnType, String name, Class[] parameterTypes ) {
      this.name = name
      this.returnType = returnType ?: Object
      this.parameterTypes = parameterTypes ?: EMPTY_PARAMETER_TYPES
   }
  
   /**
    * Creates a new MethodKey with default return type and parameters inferred from the closure.<br/>
    * Assuming closure = { a, Integer b -> } the method signature would be<br/><br/>
    * <pre>  Object name( Object, Integer )</pre>
    * 
    * @param name the name of the method
    * @param closure a closure that serves as the body of the method
    */
   MethodKey( String name, Closure closure ) {
      this( Object, name, closure.parameterTypes )
   }
  
   /**
    * Creates a new MethodKey with the specified return type and parameters inferred from the closure.<br/>
    * Assuming return type = String and closure = { a, Integer b -> } the method signature would be<br/><br/>
    * <pre>  String name( Object, Integer )</pre>
    * 
    * @param returnType the return type of the method
    * @param name the name of the method
    * @param closure a closure that serves as the body of the method
    */
   MethodKey( Class returnType, String name, Closure closure ) {
      this( returnType, name, closure.parameterTypes )
   }
  
   /**
    * Creates a new MethodKey based on the information provided by the method.<br/>
    * Assuming return type = String and closure = { a, Integer b -> } the method signature would be<br/><br/>
    * <pre>  String name( Object, Integer )</pre>
    * 
    * @param method any method
    */
   MethodKey( Method method ) {
      this( method.returnType, method.name, method.parameterTypes )
   }

   public String getName() {
      return this.name
   } 
  
   public Class[] getParameterTypes() {
      return this.parameterTypes
   } 
  
   public Class getReturnType() {
      return this.returnType
   } 
  
   public int hashCode() {
      // TODO consider returnType
      this.@parameterTypes.inject(this.@name.hashCode()){ v, c -> v + c.hashCode() }
   }
  
   public boolean equals( Object other ) {
      if( !(other instanceof MethodKey) ) return false
      if( this.@name != other.@name ) return false

      if( this.@parameterTypes.length == 0 ){
         switch( other.@parameterTypes.length ){
            case 0:
               return true
            case 1:
               return other.@parameterTypes == [Object]
            default:
               return false
         }
      }else if( other.@parameterTypes.length == 0 ){
         switch( this.@parameterTypes.length ){
            case 0:
               return true
            case 1:
               return this.@parameterTypes == [Object]
            default:
               return false
         }
      }

      // TODO consider returnType

      return this.@parameterTypes == other.@parameterTypes
   }
  
   public String toString() {
      // TODO handle arrays
      "${this.@returnType.name} ${this.@name}("+this.@parameterTypes.name.join(",")+")"
   }
}
