package org.codehaus.groovy2.mixin.lang;

import java.math.BigDecimal;

import org.codehaus.groovy2.lang.ClassMetaClass;
import org.codehaus.groovy2.lang.ClassMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class DoubleMixin {
  public static double add(double i1, double i2) {
    return i1 + i2;
  }
  
  public static void __init__(ClassMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(BigDecimal.class));
      mutator.addMixin(RT.getMetaClass(DoubleMixin.class));
    } finally {
      mutator.close();
    }
  }
}
