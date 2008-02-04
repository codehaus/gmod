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

package groovy.swing.j2d.operations.filters.distort

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.PerspectiveFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class PerspectiveFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['x0','y0','x1','y1','x2','y2','x3','y3']

   def x0 = 0
   def y0 = 0
   def x1 = 0
   def y1 = 0
   def x2 = 0
   def y2 = 0
   def x3 = 0
   def y3 = 0

   PerspectiveFilterProvider() {
      super( "perspective" )
      filter = new PerspectiveFilter()
   }

   protected void setFilterProperty( name, value ){
      if( name =~ /x|y/ ){
         filter.setCorners( x0 as float,
                            y0 as float,
                            x1 as float,
                            y1 as float,
                            x2 as float,
                            y2 as float,
                            x3 as float,
                            y3 as float )
      }else{
         super.setFilterProperty( name, value )
      }
   }
}