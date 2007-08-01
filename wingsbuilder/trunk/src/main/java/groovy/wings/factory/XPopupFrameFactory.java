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

import groovy.wings.WingXBuilder;

import java.util.Map;



import org.wings.SFrame;
import org.wingx.XPopupFrame;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class XPopupFrameFactory extends AbstractWingXFactory {
   public Object doNewInstance( WingXBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingXBuilder.checkValueIsNull( value, name );
      XPopupFrame popupFrame = null;
      SFrame frame = (SFrame) properties.remove( "frame" );
      Number width = (Number) properties.remove( "width" );
      Number height = (Number) properties.remove( "height" );

      if( frame != null && width != null && height != null ){
         popupFrame = new XPopupFrame( frame, width.intValue(), height.intValue() );
      }else{
         popupFrame = new XPopupFrame( "" );
      }

      return popupFrame;
   }
}