package org.codehaus.groovy2.mixin.lang;

import java.math.BigInteger;

import org.codehaus.groovy2.lang.ClassMetaClass;
import org.codehaus.groovy2.lang.ClassMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class LongMixin {
  public static long add(long l1, long l2) {
    return l1 + l2;
  }
  
  public static void __init__(ClassMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(BigInteger.class));
      mutator.addMixin(RT.getMetaClass(LongMixin.class));
    } finally {
      mutator.close();
    }
  }
}
