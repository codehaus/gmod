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

package groovy.wings.factory;

import groovy.wings.WingSBuilder;

import java.util.Map;



import org.wings.Resource;
import org.wings.SDownloadButton;
import org.wings.SIcon;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SDownloadButtontFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingSBuilder.checkValueIsNull( value, name );
      SDownloadButton button = null;
      Resource resource = (Resource) properties.remove( "resource" );
      String text = (String) properties.remove( "text" );
      SIcon icon = (SIcon) properties.remove( "icon" );
      if( resource != null ){
         if( text != null ){
            button = new SDownloadButton( text, resource );
         }else if( icon != null ){
            button = new SDownloadButton( icon, resource );
         }else{
            button = new SDownloadButton( resource );
         }
      }else{
         throw new RuntimeException( "Failed to create component for '" + name
               + "' reason: missing 'resource' attribute" );
      }
      return button;
   }
}