package org.codehaus.groovy2.lang;

import groovy2.lang.MetaClass;
import groovy2.lang.mop.MOPDoCallEvent;
import groovy2.lang.mop.MOPNewInstanceEvent;
import groovy2.lang.mop.MOPPropertyEvent;
import groovy2.lang.mop.MOPInvokeEvent;
import groovy2.lang.mop.MOPOperatorEvent;
import groovy2.lang.mop.MOPResult;

import java.dyn.CallSite;
import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.MethodHandles.Lookup;
import java.dyn.MethodType;
import java.dyn.NoAccessException;
import java.util.HashMap;
import java.util.List;

import org.codehaus.groovy2.dyn.Switcher;


public class MOPLinker {
  public enum MOPKind {
    MOP_GET_PROPERTY("getProperty"),
    MOP_SET_PROPERTY("setProperty"),
    MOP_INVOKE("invoke"),
    MOP_NEW_INSTANCE("newInstance"),
    MOP_OPERATOR("operator"),
    MOP_DO_CALL("doCall");
    
    final String protocol;

    private MOPKind(String textKind) {
      this.protocol = textKind;
    }
  }
  
  private static final HashMap<String, MOPKind> indexMap;
  static {
    HashMap<String, MOPKind> map = new HashMap<String, MOPKind>();
    for(MOPKind kind: MOPKind.values()) {
      map.put(kind.protocol, kind);
    }
    indexMap = map;
  }
  
  public static String mangle(MOPKind metaProtocolKind, String name) {
    return metaProtocolKind.protocol+'$'+name;
  }
  
  public static CallSite bootstrap(Class<?> declaringClass, String indyName, MethodType type) {
    try {

      int index = indyName.indexOf('$');
      if (index == -1) {
        throw new LinkageError("no meta protocol "+indyName);
      }

      String metaProtocol = indyName.substring(0, index);
      String name = indyName.substring(index + 1);

      MOPKind metaProtocolKind = indexMap.get(metaProtocol);
      if (metaProtocolKind == null) {
        throw new LinkageError("unknown meta protocol "+metaProtocol);
      }

      MOPCallSite callSite = new MOPCallSite(declaringClass, metaProtocolKind, name);
      //Class<?> receiverType = type.parameterType(0);
      /*if (Modifier.isFinal(receiverType.getModifiers())) { // static receiver

      }*/ 

      MethodHandle target = MethodHandles.insertArguments(FALLBACK, 0, callSite);
      target = MethodHandles.collectArguments(target, type.generic());
      target = MethodHandles.convertArguments(target, type);

      MethodHandle reset = MethodHandles.insertArguments(RESET, 0, callSite, target);
      reset = MethodHandles.collectArguments(reset, type.generic());
      reset = MethodHandles.convertArguments(reset, type);
      callSite.reset = reset;
      
      callSite.lazy = true;
      
      callSite.setTarget(target);
      return callSite;

    } catch(RuntimeException e) {
      e.printStackTrace();
      throw e;
    } catch(Error e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  
  
  static class MOPCallSite extends CallSite {
    final Class<?> declaringClass;
    final MOPKind mopKind;
    final String name;
    MethodHandle reset;
    volatile boolean lazy;
    
    public MOPCallSite(Class<?> declaringClass, MOPKind mopKind, String name) {
      this.declaringClass = declaringClass;
      this.mopKind = mopKind;
      this.name = name;
    }
    
    @Override
    public String toString() {
      return "CS: "+declaringClass.getName()+'.'+mopKind+'$'+name;
    }
  }
  
  public static Object fallback(MOPCallSite callSite, Object[] args) throws Throwable {
    //System.out.println("fallback "+callSite+" args " + java.util.Arrays.toString(args));
    
    try {
      MethodType type = callSite.getTarget().type();
      
      boolean isStatic = false;
      Class<?> receiverClass = type.parameterType(0);
      boolean isReceiverClassPrimitive = receiverClass.isPrimitive();
      if (!isReceiverClassPrimitive) {
        Object receiver = args[0];
        receiverClass = receiver.getClass();   // object -> class -> metaclass
        if (receiverClass == Class.class) {    // is a static call ? 
          receiverClass = (Class<?>)receiver;  // class -> metaclass
          isStatic = true;
        }
      }
      MetaClass metaClass = RT.getMetaClass(receiverClass);
      MethodType dynamicType = dynamicType(receiverClass, type, args);
      
      //System.out.println("MOP linker "+callSite.mopKind+"$"+callSite.name+" "+clazz+" "+metaClass+" "+isStatic);
      
      // special case: StaticClass.metaClass is hardcoded
      // because it will be never change, no need to hold the mutation lock
      if (isStatic && callSite.mopKind == MOPKind.MOP_GET_PROPERTY && "metaClass".equals(callSite.name)){
        MethodHandle target = MethodHandles.convertArguments(getMetaClass, type);
        callSite.setTarget(target);
        return target.invokeVarargs(args);
      }
      
      /* The call to the MOP must be done with a lock on the metaclass mutation
       * because if another thread do mutation, there is a risk that
       * the new target installed may be erased when installing the fast-path.
       */
      MethodHandle target;

      MethodHandle oldTarget = callSite.getTarget(); 

      MOPResult result = upcallMOP(callSite.mopKind, metaClass, callSite.lazy, oldTarget, callSite.reset, callSite.declaringClass, isStatic, callSite.name, dynamicType);

      target = result.getTarget().asMethodHandle();
      MethodType targetType = target.type();
      int targetTypeCount = targetType.parameterCount();
      int typeCount = type.parameterCount();
      if (targetTypeCount > typeCount) { 
        throw new LinkageError("target type has more parameters than callsite arguments"+
            target+" "+type);
      }
      // we allow a closure with less parameters than callsite arguments
      if (targetTypeCount < typeCount) {
        target = MethodHandles.dropArguments(target, targetTypeCount,
            type.parameterList().subList(targetTypeCount, typeCount));
      }
      target = MethodHandles.convertArguments(target, type);

      // prepends switcher guards
      target = prependSwitcherGuards(target, result.getConditions(), callSite.reset);

      if (!isReceiverClassPrimitive) {
        MethodHandle test = (isStatic)? INSTANCE_CHECK: CLASS_CHECK;
        test = MethodHandles.insertArguments(test, 0, receiverClass);
        test = MethodHandles.convertArguments(test, MethodType.methodType(boolean.class, type.parameterType(0)));

        MethodHandle guard = MethodHandles.guardWithTest(test, target, oldTarget);
        callSite.setTarget(guard);

      } else {
        // the receiver class is primitive, so it's not a polymorphic call
        callSite.setTarget(target);
      } 
      
      return target.invokeVarargs(args);
      
    } catch(Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }
  
  private static MethodHandle prependSwitcherGuards(MethodHandle target, List<Switcher> conditions, MethodHandle reset) {
    for(int i=conditions.size(); --i>=0;) {
      target = conditions.get(i).guard(target, reset);
    }
    return target;
  }

  public static Object reset(MOPCallSite callSite, MethodHandle fallback, Object[] args) throws Throwable {
    //System.out.println("reset");
    
    // we don't hold any lock here, so perhaps the target will be written
    // before being read by the fallback, in that case, it will create a method handle tree
    // containing an invalidated method handle.
    // But because fallback install the guard before the fallback tree,
    // the semantics will be ok, the MH tree will just contains dead code.
    callSite.setTarget(fallback);
    
    // start with a fresh callsite, new generated target can be lazy
    callSite.lazy = true;     // volatile write, force target to be written
    
    return fallback(callSite, args);
  }
  
  private static final MethodHandle getMetaClass;
  static {
    try {
      Lookup lookup = MethodHandles.publicLookup();
      getMetaClass = lookup.findStatic(RT.class, "getMetaClass",
          MethodType.methodType(MetaClass.class, Class.class));
    } catch (NoAccessException e) {
      throw new LinkageError(e.getMessage(), e);
    }
  }
  
  private static MethodType dynamicType(Class<?> receiverClass, MethodType methodType, Object[] args) {
    Class<?>[] types = new Class<?>[methodType.parameterCount()];
    types[0] = receiverClass;
    for(int i=1;i <types.length; i++) {
      Class<?> type = methodType.parameterType(i);
      if (!type.isPrimitive()) {  // null check not needed for primitive
        Object arg = args[i];
        type = (arg == null)? type: arg.getClass();
      }
      types[i] = type;
    }
    return MethodType.methodType(methodType.returnType(), types);
  }

  private static MOPResult upcallMOP(MOPKind kind, MetaClass metaClass, boolean lazyAllowed, MethodHandle fallback, MethodHandle reset, Class<?> declaringClass, boolean isStatic, String name, MethodType type) {
    switch(kind) {
    case MOP_GET_PROPERTY:
      return metaClass.mopGetProperty(new MOPPropertyEvent(declaringClass, lazyAllowed, fallback, reset, isStatic, name, RT.getMetaClass(type.returnType())));
    case MOP_SET_PROPERTY:
      return metaClass.mopGetProperty(new MOPPropertyEvent(declaringClass, lazyAllowed, fallback, reset, isStatic, name, RT.getMetaClass(type.parameterType(1))));
    case MOP_INVOKE:
      return metaClass.mopInvoke(new MOPInvokeEvent(declaringClass, lazyAllowed, fallback, reset, isStatic, name, RT.asFunctionType(type)));
    case MOP_NEW_INSTANCE:
      return metaClass.mopNewInstance(new MOPNewInstanceEvent(declaringClass, lazyAllowed, fallback, reset, RT.asFunctionType(type)));
    case MOP_OPERATOR:
      return metaClass.mopOperator(new MOPOperatorEvent(declaringClass, lazyAllowed, fallback, reset, name, RT.asFunctionType(type)));
    case MOP_DO_CALL:
      return metaClass.mopDoCall(new MOPDoCallEvent(declaringClass, lazyAllowed, fallback, reset, RT.asFunctionType(type)));
    }
    return null;
  }

  public static boolean instanceCheck(Class<?> clazz, Object o) {
    return clazz == o;
  }
  
  public static boolean classCheck(Class<?> clazz, Object o) {
    return clazz == o.getClass();
  }
  
  private static final MethodHandle INSTANCE_CHECK;
  private static final MethodHandle CLASS_CHECK;
  private static final MethodHandle FALLBACK;
  private static final MethodHandle RESET;
  static {
    try {
      Lookup lookup = MethodHandles.publicLookup();
      INSTANCE_CHECK = lookup.findStatic(MOPLinker.class, "instanceCheck",
          MethodType.methodType(boolean.class, Class.class, Object.class));
      CLASS_CHECK = lookup.findStatic(MOPLinker.class, "classCheck",
          MethodType.methodType(boolean.class, Class.class, Object.class));
      FALLBACK = lookup.findStatic(MOPLinker.class, "fallback",
          MethodType.methodType(Object.class, MOPCallSite.class, Object[].class));
      RESET = lookup.findStatic(MOPLinker.class, "reset",
          MethodType.methodType(Object.class, MOPCallSite.class, MethodHandle.class, Object[].class));
    } catch (NoAccessException e) {
      throw new LinkageError(e.getMessage(), e);
    }
  }
}
