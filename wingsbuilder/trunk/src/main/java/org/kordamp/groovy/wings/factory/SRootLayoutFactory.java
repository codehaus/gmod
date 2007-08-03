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
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.SContainer;
import org.wings.SRootLayout;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SRootLayoutFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingSBuilder.checkValueIsNull( value, name );
      Object parent = builder.getCurrent();
      if( parent instanceof SContainer ){
         String filename = (String) properties.remove( "filename" );
         File file = (File) properties.remove( "file" );
         URL url = (URL) properties.remove( "url" );
         SContainer target = WingSBuilder.getLayoutTarget( (SContainer) parent );
         SRootLayout layout = null;
         try{
            if( filename != null ){
               layout = new SRootLayout( filename );
            }else if( file != null ){
               layout = new SRootLayout( file );
            }else if( url != null ){
               layout = new SRootLayout( url );
            }else{
               layout = new SRootLayout();
            }
         }catch( IOException e ){
            throw new RuntimeException( "Failed to create component for '" + name + "' reason: "
                  + e, e );
         }

         // now let's try to set the layout property
         InvokerHelper.setProperty( target, "layout", layout );
         return layout;
      }else{
         throw new RuntimeException( "Must be nested inside a Container" );
      }
   }
}