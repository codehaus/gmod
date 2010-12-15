package org.codehaus.groovy2.mixin.math;

import org.codehaus.groovy2.lang.ExpandoMetaClass;
import org.codehaus.groovy2.lang.ExpandoMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class BigIntegerMixin {
  
  //BigInteger already has add/subtract/multiply/divide
  
  public static void init(ExpandoMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(float.class));
      //mutator.addMixin(RT.getMetaClass(BigIntegerMixin.class));
    } finally {
      mutator.close();
    }
  }
}
