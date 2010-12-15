package org.codehaus.groovy2.mixin.lang;

import groovy2.lang.MetaClass;

import org.codehaus.groovy2.lang.ExpandoMetaClass;
import org.codehaus.groovy2.lang.ExpandoMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class ObjectMixin {
  public static MetaClass getMetaClass(Object o) {
    return RT.metaClass.get(o.getClass());
  }
  
  /* This method is called directly when initializing universe
   * because we dn't want to introduce cycle
   * when processing the ObjectMixin
   */
  public static void boot(ExpandoMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addMixin(RT.getMetaClass(ObjectMixin.class));
    } finally {
      mutator.close();
    }
  }
}