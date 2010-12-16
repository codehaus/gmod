package org.codehaus.groovy2.lang.java;

import groovy2.lang.Attribute;
import groovy2.lang.Closure;
import groovy2.lang.MetaClass;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.NoAccessException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.codehaus.groovy2.lang.RT;

public class JVMAttribute implements Attribute {
  private final MetaClass declaringMetaClass;
  private final Field field;
  
  private Closure getter; // lazy initialized
  private Closure setter; // lazy initialized
  
  public JVMAttribute(MetaClass declaringMetaClass,Field field) {
    this.declaringMetaClass = declaringMetaClass;
    this.field = field;
  }

  @Override
  public MetaClass getDeclaringMetaClass() {
    return declaringMetaClass;
  }

  @Override
  public String getName() {
    return field.getName();
  }
  
  @Override
  public int getModifiers() {
    return field.getModifiers();
  }

  @Override
  public MetaClass getType() {
    return RT.getMetaClass(field.getType());
  }

  @Override
  public String toString() {
    return declaringMetaClass.toString()+".@"+getName()+": "+getType();
  }
  
  @Override
  public Closure getGetter() {
    if (getter == null) {
      try {
        MethodHandle mh = MethodHandles.publicLookup().unreflectGetter(field);
        if (Modifier.isStatic(field.getModifiers())) { // a static method is a class method
          mh = MethodHandles.dropArguments(mh, 0, Class.class);
        }
        getter = new JVMClosure(/*varags=*/false, mh);
      } catch (NoAccessException e) {
        throw RT.unsafeThrow(e);
      }
    }
    return getter;
  }

  @Override
  public Closure getSetter() {
    if (setter == null) {
      try {
        MethodHandle mh = MethodHandles.publicLookup().unreflectSetter(field);
        if (Modifier.isStatic(field.getModifiers())) { // a static method is a class method
          mh = MethodHandles.dropArguments(mh, 0, Class.class);
        }
        setter = new JVMClosure(/*varags=*/false, mh);
      } catch (NoAccessException e) {
        throw RT.unsafeThrow(e);
      }
    }
    return setter;
  }
}
