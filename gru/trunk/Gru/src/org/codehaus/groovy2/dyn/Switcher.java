package org.codehaus.groovy2.dyn;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.NoAccessException;


/** This code should be removed when switcher or equivalent
 *  will be implemented in the JSR 292 API.
 */
public final class Switcher {
  private volatile boolean switchz;   // accessed using the method handle below
  private final MethodHandle switchTest;
  
  private static final MethodHandle STATIC_SWITCH;
  static {
    try {
      STATIC_SWITCH = MethodHandles.lookup().findGetter(Switcher.class, "switchz", boolean.class);
    } catch (NoAccessException e) {
      throw (AssertionError)new AssertionError().initCause(e);
    }
  }
  
  public Switcher() {
    this.switchz = true;
    this.switchTest = MethodHandles.insertArguments(STATIC_SWITCH, 0, this);  // bind to current switcher
  }
  
  public static void invalidate(Switcher... switchers) {
    for(Switcher switcher: switchers) {
      switcher.switchz = false;
    }
  }
  
  public MethodHandle guard(MethodHandle target, MethodHandle fallback) {
    return MethodHandles.guardWithTest(switchTest, target, fallback);
  }
}
