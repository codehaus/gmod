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

package groovy.swing.j2d

import groovy.swing.j2d.factory.ColorSchemeFactory
import groovy.swing.j2d.factory.ShaperFactory
import org.jvnet.substance.shaperpack.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SubstanceGraphicsBuilderPlugin {
   public static void registerOperations( GraphicsBuilder builder ) {
      builder.registerFactory( "butterflyShape", new ShaperFactory("butterflyShape",ButterflyButtonShaper) )
      builder.registerFactory( "dolphinShape", new ShaperFactory("dolphinShape",DolphinButtonShaper) )
      builder.registerFactory( "fishShape", new ShaperFactory("fishShape",FishButtonShaper) )
      builder.registerFactory( "footShape", new ShaperFactory("footShape",FootButtonShaper) )
      builder.registerFactory( "iceCreamShape", new ShaperFactory("iceCreamShape",IceCreamButtonShaper) )
      builder.registerFactory( "raceCarShape", new ShaperFactory("raceCarShape",RaceCarButtonShaper) )
      builder.registerFactory( "rhinoShape", new ShaperFactory("rhinoShape",RhinoButtonShaper) )
      builder.registerFactory( "stegosaurusShape", new ShaperFactory("stegosaurusShape",StegosaurusButtonShaper) )

      def colorSchemeFactory = new ColorSchemeFactory()
      builder.registerFactory( "colorScheme", colorSchemeFactory )
      builder.registerFactory( "cs", colorSchemeFactory )

      ["butterfly","dolphin","fish","foot","iceCream","raceCar","rhino","stegosaurus"].each { nodeName ->
         nodeName += "Shape"
         builder.addShortcut( nodeName, 'width', 'w' )
         builder.addShortcut( nodeName, 'height', 'h' )
         builder.addShortcut( nodeName, 'borderColor', 'bc' )
         builder.addShortcut( nodeName, 'borderWidth', 'bw' )
         builder.addShortcut( nodeName, 'fill', 'f' )
         builder.addShortcut( nodeName, 'opacity', 'o' )
         builder.addShortcut( nodeName, 'composite', 'c' )
         builder.addShortcut( nodeName, 'asShape', 's' )
         builder.addShortcut( nodeName, 'asImage', 'i' )
         builder.addShortcut( nodeName, 'passThrough', 'pt' )
      }

      builder.variables['aquaColorScheme'] = builder.colorScheme( "Aqua")
      builder.variables['barbyPinkColorScheme'] = builder.colorScheme( "BarbyPink")
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "BlendBi"
      builder.variables['bottleGreenColorScheme'] = builder.colorScheme( "BottleGreen")
      builder.variables['brownColorScheme'] = builder.colorScheme( "Brown")
      builder.variables['charcoalColorScheme'] = builder.colorScheme( "Charcoal")
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Colorblind"
      builder.variables['cremeColorScheme'] = builder.colorScheme( "Creme")
      builder.variables['darkGrayColorScheme'] = builder.colorScheme( "DarkGray")
      builder.variables['darkMetallicColorScheme'] = builder.colorScheme( "DarkMetallic")
      builder.variables['darkVioletColorScheme'] = builder.colorScheme( "DarkViolet")
      builder.variables['desertSandColorScheme'] = builder.colorScheme( "DesertSand")
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Deuteranopia"
      builder.variables['ebonyColorScheme'] = builder.colorScheme( "Ebony")
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "HueShift"
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Inverted"
      builder.variables['jadeForestColorScheme'] = builder.colorScheme( "JadeForest")
      builder.variables['lightAquaColorScheme'] = builder.colorScheme( "LightAqua")
      builder.variables['lightGrayColorScheme'] = builder.colorScheme( "LightGray")
      builder.variables['limeGreenColorScheme'] = builder.colorScheme( "LimeGreen")
      builder.variables['metallicColorScheme'] = builder.colorScheme( "Metallic")
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Negated"
      builder.variables['oliveColorScheme'] = builder.colorScheme( "Olive")
      builder.variables['orangeColorScheme'] = builder.colorScheme( "Orange")
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Protanopia"
      builder.variables['purpleColorScheme'] = builder.colorScheme( "Purple")
      builder.variables['raspberryColorScheme'] = builder.colorScheme( "Raspberry")
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Saturated"
      builder.variables['sepiaColorScheme'] = builder.colorScheme( "Sepia")
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Shade"
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Shift"
      builder.variables['steelBlueColorScheme'] = builder.colorScheme( "SteelBlue")
      builder.variables['sunfireRedColorScheme'] = builder.colorScheme( "SunfireRed")
      builder.variables['sunGlareColorScheme'] = builder.colorScheme( "SunGlare")
      builder.variables['sunsetColorScheme'] = builder.colorScheme( "Sunset")
      builder.variables['terracottaColorScheme'] = builder.colorScheme( "Terracotta")
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Tint"
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Tone"
      //builder.variables['aquaColorScheme'] = builder.colorScheme( "Trinatopia"
      builder.variables['ultramarineColorScheme'] = builder.colorScheme( "Ultramarine")
   }
}