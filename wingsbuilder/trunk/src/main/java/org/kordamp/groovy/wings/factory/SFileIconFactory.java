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

package org.kordamp.groovy.wings.factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.SFileIcon;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SFileIconFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingSBuilder.checkValueIsNull( value, name );
      SFileIcon icon = null;
      File file = (File) properties.remove( "file" );
      String filename = (String) properties.remove( "filename" );
      String extension = (String) properties.remove( "extension" );
      String mimetype = (String) properties.remove( "mimetype" );
      if( file != null ){
         try{
            icon = new SFileIcon( file, extension, mimetype );
         }catch( FileNotFoundException e ){
            throw new RuntimeException( "Failed to create component for '" + name + "' reason: "
                  + e, e );
         }
      }else if( filename != null ){
         try{
            icon = new SFileIcon( filename );
         }catch( FileNotFoundException e ){
            throw new RuntimeException( "Failed to create component for '" + name + "' reason: "
                  + e, e );
         }
      }else{
         throw new RuntimeException( "Failed to create component for '" + name
               + "' reason: specify one of ['file','filename']" );
      }
      return icon;
   }
}