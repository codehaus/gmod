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

import groovy.swing.j2d.factory.ExtPathOperationFactory
import groovy.swing.j2d.operations.shapes.ExtPathGraphicsOperation
import groovy.swing.j2d.operations.shapes.path.ArcToExtPathOperation
import groovy.swing.j2d.operations.shapes.path.MoveToExtPathOperation
import groovy.swing.j2d.operations.shapes.path.LineToExtPathOperation
import groovy.swing.j2d.operations.shapes.path.QuadToExtPathOperation
import groovy.swing.j2d.operations.shapes.path.CurveToExtPathOperation
import groovy.swing.j2d.operations.shapes.path.HLineExtPathOperation
import groovy.swing.j2d.operations.shapes.path.VLineExtPathOperation
import groovy.swing.j2d.operations.shapes.path.ShapeExtPathOperation
import groovy.swing.j2d.operations.shapes.path.CloseExtPathOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class BatikGraphicsBuilderPlugin {
   public static void registerOperations( GraphicsBuilder builder ) {
      builder.registerGraphicsOperationBeanFactory( "xpath", ExtPathGraphicsOperation )
      builder.registerFactory( "xarcTo", new ExtPathOperationFactory( ArcToExtPathOperation) )
      builder.registerFactory( "xmoveTo", new ExtPathOperationFactory( MoveToExtPathOperation) )
      builder.registerFactory( "xlineTo", new ExtPathOperationFactory( LineToExtPathOperation) )
      builder.registerFactory( "xquadTo", new ExtPathOperationFactory( QuadToExtPathOperation) )
      builder.registerFactory( "xcurveTo", new ExtPathOperationFactory( CurveToExtPathOperation) )
      builder.registerFactory( "xhline", new ExtPathOperationFactory( HLineExtPathOperation) )
      builder.registerFactory( "xvline", new ExtPathOperationFactory( VLineExtPathOperation) )
      builder.registerFactory( "xshapeTo", new ExtPathOperationFactory( ShapeExtPathOperation) )
      builder.registerFactory( "xclose", new ExtPathOperationFactory( CloseExtPathOperation) )
   }
}