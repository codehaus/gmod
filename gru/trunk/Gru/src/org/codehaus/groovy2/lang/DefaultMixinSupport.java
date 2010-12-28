package org.codehaus.groovy2.lang;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.MethodType;
import java.dyn.NoAccessException;

class DefaultMixinSupport {
  //TODO add circular init detection ??
  public static void init(ExpandoMetaClass metaClass) {
    Class<?> clazz = metaClass.getType();
    if (clazz.isArray()) {  //TODO add array support
      return;
    }
    
    String mixinName = clazz.getName() + "Mixin";
    if (clazz.isPrimitive()) {
      mixinName = "org.codehaus.groovy2.mixin.lang." + Utils.capitalize(mixinName); 
    } else {
      if (mixinName.startsWith("java.") || mixinName.startsWith("groovy2.")) {
        int index = mixinName.indexOf('.');
        mixinName = "org.codehaus.groovy2.mixin." + mixinName.substring(index + 1);
      }
    }
    
    Class<?> mixinClass;
    try {
      mixinClass = Class.forName(mixinName);
    } catch (ClassNotFoundException e) {
      //System.out.println("init mixin: "+mixinName+" no mixin");
      return;  // no mixin
    }
    
    MethodHandle init;
    try {
      init = MethodHandles.lookup().findStatic(mixinClass, "init",
          MethodType.methodType(void.class, ExpandoMetaClass.class));
    } catch (NoAccessException e) {
      //System.out.println("init mixin: "+mixinName+" no init");
      return; // no init
    }
    try {
      //FIXME: invokeExact should be sufficient but it has no eclipse support :(
      init.invokeWithArguments(metaClass);
    } catch (Throwable e) {
      throw RT.unsafeThrow(e);
    }
  }
}
