package org.codehaus.groovy2.lang;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.MethodType;
import java.dyn.NoAccessException;

import org.codehaus.groovy2.mixin.lang.ArrayMixin;

class DefaultMixinSupport {
  //TODO add circular init detection ??
  public static void init(ClassMetaClass metaClass) {
    Class<?> mixinClass = getMixinClass(metaClass);
    if (mixinClass == null) {  // no mixin
      return;
    }
    
    MethodHandle init;
    try {
      init = MethodHandles.lookup().findStatic(mixinClass, "__init__",
          MethodType.methodType(void.class, ClassMetaClass.class));
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
  
  private static Class<?> getMixinClass(ClassMetaClass metaClass) {
    Class<?> clazz = metaClass.getType();
    if (clazz.isArray()) {  // array class has a special mixin
      if (clazz == Object[].class || clazz.getComponentType().isPrimitive())
        return ArrayMixin.class;
      return null;
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
    
    try {
      return Class.forName(mixinName);
    } catch (ClassNotFoundException e) {
      //System.out.println("init mixin: "+mixinName+" no mixin");
      return null;  // no mixin
    }
  }
}
