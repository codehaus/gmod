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

import groovy.swing.j2d.factory.*
import org.jdesktop.animation.timing.Animator.Direction
import org.jdesktop.animation.timing.Animator.EndBehavior
import org.jdesktop.animation.timing.Animator.RepeatBehavior

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class AnimationGraphicsBuilderPlugin {
   public static void registerOperations( GraphicsBuilder builder ) {
      builder.registerFactory( "animate", new TimingFrameworkFactory() )

      // animation related variables
      builder.endBehaviorHold = EndBehavior.HOLD
      builder.endBehaviorReset = EndBehavior.RESET
      builder.directionForward = Direction.FORWARD
      builder.directionBackward = Direction.BACKWARD
      builder.repeatBehaviorLoop = RepeatBehavior.LOOP
      builder.repeatBehaviorReverse = RepeatBehavior.REVERSE
      builder.inifiniteDuration = -1
      builder.inifiteRepeatCount = -1
   }
}