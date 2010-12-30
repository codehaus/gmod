package org.codehaus.groovy2.mixin.lang;

import org.codehaus.groovy2.lang.ClassMetaClass;
import org.codehaus.groovy2.lang.ClassMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class FloatMixin {
  public static float add(float i1, float i2) {
    return i1 + i2;
  }
  
  public static void __init__(ClassMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(double.class));
      mutator.addMixin(RT.getMetaClass(FloatMixin.class));
    } finally {
      mutator.close();
    }
  }
}
