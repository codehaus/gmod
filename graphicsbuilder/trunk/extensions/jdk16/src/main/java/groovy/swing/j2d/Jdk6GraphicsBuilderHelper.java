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

package groovy.swing.j2d;

import groovy.swing.j2d.factory.GradientStopFactory;
import groovy.swing.j2d.factory.PathOperationFactory;
import groovy.swing.j2d.operations.LinearGradientPaintGraphicsOperation;
import groovy.swing.j2d.operations.PathGraphicsOperation;
import groovy.swing.j2d.operations.RadialGradientPaintGraphicsOperation;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class Jdk6GraphicsBuilderHelper {
   public static void registerOperations( GraphicsBuilder builder ) {
      //
      // paths
      //
      builder.registerGraphicsOperationFactory( "path", PathGraphicsOperation.class, true );
      builder.registerFactory( "moveTo", new PathOperationFactory(
            PathGraphicsOperation.MoveTo.class ) );
      builder.registerFactory( "lineTo", new PathOperationFactory(
            PathGraphicsOperation.LineTo.class ) );
      builder.registerFactory( "quadTo", new PathOperationFactory(
            PathGraphicsOperation.QuadTo.class ) );
      builder.registerFactory( "curveTo", new PathOperationFactory(
            PathGraphicsOperation.CurveTo.class ) );

      //
      // JDK 1.6
      //
      builder.registerFactory( "gradientStop", new GradientStopFactory() );
      builder.registerGraphicsOperationFactory( "linearGradient",
            LinearGradientPaintGraphicsOperation.class, false );
      builder.registerGraphicsOperationFactory( "radialGradient",
            RadialGradientPaintGraphicsOperation.class, false );
   }
}