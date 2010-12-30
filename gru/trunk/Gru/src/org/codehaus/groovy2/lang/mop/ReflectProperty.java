package org.codehaus.groovy2.lang.mop;

import groovy2.lang.Closure;
import groovy2.lang.MetaClass;
import groovy2.lang.Property;

public class ReflectProperty implements Property {
  private final MetaClass declaringMetaClass;
  private final int modifiers;
  private final String name;
  private final MetaClass type;
  private final Closure getter;
  private final Closure setter;
  
  public ReflectProperty(MetaClass declaringMetaClass, int modifiers, String name, MetaClass type, Closure getter, Closure setter) {
    this.declaringMetaClass = declaringMetaClass;
    this.modifiers = modifiers;
    this.name = name;
    this.type = type;
    this.getter = getter;
    this.setter = setter;
  }

  @Override
  public MetaClass getDeclaringMetaClass() {
    return declaringMetaClass;
  }
  
  @Override
  public int getModifiers() {
    return modifiers;
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
    return declaringMetaClass.toString()+"."+name+": "+type;
  }
  
  @Override
  public Closure getGetter() {
    return getter;
  }

  @Override
  public Closure getSetter() {
    return setter;
  }
}
