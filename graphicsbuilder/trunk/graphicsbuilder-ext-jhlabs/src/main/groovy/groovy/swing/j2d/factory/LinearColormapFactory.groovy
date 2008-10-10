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

package groovy.swing.j2d.factory

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.operations.filters.ColormapAware

import com.jhlabs.image.LinearColormap

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class LinearColormapFactory extends ColormapFactory {
   LinearColormapFactory(){
      super( LinearColormap )
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node, Map attributes ){
      def color1 = attributes.remove("color1")
      def color2 = attributes.remove("color2")
      if( color1 != null ) node.color1 = ColorCache.getColor(color1).getRGB()
      if( color2 != null ) node.color2 = ColorCache.getColor(color2).getRGB()
      return true
   }
}