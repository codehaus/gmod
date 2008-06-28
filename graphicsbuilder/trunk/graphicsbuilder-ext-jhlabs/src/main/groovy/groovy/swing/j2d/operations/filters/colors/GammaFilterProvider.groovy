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

package groovy.swing.j2d.operations.filters.colors

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.GammaFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GammaFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['gamma','rGamma','gGamma','bGamma']

   def gamma
   def rGamma
   def gGamma
   def bGamma

   GammaFilterProvider() {
      super( "gamma" )
      filter = new GammaFilter()
   }

   protected void setFilterProperty( name, value ){
      if( name == "rGamma" || name == "gGamma" || name == "bGamma" ){
         def m = gamma != null ? gamma : 1
         def r = rGamma != null ? rGamma : m
         def g = gGamma != null ? gGamma : m
         def b = bGamma != null ? bGamma : m
         filter = new GammaFilter( r as float, g as float, b as float )
      }else{
         super.setFilterProperty( name, value )
      }
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "gamma":
            return value as float
         default:
            return super.convertValue(property,value)
      }
   }
}