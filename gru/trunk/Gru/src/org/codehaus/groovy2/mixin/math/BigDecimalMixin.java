package org.codehaus.groovy2.mixin.math;

import org.codehaus.groovy2.lang.ClassMetaClass;
import org.codehaus.groovy2.lang.ClassMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class BigDecimalMixin {
  
  //BigDecimal already has add/subtract/multiply/divide
  
  public static void init(ClassMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(Object.class));
      //mutator.addMixin(RT.getMetaClass(BigDecimalMixin.class));
    } finally {
      mutator.close();
    }
  }
}
