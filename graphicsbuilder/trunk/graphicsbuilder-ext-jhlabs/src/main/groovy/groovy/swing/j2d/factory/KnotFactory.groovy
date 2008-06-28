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

import groovy.swing.j2d.operations.filters.Knot

import com.jhlabs.image.Gradient
import com.jhlabs.image.SplineColormap

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class KnotFactory extends GraphicsOperationBeanFactory {
   public KnotFactory() {
      super( Knot, true )
   }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
      int index = child.index
      int color = child.realColor
      int x = child.x
      int type = child.type

      if( parent instanceof SplineColormap ){
         int numKnots = parent.numKnots
         if( index != null && index < numKnots ){
            parent.setKnot( index, color )
         }else if( x != null ){
            parent.addKnot( x, color )
         }
      }else if( parent instanceof Gradient ){
         int numKnots = parent.numKnots
         if( index != null && index < numKnots ){
            parent.setKnot( index, color )
         }else if( x != null && type != null ){
            parent.addKnot( x, color, type )
         }
      }else{
         throw new IllegalArgumentException("parent doesn't allow nesting of knot.")
      }
   }
}