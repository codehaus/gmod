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

package org.kordamp.groovy.swing.jide.factory

import java.util.logging.Level
import java.util.logging.Logger
import java.awt.Component
import groovy.util.AbstractFactory
import groovy.util.FactoryBuilderSupport
import org.kordamp.groovy.swing.jide.impl.ResizableSVGIcon

import com.kitfox.svg.SVGCache
import com.kitfox.svg.SVGUniverse

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SVGIconFactory extends AbstractFactory {
   static Map aliases = [:]

   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      String path = properties.remove("path")
      ResizableSVGIcon svgIcon = new ResizableSVGIcon()
      if( path ){
         // handle svg aliases
         String alias = path.substring(0, path.indexOf(":") )
         if( alias && aliases.get(alias) ){
            path = aliases.get(alias) + (path - alias - ":")
         }
         InputStream is = null
         try{
            is = Thread.currentThread().contextClassLoader.getResourceAsStream( path )
            URI uri = SVGCache.getSVGUniverse().loadSVG( (InputStream) is, path)
            svgIcon.setSvgURI( uri )
         }catch( Exception e ){
            Logger.getLogger("global").log(Level.WARNING, "Can't create SVG image from path '${path}'", e)
         }
      }

      return svgIcon
   }

   public static void registerSVGAlias( String alias, String path ){
      aliases[alias] = path
   }
}