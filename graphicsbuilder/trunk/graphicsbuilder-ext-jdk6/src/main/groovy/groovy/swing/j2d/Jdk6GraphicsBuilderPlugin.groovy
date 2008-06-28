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

import groovy.swing.j2d.factory.GradientStopFactory
import groovy.swing.j2d.operations.paints.LinearGradientPaintGraphicsOperation
import groovy.swing.j2d.operations.paints.RadialGradientPaintGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class Jdk6GraphicsBuilderPlugin {
   public static void registerOperations( GraphicsBuilder builder ) {
      //
      // JDK 1.6
      //
      builder.registerFactory( "stop", new GradientStopFactory() )
      builder.registerGraphicsOperationBeanFactory( "linearGradient",
            LinearGradientPaintGraphicsOperation )
      builder.registerGraphicsOperationBeanFactory( "radialGradient",
            RadialGradientPaintGraphicsOperation )

      builder.addShortcut( 'stop', 'red', 'r' )
      builder.addShortcut( 'stop', 'green', 'g' )
      builder.addShortcut( 'stop', 'blue', 'b' )
      builder.addShortcut( 'stop', 'alpha', 'a' )
      builder.addShortcut( 'stop', 'offset', 's' )
      builder.addShortcut( 'stop', 'color', 'c' )
      builder.addShortcut( 'stop', 'opacity', 'o' )
      builder.addShortcut( 'radialGradient', 'radius', 'r' )
   }
}