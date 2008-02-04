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

package groovy.swing.j2d.factory

import com.jhlabs.image.WarpGrid

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class WarpGridFactory extends GraphicsOperationBeanFactory {
   public WarpGridFactory() {
      super( WarpGrid, true )
   }

   public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
         Map properties ) throws InstantiationException, IllegalAccessException {
     if( FactoryBuilderSupport.checkValueIsTypeNotString( value, name, WarpGrid ) ){
         return value
     }else{
         def rows = properties.remove("rows")
         def cols = properties.remove("cols")
         def w = properties.remove("w")
         def h = properties.remove("h")
         rows = rows ? rows : 0
         cols = cols ? cols : 0
         w = w ? w : 0
         h = h ? h : 0
         return new WarpGrid( rows as int, cols as int, w as int, h as int )
     }
   }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
      // empty
   }
}