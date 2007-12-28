/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovy.swing.j2d.binding

import org.codehaus.groovy.binding.*
import org.jdesktop.animation.timing.Animator
import org.jdesktop.animation.timing.Animator.Direction
import org.jdesktop.animation.timing.Animator.EndBehavior
import org.jdesktop.animation.timing.Animator.RepeatBehavior
import org.jdesktop.animation.timing.TimingTarget
import org.jdesktop.animation.timing.interpolation.KeyFrames
import org.jdesktop.animation.timing.interpolation.KeyValues
import org.jdesktop.animation.timing.interpolation.KeyTimes

/**
* @author <a href="mailto:shemnon@yahoo.com">Danno Ferrin</a>
* @version $Revision: 7797 $
* @since Groovy 1.1
*/
public class TimingFrameworkTriggerBinding implements TriggerBinding {
    public FullBinding createBinding(SourceBinding source, TargetBinding target) {
        return new TimingFrameworkFullBinding((ClosureSourceBinding) source, target)
    }
}

/**
 * @author <a href="mailto:shemnon@yahoo.com">Danno Ferrin</a>
 * @version $Revision: 7797 $
 * @since Groovy 1.1
 */
class TimingFrameworkFullBinding extends AbstractFullBinding implements TimingTarget {

    Range basicRange = 0.0f..1.0f
    Object[] specialRange

    KeyFrames keyFrames

    // Animator is final, can't extend it, so we must wrap it.
    protected Animator animator
    boolean bound

    TimingFrameworkFullBinding(ClosureSourceBinding source, TargetBinding target) {
        this(source, target, 1000)
    }

    TimingFrameworkFullBinding(SourceBinding source, TargetBinding target, int duration) {
        setSourceBinding(source)
        setTargetBinding(target)
        animator = new Animator(duration)
        animator.addTarget(this)
    }

    // basic pass through properties
    void setDuration(int duration) {animator.duration = duration}
    void setInterval(int interval) {animator.resolution = interval}
    void setResolution(int resolution) {animator.resolution = resolution}
    void setRepeatCount(double repeatCount) {animator.repeatCount = repeatCount}
    void setRepeatBehavior(RepeatBehavior repeatBehavior) {animator.repeatBehavior = repeatBehavior}
    void setStartDelay(int startDelay) {animator.startDelay = startDelay}
    void setStartDirection(Direction startDirection) {animator.startDirection = startDirection}
    void setEndBehavior(EndBehavior endBehavior) {animator.endBehavior = endBehavior}
    void setAcceleration(float acceleration) {animator.acceleration = acceleration}
    void setDeceleration(float deceleration) {animator.deceleration = deceleration}

    // surrigat ones...
    void setEaseIn(boolean ease) {acceleration = ease ? 0.25f : 0.0f}
    void setEaseOut(boolean ease) {deceleration = ease ? 0.25f : 0.0f}
    void setRepeat(boolean repeat) {
        animator.repeatCount = Animator.INFINITE
        animator.repeatBehavior = RepeatBehavior.LOOP
    }


    // more complex ones...
    void setStart(boolean start) {if (start) {animator.start()}}
    void setStartValue(startValue) {
        if (basicRange) {
            if (basicRange.containsWithinBounds(startValue)) {
                animator.startValue = ((startValue - basicRange.from) / basicRange.to - basicRange - from) as float
            }
            // else throw error?
        } // else throw error?
    }

    // intercept some strangeness
    public void setRange(newRange) {
        if (newRange instanceof Range) {
            keyFrames = new KeyFrames(new KeyValues(newRange.from, newRange.to))
        } else if (newRange instanceof List) {
            keyFrames = new KeyFrames(new KeyValues(* newRange))
        } else if (newRange instanceof Map) {
            Map timesMap = new TreeMap()
            newRange.each {k, v ->
                try {
                    timesMap.put(k as float, v)
                } catch (ClassCastException cce) {
                    throw new RuntimeException("The keys in an animate range should be all convertable to float, '$k' isn't")
                }
            }
            keyFrames = new KeyFrames(new KeyValues(*timesMap.values()), new KeyTimes(*timesMap.keySet()));
        }
    }

    void resetTimer() {
        animator.stop()
        animator.start()
    }

    public void bind() {
        if (!bound) {
            resetTimer()
            bound = true
        }
    }

    public void unbind() {
        if (bound) {
            animator.cancel()
            bound = false
        }
    }

    public void rebind() {
        if (bound) {
            resetTimer()
        }
    }



    //timingtarget methods
    public void timingEvent(float v) {
        ((ClosureSourceBinding) sourceBinding).setClosureArgument(keyFrames.getValue(v))
        update()
    }

    public void begin() {
        timingEvent(0.0f) // unneeded?
    }

    public void end() {
        timingEvent(1.0f) // unneeded?
    }

    public void repeat() {
        // ignore
    }

}