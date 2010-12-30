package org.codehaus.groovy2.lang.mop;

import groovy2.lang.Closure;
import groovy2.lang.MetaClass;
import groovy2.lang.Property;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.MethodType;
import java.dyn.NoAccessException;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import org.codehaus.groovy2.lang.RT;

public class InstanceProperty implements Property {
  private final IdentityHashMap<Object, Object> valueMap =
      new IdentityHashMap<Object, Object>();
  
  private final MetaClass declaringClass;
  private final String name;
  private final MetaClass type;
  
  private Closure getter;   // lazy
  private Closure setter;   // lazy
  
  private static final MethodHandle GETTER;
  private static final MethodHandle SETTER;
  static {
    try {
      GETTER = MethodHandles.lookup().findVirtual(InstanceProperty.class, "getAt",
          MethodType.methodType(Object.class, Object.class));
      SETTER = MethodHandles.lookup().findVirtual(InstanceProperty.class, "putAt",
          MethodType.methodType(void.class, Object.class, Object.class));
    } catch (NoAccessException e) {
      throw (AssertionError)new AssertionError().initCause(e);
    }
  }

  public InstanceProperty(MetaClass declaringClass, String name, MetaClass type) {
    this.declaringClass = declaringClass;
    this.name = name;
    this.type = type;
  }

  @Override
  public MetaClass getDeclaringMetaClass() {
    return declaringClass;
  }

  @Override
  public int getModifiers() {
    return Modifier.PUBLIC;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public MetaClass getType() {
    return type;
  }

  @Override
  public String toString() {
    return declaringClass.toString()+"."+name+": "+type;
  }
  
  // called using method handle
  private void putAt(Object receiver, Object value) {
    valueMap.put(receiver, value);
  }
  
  //called using method handle
  private Object getAt(Object receiver) {
    return valueMap.get(receiver);
  }
  
  @Override
  public Closure getGetter() {
    if (getter == null) {
      MethodHandle mh = GETTER.bindTo(this).asType(
          MethodType.methodType(RT.getRawClass(type), RT.getRawClass(declaringClass)));
      getter = new ReflectClosure(false, mh);
    }
    return getter;
  }

  @Override
  public Closure getSetter() {
    if (setter == null) {
      MethodHandle mh = SETTER.bindTo(this).asType(
          MethodType.methodType(void.class, RT.getRawClass(declaringClass), RT.getRawClass(type)));
      setter = new ReflectClosure(false, mh);
    }
    return setter;
  }
}
