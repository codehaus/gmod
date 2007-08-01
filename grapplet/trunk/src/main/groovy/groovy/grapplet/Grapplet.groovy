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

import groovy.lang.Binding
import groovy.lang.GroovyShell

import netscape.javascript.JSObject

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class Grapplet extends javax.swing.JApplet {
   private Binding binding
   private GroovyJSObject groovyWindow
   private GroovyShell shell
   private boolean started = false

   public String evalGroovy( String code ) {
      def returnValue = null
      try{
         returnValue = shell.evaluate( code )
      }catch( RuntimeException e ){
         e.printStackTrace()
         returnValue = e.getMessage()
      }
      if( !(returnValue instanceof JSObject) ){
         returnValue = String.valueOf( returnValue )
      }

      return returnValue
   }

   public boolean hasStarted() {
      return this.started
   }

   public void init() {
      JSObject window = JSObject.getWindow( this )
      groovyWindow = GroovyJSObject.wrap( "window", window )
      binding = new Binding()
      binding.setVariable( "window", groovyWindow )
      shell = new GroovyShell( binding )
   }

   public void start() {
      this.started = true
   }
}