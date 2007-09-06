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

package groovy.swing.j2d

import groovy.swing.j2d.factory.ExtPathOperationFactory
import groovy.swing.j2d.impl.ArcToExtPathOperation
import groovy.swing.j2d.impl.MoveToExtPathOperation
import groovy.swing.j2d.impl.LineToExtPathOperation
import groovy.swing.j2d.impl.QuadToExtPathOperation
import groovy.swing.j2d.impl.CurveToExtPathOperation
import groovy.swing.j2d.impl.HLineExtPathOperation
import groovy.swing.j2d.impl.VLineExtPathOperation
import groovy.swing.j2d.impl.ShapeExtPathOperation
import groovy.swing.j2d.impl.CloseExtPathOperation
import groovy.swing.j2d.operations.ExtPathGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class BatikGraphicsBuilderHelper {
   public static void registerOperations( GraphicsBuilder builder ) {
      builder.registerGraphicsOperationBeanFactory( "xpath", ExtPathGraphicsOperation )
      builder.registerFactory( "xarcTo", new ExtPathOperationFactory( ArcToExtPathOperation) )
      builder.registerFactory( "xmoveTo", new ExtPathOperationFactory( MoveToExtPathOperation) )
      builder.registerFactory( "xlineTo", new ExtPathOperationFactory( LineToExtPathOperation) )
      builder.registerFactory( "xquadTo", new ExtPathOperationFactory( QuadToExtPathOperation) )
      builder.registerFactory( "xcurveTo", new ExtPathOperationFactory( CurveToExtPathOperation) )
      builder.registerFactory( "xhline", new ExtPathOperationFactory( HLineExtPathOperation) )
      builder.registerFactory( "xvline", new ExtPathOperationFactory( VLineExtPathOperation) )
      builder.registerFactory( "xshape", new ExtPathOperationFactory( ShapeExtPathOperation) )
      builder.registerFactory( "xclose", new ExtPathOperationFactory( CloseExtPathOperation) )
   }
}