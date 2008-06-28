/*
 * Copyright 2007-2008 the original author or authors.
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

package groovy.swing.j2d.operations.filters.lights

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.AbstractLightFilterProvider

import com.jhlabs.image.LightFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class LightsFilterProvider extends AbstractLightFilterProvider {
   boolean originalLight

   LightsFilterProvider() {
      super( "lights" )
      filter = new LightFilter()
   }

   public void setOriginalLight( boolean on ){
      if( !on && !originalLight ){
         filter.removeLight( filter.lights[0] )
         originalLight = true
      }
   }
}