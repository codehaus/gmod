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
 */

package groovy.wings.factory;

import groovy.wings.WingSBuilder;

import java.util.Map;



import org.codehaus.groovy.runtime.InvokerHelper;
import org.wings.SBoxLayout;
import org.wings.SContainer;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SBoxLayoutFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingSBuilder.checkValueIsNull( value, name );
      Object parent = builder.getCurrent();
      if( parent instanceof SContainer ){
         Object axisObject = properties.remove( "axis" );
         int axis = SBoxLayout.X_AXIS;
         if( axisObject != null ){
            Integer i = (Integer) axisObject;
            axis = i.intValue();
         }

         SContainer target = WingSBuilder.getLayoutTarget( (SContainer) parent );
         SBoxLayout answer = new SBoxLayout( axis );

         // now let's try to set the layout property
         InvokerHelper.setProperty( target, "layout", answer );
         return answer;
      }else{
         throw new RuntimeException( "Must be nested inside a Container" );
      }
   }
}