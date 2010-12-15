package org.codehaus.groovy2.mixin.lang;

import org.codehaus.groovy2.lang.ExpandoMetaClass;
import org.codehaus.groovy2.lang.ExpandoMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class CharMixin {
  public static int add(char i1, char i2) {
    return i1 + i2;
  }
  
  public static void init(ExpandoMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(int.class));
      mutator.addMixin(RT.getMetaClass(CharMixin.class));
    } finally {
      mutator.close();
    }
  }
}
