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

import groovy.swing.j2d.operations.filters.distort.FieldWarpFilterProvider
import com.jhlabs.image.FieldWarpFilter
import com.jhlabs.image.FieldWarpFilter.Line

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class FieldWarpLineFactory extends GraphicsOperationBeanFactory {
   private isOut

   public FieldWarpLineFactory( isOut ) {
      super( Line, true )
      this.isOut = isOut
   }

   public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
         Map properties ) throws InstantiationException, IllegalAccessException {
     if( FactoryBuilderSupport.checkValueIsTypeNotString( value, name, Line ) ){
         return value
     }else{
         def x1 = properties.remove("x1")
         def y1 = properties.remove("y1")
         def x2 = properties.remove("x2")
         def y2 = properties.remove("y2")
         x1 = x1 ? x1 : 0
         y1 = y1 ? y1 : 0
         x2 = x2 ? x2 : 0
         y2 = y2 ? y2 : 0
         return new Line( x1 as int, y1 as int, x2 as int, y2 as int )
     }
 }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
      if( parent instanceof FieldWarpFilterProvider ){
         isOut ? addOutLine(parent,child) : addInLine(parent,child)
      }else{
         throw new IllegalArgumentException("parent doesn't allow nesting of fieldWarpLines.")
      }
   }

   private void addOutLine( filter, line ){
      List outlines = filter.filter.outLines as List
      if( !outlines ) outlines = []
      outlines << line
      filter.filter.outLines = outlines as Line[]
   }

   private void addInLine( filter, line ){
      List inlines = filter.filter.inLines as List
      if( !inlines ) inlines = []
      inlines << line
      filter.filter.inLines = inlines as Line[]
   }
}
