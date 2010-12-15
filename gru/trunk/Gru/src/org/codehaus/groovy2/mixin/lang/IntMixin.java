package org.codehaus.groovy2.mixin.lang;

import org.codehaus.groovy2.lang.ExpandoMetaClass;
import org.codehaus.groovy2.lang.ExpandoMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class IntMixin {
  public static int add(int i1, int i2) {
    return i1 + i2;
  }
  
  public static void init(ExpandoMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(long.class));
      mutator.addSuperType(RT.getMetaClass(float.class));
      mutator.addSuperType(RT.getMetaClass(double.class));
      mutator.addMixin(RT.getMetaClass(IntMixin.class));
    } finally {
      mutator.close();
    }
  }
}