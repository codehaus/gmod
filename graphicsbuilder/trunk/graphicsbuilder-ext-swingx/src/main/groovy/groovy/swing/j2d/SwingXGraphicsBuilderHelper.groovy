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

import groovy.swing.j2d.factory.TimingFrameworkFactory
import groovy.swing.j2d.operations.MorphGraphicsOperation
import groovy.swing.j2d.operations.StarGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SwingXGraphicsBuilderHelper {
   public static void registerOperations( GraphicsBuilder builder ) {
      builder.registerGraphicsOperationBeanFactory( "morph", MorphGraphicsOperation )
      builder.registerGraphicsOperationBeanFactory( "star", StarGraphicsOperation )
      builder.registerFactory( "animate", new TimingFrameworkFactory() )
   }
}