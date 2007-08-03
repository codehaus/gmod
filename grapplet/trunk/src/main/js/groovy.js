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
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */

var Groovy = {
   /** extra cofiguration */
   config: {
      codebase: "",
      jars: ""
   },

   /** locates all script tags that have Groovy code */
   getCode: function() {
      var scripts = document.getElementsByTagName("script"),
          code = '';

      for( var i = 0; i < scripts.length; i++ ){
         var script = scripts[i],
             type = script.getAttribute("type");
         if( type == "text/groovy" || type == "text/x-groovy" ){
           code += script.innerHTML
         }
      }
      return code
   },

   /** draws and initializes the applet */
   init: function(callback){
      Groovy.applet();
      Groovy.evalScript(callback)
   },

   evalScript: function(callback){
      if( document.Grapplet.evalGroovy && document.Grapplet.hasStarted() ){
         if( callback != null && callback != undefined ){
            callback( Groovy.eval(Groovy.getCode()) )
         }
      }else{
         setTimeout(function(){ Groovy.evalScript(callback); }, 500)
      }
   },

   eval: function(code){
      return document.Grapplet.evalGroovy( code )
   },

   applet: function(){
      var applet = document.createElement('applet'),
          archive = "lib/groovy-all-1.0.jar,lib/grapplet-0.1.jar";

      if( Groovy.config.jars != "" ){
         archive += "," + Groovy.config.jars
      }

      applet.setAttribute("archive", archive );
      applet.setAttribute("code", "org.kordamp.groovy.grapplet.Grapplet");
      if( Groovy.config.codebase != "" ){
         applet.setAttribute("codebase", Groovy.config.codebase);
      }
      applet.setAttribute("name", "Grapplet");
      applet.setAttribute("mayscript", "true");
      applet.setAttribute("width", "1");
      applet.setAttribute("height", "1");

      document.body.appendChild(applet);
   }
};