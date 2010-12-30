package org.codehaus.groovy2.lang;

import groovy2.lang.Failures;
import groovy2.lang.FunctionType;
import groovy2.lang.MetaClass;
import groovy2.lang.mop.MOPPropertyEvent;
import groovy2.lang.mop.MOPResult;

import java.dyn.ClassValue;
import java.dyn.MethodType;
import java.lang.reflect.Type;
import java.util.Objects;

import org.codehaus.groovy2.mixin.lang.ObjectMixin;

public class RT {
  public final static ClassValue<MetaClass> metaClass =
      new ClassValue<MetaClass>() {
        @Override
        protected MetaClass computeValue(Class<?> clazz) {
          Objects.nonNull(clazz);
          
          // wrapper and primitive should share the same metaclass
          if (!clazz.isPrimitive()) {
            clazz = Utils.getPrimitive(clazz);
            if (clazz.isPrimitive()) {
              return RT.getMetaClass(clazz);
            }
          }
          
          ClassMetaClass metaClass = new ClassMetaClass(clazz);
          DefaultMixinSupport.init(metaClass);
          
          //System.out.println("verbose load metaclass: "+metaClass);
          
          return metaClass;
        }
      };
    
      
  public static MetaClass getMetaClass(Object o) {
    // first find the metaClass
    MetaClass metaClass = getMetaClass(o.getClass());
    
    // now ask the property metaClass to the metaClass
    MOPResult result = metaClass.mopGetProperty(new MOPPropertyEvent(RT.class, true, null, null, false,
        "metaClass", new FunctionType(RT.getMetaClass(MetaClass.class))));
    
    assert !Failures.isFailure(result.getTarget());
    
    if (result.getTarget().getFunctionType().getReturnType() != RT.getMetaClass(MetaClass.class)) {
      // interropt with already existing Groovy v1 object
      // FIXME revisit
      return metaClass;
    }
    
    try {
      return (MetaClass)result.getTarget().asMethodHandle().invokeWithArguments(o);
    } catch (Throwable t) {
      throw unsafeThrow(t);
    }
  }
      
      
  public static MetaClass getMetaClass(Class<?> clazz) {
    return metaClass.get(clazz);
  }
  
  static {
    // boot sequence, initialize the universe ...
    // get object metaclass
    MetaClass objectMetaClass = getMetaClass(Object.class);
    
    // insert getMetaClass link
    ObjectMixin.__boot__((ClassMetaClass)objectMetaClass);
    
    // object metaclass is fully initialized
    // Let's rule the world !!
  }
  
  //public static void setMetaClass(Class<?> clazz, MetaClass metaClass) {
    //metaClass.set(clazz, metaClass);
  //}
  
  public static MetaClass getMetaClass(Type type) {
    if (type instanceof Class<?>)
      return getMetaClass((Class<?>)type);
    //if (type instanceof ParameterizedType)
    //  return null;  //TODO
    throw new IllegalArgumentException("invalid type "+type);
  }
  
  public static MetaClass[] getMetaClasses(Type... types) {
    MetaClass[] metaClasses = new MetaClass[types.length];
    for(int i=0; i<metaClasses.length; i++) {
      metaClasses[i] = RT.getMetaClass(types[i]);
    }
    return metaClasses;
  }
  
  public static FunctionType asFunctionType(MethodType methodType) {
    int count = methodType.parameterCount();
    MetaClass[] types = new MetaClass[count];
    for(int i=0; i<types.length; i++) {
      types[i] = RT.getMetaClass(methodType.parameterType(i));
    }
    return new FunctionType(RT.getMetaClass(methodType.returnType()), types);
  }
  
  public static FunctionType asMethodType(MethodType methodType) {
    int count = methodType.parameterCount() - 1;
    MetaClass[] types = new MetaClass[count];
    for(int i=0; i<count; i++) {
      types[i] = RT.getMetaClass(methodType.parameterType(i + 1));
    }
    return new FunctionType(RT.getMetaClass(methodType.returnType()), types);
  }
  
  public static MethodType asDynMethodType(FunctionType functionType) {
    int count = functionType.getParameterCount();
    Class<?>[] types = new Class<?>[count];
    for(int i=0; i<types.length; i++) {
      types[i] = getRawClass(functionType.getParameterType(i));
    }
    return MethodType.methodType(getRawClass(functionType.getReturnType()), types);
  }
  
  public static Class<?> getRawClass(MetaClass metaClass) {
    return (Class<?>)metaClass.getRawMetaClass().getType();
  }
  
  static class Escaper<T extends Throwable> {
    final T throwable;
    
    Escaper(T throwable) {
      this.throwable = throwable;
    }  
    
    void rethrow() throws T {
      throw throwable;
    }
  }
  
  // this is clearly unsafe, but that's exactly what we want
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static RuntimeException unsafeThrow(Throwable e) {
    ((Escaper<RuntimeException>)(Escaper)new Escaper<Throwable>(e)).rethrow();
    throw null; // never reached
  }
  
}
