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
   }
}