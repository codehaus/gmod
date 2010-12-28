package org.codehaus.groovy2.mixin.lang;

import org.codehaus.groovy2.lang.ExpandoMetaClass;
import org.codehaus.groovy2.lang.ExpandoMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class BooleanMixin {
  public static void __init__(ExpandoMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(byte.class));
      mutator.addMixin(RT.getMetaClass(BooleanMixin.class));
    } finally {
      mutator.close();
    }
  }
}
