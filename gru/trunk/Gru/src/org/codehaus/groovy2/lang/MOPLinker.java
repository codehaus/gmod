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

      MethodHandle reset = MethodHandles.insertArguments(MOPLinker.RESET, 0, callSite, FALLBACK);
      reset = MethodHandles.collectArguments(target, type.generic());
      reset = MethodHandles.convertArguments(target, type);
      callSite.reset = reset;
      
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
    
    public MOPCallSite(Class<?> declaringClass, MOPKind mopKind, String name) {
      this.declaringClass = declaringClass;
      this.mopKind = mopKind;
      this.name = name;
    }
  }
  
  public static Object fallback(MOPCallSite callSite, Object[] args) throws Throwable {
    try {
      MethodHandle oldTarget = callSite.getTarget();
      MethodType type = oldTarget.type();
      
      boolean[] instanceChecks = new boolean[type.parameterCount()];
      MethodType dynamicType = dynamicType(type, instanceChecks, args);
      
      Class<?> clazz = dynamicType.parameterType(0);
      MetaClass metaClass = RT.getMetaClass(clazz);
      boolean isStatic = instanceChecks[0];
      
      //System.out.println("MOP linker "+callSite.mopKind+"$"+callSite.name+" "+clazz+" "+metaClass+" "+isStatic);
      
      // special case: StaticClass.metaClass is hardcoded
      if (isStatic && callSite.mopKind == MOPKind.MOP_GET_PROPERTY && "metaClass".equals(callSite.name)){
        MethodHandle target = MethodHandles.convertArguments(getMetaClass, type);
        callSite.setTarget(target);
        return target.invokeVarargs(args);
      }
      
      MOPResult result = upcallMOP(callSite.mopKind, metaClass, callSite.reset, callSite.declaringClass, isStatic, callSite.name, dynamicType);
      Throwable failure = result.getFailure();
      if (failure != null) {
        throw new LinkageError("MOP not found "+callSite.name+dynamicType+" reason "+failure.getMessage(), failure);
      }

      //FIXME, result.control is not used

      MethodHandle target = result.getTarget();
      
      target = MethodHandles.convertArguments(target, type);
      MethodHandle test = INSTANCE_CHECK;
      test = MethodHandles.insertArguments(test, 0, clazz);
      test = MethodHandles.convertArguments(test, MethodType.methodType(boolean.class, type.parameterType(0)));
      MethodHandle guard = MethodHandles.guardWithTest(test, target, oldTarget);

      callSite.setTarget(guard);
      return target.invokeVarargs(args);

    } catch(Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }
  
  public static Object reset(MOPCallSite callSite, MethodHandle fallback, Object[] args) throws Throwable {
    callSite.setTarget(fallback);
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
  
  private static MethodType dynamicType(MethodType methodType, boolean[] instanceCheck, Object[] args) {
    Class<?>[] types = new Class<?>[methodType.parameterCount()];
    for(int i=0;i <types.length; i++) {
      Class<?> type = methodType.parameterType(i);
      if (type.isPrimitive()) {
        types[i] = type;
        continue;
      }
      Object arg = args[i];
      type = arg.getClass();      // object -> class -> metaclass
      if (type == Class.class) {  // or class -> metaclass
        type = (Class<?>)arg;
        instanceCheck[i] = true;
      }
      types[i] = type;
    }
    return MethodType.methodType(methodType.returnType(), types);
  }

  private static MOPResult upcallMOP(MOPKind kind, MetaClass metaClass, MethodHandle fallback, Class<?> declaringClass, boolean isStatic, String name, MethodType type) {
    switch(kind) {
    case MOP_GET_PROPERTY:
      return metaClass.mopGetProperty(new MOPPropertyEvent(declaringClass, fallback, isStatic, name, RT.getMetaClass(type.returnType())));
    case MOP_SET_PROPERTY:
      return metaClass.mopGetProperty(new MOPPropertyEvent(declaringClass, fallback, isStatic, name, RT.getMetaClass(type.parameterType(1))));
    case MOP_INVOKE:
      return metaClass.mopInvoke(new MOPInvokeEvent(declaringClass, fallback, isStatic, name, RT.asFunctionType(type)));
    case MOP_NEW_INSTANCE:
      return metaClass.mopNewInstance(new MOPNewInstanceEvent(declaringClass, fallback, RT.asFunctionType(type)));
    case MOP_OPERATOR:
      return metaClass.mopOperator(new MOPOperatorEvent(declaringClass, fallback, name, RT.asFunctionType(type)));
    case MOP_DO_CALL:
      return metaClass.mopDoCall(new MOPDoCallEvent(declaringClass, fallback, RT.asFunctionType(type)));
    }
    return null;
  }

  public static boolean instanceCheck(Class<?> clazz, Object o) {
    return clazz == o;
  }
  
  private static final MethodHandle INSTANCE_CHECK;
  private static final MethodHandle FALLBACK;
  private static final MethodHandle RESET;
  static {
    try {
      Lookup lookup = MethodHandles.publicLookup();
      INSTANCE_CHECK = lookup.findStatic(MOPLinker.class, "instanceCheck",
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
