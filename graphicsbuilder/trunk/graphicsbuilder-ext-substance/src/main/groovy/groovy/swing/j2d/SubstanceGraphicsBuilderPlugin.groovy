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

import groovy.swing.j2d.factory.FilterFactory
import groovy.swing.j2d.factory.ColorSchemeFactory
import groovy.swing.j2d.factory.ShaperFactory
import groovy.swing.j2d.operations.filters.substance.*
import org.jvnet.substance.shaperpack.*
import org.jvnet.substance.watermarkpack.flamefractal.*

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
      builder.registerFactory( "fractalFlame", new FilterFactory(FractalFlameFilterProvider) )

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
      
      builder.addShortcut( "fractalFlame", 'colorScheme1', 'cs1' )
      builder.addShortcut( "fractalFlame", 'colorScheme2', 'cs2' )
      builder.addShortcut( "fractalFlame", 'iterations', 'i' )
      
      builder.aquaColorScheme = builder.colorScheme("Aqua")
      builder.barbyPinkColorScheme = builder.colorScheme("BarbyPink")
      builder.bottleGreenColorScheme = builder.colorScheme("BottleGreen")
      builder.brownColorScheme = builder.colorScheme("Brown")
      builder.charcoalColorScheme = builder.colorScheme("Charcoal")
      builder.cremeColorScheme = builder.colorScheme("Creme")
      builder.darkGrayColorScheme = builder.colorScheme("DarkGray")
      builder.darkMetallicColorScheme = builder.colorScheme("DarkMetallic")
      builder.darkVioletColorScheme = builder.colorScheme("DarkViolet")
      builder.desertSandColorScheme = builder.colorScheme("DesertSand")
      builder.ebonyColorScheme = builder.colorScheme("Ebony")
      builder.jadeForestColorScheme = builder.colorScheme("JadeForest")
      builder.lightAquaColorScheme = builder.colorScheme("LightAqua")
      builder.lightGrayColorScheme = builder.colorScheme("LightGray")
      builder.limeGreenColorScheme = builder.colorScheme("LimeGreen")
      builder.metallicColorScheme = builder.colorScheme("Metallic")
      builder.oliveColorScheme = builder.colorScheme("Olive")
      builder.orangeColorScheme = builder.colorScheme("Orange")
      builder.purpleColorScheme = builder.colorScheme("Purple")
      builder.raspberryColorScheme = builder.colorScheme("Raspberry")
      builder.sepiaColorScheme = builder.colorScheme("Sepia")
      builder.steelBlueColorScheme = builder.colorScheme("SteelBlue")
      builder.sunfireRedColorScheme = builder.colorScheme("SunfireRed")
      builder.sunGlareColorScheme = builder.colorScheme("SunGlare")
      builder.sunsetColorScheme = builder.colorScheme("Sunset")
      builder.terracottaColorScheme = builder.colorScheme("Terracotta")
      builder.ultramarineColorScheme = builder.colorScheme("Ultramarine")
      
      builder.kaleidoscopeIFS = new Kaleidoscope()
      builder.scripturesIFS = new Scriptures()
      builder.singularityIFS = new Singularity()
      builder.vortexIFS = new Vortex()
   }
}