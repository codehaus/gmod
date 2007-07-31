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

import java.awt.Image;
import java.util.Map;

import javax.swing.ImageIcon;



import org.wings.SImageIcon;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SImageIconFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingSBuilder.checkValueIsNull( value, name );
      SImageIcon imageIcon = null;
      ImageIcon icon = (ImageIcon) properties.remove( "icon" );
      Image image = (Image) properties.remove( "image" );
      String iname = (String) properties.remove( "name" );
      String mimetype = (String) properties.remove( "mimetype" );

      if( icon != null ){
         if( mimetype != null ){
            imageIcon = new SImageIcon( icon, mimetype );
         }else{
            imageIcon = new SImageIcon( icon );
         }
      }else if( image != null ){
         if( mimetype != null ){
            imageIcon = new SImageIcon( image, mimetype );
         }else{
            imageIcon = new SImageIcon( image );
         }
      }else{
         if( mimetype != null ){
            imageIcon = new SImageIcon( iname, mimetype );
         }else{
            imageIcon = new SImageIcon( iname );
         }
      }

      return imageIcon;
   }
}